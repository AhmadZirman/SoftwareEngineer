[[Lecture6-DML-Data-Integrity.pdf]]

# SQL DML and Data Integrity in Practice
Lectures 1 to 5 only **read** data with `SELECT`. A wrong read costs nothing. This lecture **changes** data, where a mistake is real. It follows the life of one record: it arrives, it changes, it leaves, and sometimes it leaves while others still point to it.

**DML** (Data Manipulation Language) is the part of SQL that changes data: `INSERT`, `UPDATE`, `DELETE`.

## Course Framing: What Lasts vs. What Changes
From Lecture 6 the course separates two things:
- **What lasts**: the problems, the understanding and the judgment. What a database exists to solve and how to decide well
- **What changes**: the SQL syntax, which AI can increasingly write once you can say exactly what you need

Every slide now carries one of three labels:

| Label | Holds |
|---|---|
| 1 Key point | the real problem and how databases handle it in general |
| 2 Example | the library, as the bridge to a real scenario |
| 3 Practice | the concrete SQL |

Each need splits into **what the database can provide** and **what you must decide**. The second column is the exam-relevant judgment part.

Lectures 1 to 8 are databases, 9 to 11 software architecture, 12 Q&A.

## Recap: The Library Schema
```
Book: {[ book_id: int, title: string, isbn: string, publication_year: int ]}
Author: {[ author_id: int, first_name: string, last_name: string, birth_year: int ]}
Member: {[ member_id: int, first_name: string, last_name: string, email: string, join_date: date, referred_by → Member ]}
Copy: {[ book_id → Book, copy_number: int, status: string ]}
Loan: {[ loan_id: int, member_id → Member, (book_id, copy_number) → Copy, loan_date: date, due_date: date, return_date: date ]}
```

Plus `BookGenre` and `BookAuthor`. `Copy` is the weak entity, `status` is `'available'` or `'on loan'`.

## The Four Needs
| Need | Database provides | You decide |
|---|---|---|
| 1 Something new arrives | 1.1 add one, 1.2 add many | 1.3 already there: whose data wins? 1.4 unclear data: never guess |
| 2 Something changes | 2.1 change named rows, 2.2 rows found via another table | 2.3 undo a mistake: check the count first |
| 3 Something leaves | 3.1 remove named rows, 3.3 empty a table | 3.2 delete it or only mark it? |
| 4 Leaves, but others depend on it | 4.1 stop, 4.2 cascade, 4.3 clear, 4.4 point elsewhere | which rule fits each reference? |

These four steps are the life of any record in any system, not just PostgreSQL.

## Need 1: Something New Arrives
Lisa signs up at the desk. The city library sends its member list. A spreadsheet from 2009 must be imported.

| Need | SQL | What it does |
|---|---|---|
| 1.1 Add one record | `INSERT` | one row with values for the named columns |
| 1.2 Add many | multi-row `INSERT`, `INSERT ... SELECT`, `COPY` | many rows in one command |
| 1.3 Already there | `ON CONFLICT` | decide in advance: skip or update |
| 1.4 File from elsewhere | `COPY ... WITH (ENCODING ..., NULL ...)` | tell the database the file's rules |

**Cannot do for you**: decide whose data is right, or read `03/04/2024`.

### 1.1 INSERT: Single Row
```sql title:Insert-Single
INSERT INTO Author (first_name, last_name, birth_year)  -- name the columns
VALUES ('Ursula', 'Le Guin', 1929);                     -- values in the same order
```

- **Always name the columns.** Without a column list the values must follow the table's own column order, and the statement breaks when someone adds a column
- A column left out gets its **default**. No default means NULL. NULL in a `NOT NULL` column means the insert fails

### 1.2 Multi-Row INSERT
```sql title:Insert-Multi
INSERT INTO Author (first_name, last_name, birth_year) VALUES
    ('Ursula',  'Le Guin', 1929),
    ('Octavia', 'Butler',  1947),
    ('Ted',     'Chiang',  1967);
-- INSERT 0 3: three rows added, the 0 is a legacy field and always 0
```

- **One round trip** instead of three: send, wait, answer. Adds up on real loads
- **All or nothing**: one bad row and none are added

### 1.2 INSERT ... SELECT
The rows come from a query instead of typed values. The data never leaves the database.

```sql title:Insert-Select
-- Tag every book from before 1950 as Classic
INSERT INTO BookGenre (book_id, genre)
SELECT book_id, 'Classic'   -- constant column: same value on every row
FROM Book
WHERE publication_year < 1950;
```

Same statement for 20 rows or 2000. But **one duplicate stops everything**: 17 of the 20 books are already tagged and `(book_id, genre)` is the primary key, so this fails and adds nothing. Fixed by `ON CONFLICT`.

### SERIAL and Auto-Generated Keys
```sql title:Serial
CREATE TABLE Author (
    author_id SERIAL PRIMARY KEY,  -- the database fills this in
    ...
);
```

- Leave the SERIAL column **out** of the insert. Each new row gets the next number
- Each number is **used once**. Two concurrent inserts never collide, and a number is never reused, not even after a `DELETE` or a `ROLLBACK`. Gaps in the sequence are normal
- `SERIAL` is shorthand for `INTEGER` plus a **sequence** (a counter). The SQL-standard form is `GENERATED ALWAYS AS IDENTITY`. The course uses SERIAL

### RETURNING
Hands back values from the new row in the same statement.

```sql title:Returning
INSERT INTO Member (first_name, last_name, email)
VALUES ('Lisa', 'Berg', 'lisa@example.com')
RETURNING member_id, join_date;  -- 51 | today
```

- **No second query.** Otherwise: insert, then a `SELECT` to find the number
- Any column, including defaults. `join_date` was filled in by its default and shows up here
- Multi-row inserts return one line per row. Also works on `UPDATE` and `DELETE` to see exactly what changed

### 1.3 ON CONFLICT DO NOTHING
```sql title:On-Conflict-Nothing
INSERT INTO BookGenre (book_id, genre)
SELECT book_id, 'Classic' FROM Book WHERE publication_year < 1950
ON CONFLICT (book_id, genre) DO NOTHING;  -- INSERT 0 3: only the missing tags
```

- `(book_id, genre)` is the key to check. It must be a primary key or have a `UNIQUE` constraint
- `DO NOTHING` skips the clashing row and carries on. **Keeps ours**

### 1.3 ON CONFLICT DO UPDATE: Upsert
**Upsert** = update + insert: insert the row, or update it if it already exists.

```sql title:Upsert
INSERT INTO Member (first_name, last_name, email, join_date)
VALUES ('Daniel', 'Petersen', 'daniel.petersen@example.dk', '2019-05-02')
ON CONFLICT (email)                         -- email is UNIQUE
DO UPDATE SET join_date = EXCLUDED.join_date;
```

- **EXCLUDED** is the incoming row, the one that could not be inserted
- Only the columns listed in `SET` change. Everything else stays ours

**The decision: whose data wins, column by column.**
- The city's `join_date` is when Daniel joined the city library, not us. So `DO UPDATE` on this column is the **wrong** choice
- A corrected surname from the city? Probably theirs

```sql title:Upsert-Per-Column
-- Take their surname, keep our join_date
ON CONFLICT (email)
DO UPDATE SET last_name = EXCLUDED.last_name;
```

### 1.4 COPY: Bulk Import
```sql title:Copy
COPY Member (first_name, last_name, email, join_date)
FROM '/path/to/new_members.csv'
WITH (FORMAT csv, HEADER true);  -- HEADER true skips the column-name line
```

- **Much faster** than one `INSERT` per row
- `COPY` reads the file **on the database server**. For a file on your own machine use psql's `\copy`: same options, reads where psql runs. With Docker, the server is the container, so a path on your laptop does not exist for `COPY`

### Typical CSV Problems
```MD
first_name,last_name,email,join_date
Anna,Nielsen,anna@example.com,2024-03-15       -- fine
S<f8>ren,Kj<e6>r,soren@example.com,15/03/2024  -- Latin-1 bytes, DD/MM date
Line,Thomsen,NULL,2024-03-16                   -- the word NULL, not a missing value
Mikkel,,mikkel@example.com,                    -- empty last_name and join_date
Nikolaj,Lund,nikolaj@example.com,03/04/2024    -- 3 April or 4 March?
```

| Row | Problem |
|---|---|
| 2 | `<f8>` is ø and `<e6>` is æ in **Latin-1**. The database expects **UTF-8**, so `COPY` errors |
| 3 | The text `NULL` is four letters. A search for members without email misses Line |
| 4 | `last_name` is `NOT NULL`, cannot go in as is |
| 5 | Ambiguous date. The file cannot tell you |

### Fixing It: Staging Table
Load into a **staging table** of all-`TEXT` columns first, so no odd value can stop the load. Fix what is clear, then move rows into `Member`.

```sql title:Staging
CREATE TABLE staging_members (
    first_name TEXT, last_name TEXT, email TEXT, join_date TEXT  -- everything TEXT
);

COPY staging_members FROM '/path/to/new_members.csv'
WITH (FORMAT csv, HEADER true,
      ENCODING 'LATIN1',  -- read bytes as Latin-1 so ø and æ arrive correctly
      NULL 'NULL');       -- the text NULL becomes a real NULL
```

- `NULL 'NULL'` has a side effect: an **empty field is no longer NULL** but an empty string `''`
- Options fix **the whole file**, not single rows
- **No option can fix `03/04/2024`.** Set the row aside and ask the sender. **Never guess**

```sql title:Staging-Move
-- Move only the clear rows, leave the rest for the sender
INSERT INTO Member (first_name, last_name, email, join_date)
SELECT first_name, last_name, email, join_date::DATE   -- cast TEXT to DATE
FROM staging_members
WHERE last_name <> ''                                   -- '' passes NOT NULL, so test it
  AND join_date ~ '^\d{4}-\d{2}-\d{2}$';                -- only unambiguous ISO dates
```

## Need 2: Something Changes
A copy of *Dune* is lost. A colleague changes data but forgets to say which rows.

| Need | SQL | What it does |
|---|---|---|
| 2.1 Change named rows | `UPDATE ... SET ... WHERE` | exactly the rows `WHERE` matches. No WHERE: every row |
| 2.2 Rows via another table | `UPDATE ... FROM` | joins in the table that knows, e.g. the title |
| 2.3 Undo a mistake | `BEGIN ... ROLLBACK` | nothing is final until `COMMIT` |

**Cannot do for you**: notice that you forgot the `WHERE`. It changes what you tell it to.

### 2.1 UPDATE
```sql title:Update
UPDATE Copy
SET status = 'lost'                       -- SET: what changes
WHERE book_id = 16 AND copy_number = 1;   -- WHERE: which rows
```

The `WHERE` works exactly as in a `SELECT`. **Run the same WHERE as a SELECT first** to see which rows it hits.

### 2.2 UPDATE ... FROM
`Copy` has no title. `FROM Book` brings in the table that has it.

```sql title:Update-From
UPDATE Copy c
SET status = 'lost'
FROM Book b
WHERE c.book_id = b.book_id   -- join condition goes in WHERE
  AND b.title = 'Dune';       -- UPDATE 2
```

Forget the join condition and every copy matches, the same accidental cross join as Lecture 5.

### 2.3 UPDATE Without WHERE and ROLLBACK
```sql title:Missing-Where
BEGIN;                                             -- start a draft
UPDATE Copy SET status = 'lost';                   -- UPDATE 157: every copy
SELECT count(*) FROM Copy WHERE status = 'lost';   -- 157
ROLLBACK;                                          -- throw the draft away
SELECT count(*) FROM Copy WHERE status = 'lost';   -- 3, the ones lost before
```

- **The row count is the warning sign.** You meant one copy, the database says 157
- `ROLLBACK` undoes **everything since `BEGIN`**
- **Without `BEGIN`, every statement is final the moment it runs** (autocommit). Only a backup helps then

### The Safe Routine
```sql title:Safe-Routine
BEGIN;
UPDATE Copy SET status = 'lost'
WHERE book_id = 16 AND copy_number = 1;
-- UPDATE 1 <- the number you expected?
COMMIT;      -- yes: make it final
-- ROLLBACK; -- no: throw it away
```

**Habit**: `BEGIN` before any hand-typed `UPDATE`, `DELETE` or `TRUNCATE`. Check the count, then `COMMIT` or `ROLLBACK`. Transactions in full are Lecture 7.

## Need 3: Something Leaves
| Need | SQL | What it does |
|---|---|---|
| 3.1 Remove named rows | `DELETE ... WHERE` | exactly the matching rows. No WHERE: every row |
| 3.2 Delete or mark? | `ON DELETE RESTRICT`, `UPDATE ... SET status` | refuses rows others point at, so you mark instead |
| 3.3 Empty a table | `TRUNCATE` | all rows at once, much faster, own numbering rule |

**Cannot do for you**: decide whether a withdrawn book should be deleted or marked.

### 3.1 DELETE
```sql title:Delete
DELETE FROM Copy WHERE book_id = 6;  -- DELETE 2: the never-borrowed Guards! Guards!

BEGIN;
DELETE FROM BookGenre;  -- DELETE 139: every genre tag
ROLLBACK;               -- saved only by the rollback
```

- `DELETE ... RETURNING *` shows exactly which rows were removed
- A **foreign key can block a delete**. Even `DELETE FROM Copy` as a whole is refused while `Loan` points at copies

### 3.2 Delete or Mark?
```sql title:Delete-Refused
DELETE FROM Copy WHERE book_id = 16;
-- ERROR: update or delete on table "copy" violates foreign key constraint
--        "loan_book_id_copy_number_fkey" on table "loan"
-- DETAIL: Key (book_id, copy_number)=(16, 1) is still referenced from table "loan".
```

```sql title:Mark-Instead
UPDATE Copy SET status = 'withdrawn' WHERE book_id = 16;  -- UPDATE 2
SELECT count(*) FROM Loan WHERE book_id = 16;             -- 6 loans, still there
```

- **The refusal is a message**: the copies carry history
- **Marking** (a **soft delete**) keeps that history. The copy leaves the shelf, every loan still points at a real copy
- **Delete only what nobody points at**

### 3.3 TRUNCATE
```sql title:Truncate-Counter
-- demo_seq: id SERIAL, rows a, b, c
DELETE FROM demo_seq;                     -- 3 rows removed
INSERT INTO demo_seq (val) VALUES ('d');  -- id = 4, counter keeps going

TRUNCATE TABLE demo_seq;                  -- no RESTART IDENTITY
INSERT INTO demo_seq (val) VALUES ('e');  -- id = 5, NOT 1

TRUNCATE TABLE demo_seq RESTART IDENTITY; -- now the counter restarts at 1
```

**TRUNCATE does not reset the counter** unless you say `RESTART IDENTITY`.

| `DELETE` (no WHERE) | `TRUNCATE` |
|---|---|
| removes rows one by one | drops the storage at once, much faster |
| runs row-level `DELETE` triggers | row-level triggers do **not** run |
| locks the rows it deletes | locks the whole table |
| keeps the SERIAL counter | same, unless `RESTART IDENTITY` |
| can take a `WHERE` | always every row |

Both can be rolled back in PostgreSQL.

## Need 4: Something Leaves That Others Depend On
Freja (11) asks to have her account deleted. Maria (21) was referred by her, and the chain goes on.

```MD
member_id | name            | referred_by
1         | Daniel Petersen | NULL         -- founder
11        | Freja Andersen  | 1
21        | Maria Andersen  | 11           -- referred by Freja
31        | Maria Poulsen   | 21
41        | Peter Olsen     | 31
```

| Need | `ON DELETE` | Effect on Maria | Use when |
|---|---|---|---|
| 4.1 Stop and tell you | `RESTRICT` / `NO ACTION` | Freja stays, error names Maria | the reference is **history** (a loan) |
| 4.2 Delete dependants | `CASCADE` | Maria goes, and everyone below her | the dependant is meaningless alone (a genre tag) |
| 4.3 Clear the reference | `SET NULL` | Maria stays, referred by nobody | the reference is only a note (a referral) |
| 4.4 Point elsewhere | `SET DEFAULT` | Maria points at member 0 "(deleted)" | you want to record that it was deleted |

The rule is set **once per foreign key**, in the schema, and applied on every delete.

**Cannot do for you**: pick the rule. All four are legal, and the wrong one silently erases a referral chain.

### Changing the Rule on an Existing FK
A constraint cannot be edited. Drop it and add it again.

```sql title:Change-FK-Rule
ALTER TABLE Member DROP CONSTRAINT member_referred_by_fkey;
ALTER TABLE Member ADD CONSTRAINT member_referred_by_fkey
    FOREIGN KEY (referred_by) REFERENCES Member(member_id)
    ON DELETE SET NULL;  -- the new rule
```

PostgreSQL names FK constraints `<table>_<column>_fkey` by default.

### 4.1 NO ACTION: the Default
```sql title:No-Action
BEGIN;
-- FK with no ON DELETE clause = NO ACTION
DELETE FROM Loan WHERE member_id = 11;    -- DELETE 5, clear her loans first
DELETE FROM Member WHERE member_id = 11;
-- ERROR: ... violates foreign key constraint "member_referred_by_fkey"
-- DETAIL: Key (member_id)=(11) is still referenced from table "member".
ROLLBACK;                                 -- the 5 loans come back too
```

Loans had to go first because `Loan.member_id` is `RESTRICT`.

### 4.2 CASCADE
```sql title:Cascade
BEGIN;
DELETE FROM Loan WHERE member_id IN (11, 21, 31, 41);  -- whole chain's loans first
-- ... re-add FK with ON DELETE CASCADE ...
DELETE FROM Member WHERE member_id = 11;
SELECT member_id FROM Member WHERE member_id IN (11, 21, 31, 41);  -- 0 rows
ROLLBACK;
```

**Deleting one member deleted four**, without a warning. Cascades follow the chain recursively.

Why the loans had to go first: a cascade that reaches a row protected by a `RESTRICT` FK fails, and the **whole** delete is rolled back. One RESTRICT anywhere in the chain blocks the entire cascade.

### 4.3 SET NULL
```sql title:Set-Null
BEGIN;
DELETE FROM Loan WHERE member_id = 11;
-- ... re-add FK with ON DELETE SET NULL ...
DELETE FROM Member WHERE member_id = 11;
SELECT member_id, referred_by FROM Member WHERE member_id = 21;  -- 21 | NULL
ROLLBACK;
```

Only Maria's `referred_by` becomes NULL. 31 and 41 are untouched. The right answer for a referral. Requires the FK column to be **nullable**.

### 4.4 SET DEFAULT
```sql title:Set-Default
BEGIN;
DELETE FROM Loan WHERE member_id = 11;
INSERT INTO Member (member_id, first_name, last_name, email)
VALUES (0, '(deleted)', '(deleted)', 'deleted@example.dk');     -- placeholder row
ALTER TABLE Member ALTER COLUMN referred_by SET DEFAULT 0;      -- the default
-- ... re-add FK with ON DELETE SET DEFAULT ...
DELETE FROM Member WHERE member_id = 11;
SELECT member_id, referred_by FROM Member WHERE member_id = 21;  -- 21 | 0
ROLLBACK;
```

- The column needs a `DEFAULT`
- **The default row must exist**, or the FK refuses the delete. SET DEFAULT is still checked against the foreign key

### RESTRICT vs. NO ACTION
Both stop the delete. The only difference is **when** they check.

```sql title:Restrict-vs-No-Action
REFERENCES Member(member_id) ON DELETE RESTRICT    -- checks at once, always
REFERENCES Member(member_id) ON DELETE NO ACTION   -- checks at once by default
    DEFERRABLE INITIALLY DEFERRED                  -- or only at COMMIT
```

- `RESTRICT`: immediately, always. `Loan`'s references to `Member` and `Copy` use it
- `NO ACTION`: can be **deferred** to `COMMIT`. Useful when a transaction breaks a reference and repairs it before it ends. It is the **default**

### TRUNCATE and Foreign Keys
```sql title:Truncate-FK
TRUNCATE TABLE Book;
-- ERROR: cannot truncate a table referenced in a foreign key constraint
-- DETAIL: Table "bookgenre" references "book".
-- HINT: Truncate table "bookgenre" at the same time, or use TRUNCATE ... CASCADE.
```

- **TRUNCATE ignores the `ON DELETE` rules.** It does not go row by row. If any table references this one, it refuses, whatever the FK's rule
- `TRUNCATE Book CASCADE` empties every referencing table, recursively: `BookGenre`, `Copy`, `BookAuthor`, and through `Copy` also `Loan`. It empties `Loan` even though that FK is RESTRICT. Powerful and dangerous

## Efficiency: Views
### CREATE VIEW
A **view** is a saved query used as a virtual table.

```sql title:View-ActiveLoan
CREATE VIEW ActiveLoan AS
SELECT l.loan_id, m.first_name, m.last_name, b.title,
       l.loan_date, l.due_date
FROM Loan l
JOIN Member m ON l.member_id = m.member_id
JOIN Book b   ON l.book_id   = b.book_id
WHERE l.return_date IS NULL;   -- only open loans
```

```sql title:View-Use
SELECT first_name, last_name, title
FROM ActiveLoan                -- used like a table
ORDER BY due_date
LIMIT 3;                       -- the three loans due first
```

- **Stores the question, not the answers.** Every query reruns the join on today's data. Return a book and it drops out at once
- `WHERE`, `ORDER BY` and further joins all work on it
- A long join gets a short name, and a frequent question lives in **one place**

### COUNT FILTER
```sql title:View-BookAvailability
CREATE VIEW BookAvailability AS
SELECT b.book_id, b.title,
       COUNT(*) AS total_copies,
       COUNT(*) FILTER (WHERE c.status = 'available') AS available_copies
FROM Book b
JOIN Copy c ON b.book_id = c.book_id
GROUP BY b.book_id, b.title;

SELECT * FROM BookAvailability WHERE available_copies = 0;  -- 20 books
```

**COUNT(\*) FILTER (WHERE ...)** counts only the rows matching the condition, inside the same group. No subquery needed.

### What Views Are Not
- **Not a copy**: if the join is slow, the view is just as slow
- **Not always writable**: a simple view over one table can be updated. Joins or aggregates cannot, not without extra work
- **Not a snapshot**: **materialized views** store a result and must be refreshed. Different tool, not covered

## Exercise Sheet 6 Rule
Every statement comes with three lines:
1. Which Index row (need) it belongs to
2. Why it is right for your schema
3. How you checked it: a query, a row count, a `BEGIN ... ROLLBACK`

## Common Traps
- `UPDATE` or `DELETE` without `WHERE` hits every row. Run the WHERE as a SELECT first, wrap in `BEGIN`
- Without `BEGIN`, autocommit makes every statement final at once
- `UPDATE ... FROM` without the join condition updates every row
- Inserting without a column list breaks when the table gains a column
- One duplicate key fails the whole multi-row insert. Use `ON CONFLICT`
- `ON CONFLICT (col)` needs a PK or UNIQUE constraint on exactly those columns
- `DO UPDATE` is a per-column decision. Do not blindly take every incoming value
- SERIAL numbers are never reused. Gaps after DELETE or ROLLBACK are normal
- `TRUNCATE` keeps the counter unless `RESTART IDENTITY`
- `TRUNCATE` ignores `ON DELETE` rules. `TRUNCATE ... CASCADE` empties referencing tables even when their FK is RESTRICT
- `CASCADE` can wipe a whole chain silently. One RESTRICT anywhere in the chain fails the entire delete
- `SET DEFAULT` needs the default row to exist. `SET NULL` needs a nullable column
- `COPY` reads from the server, `\copy` from where psql runs
- `NULL 'NULL'` turns empty fields into `''`, and `''` passes `NOT NULL`. Check for it explicitly before moving rows out of staging
- The text `NULL` in a file is a four-letter string unless you tell `COPY` otherwise
- Ambiguous dates are never guessed. Set aside and ask
- A view with an INNER JOIN drops rows with no match. In `BookAvailability`, a book with zero copies disappears instead of showing 0. Use `LEFT JOIN` and `COUNT(c.copy_number)` if those books matter