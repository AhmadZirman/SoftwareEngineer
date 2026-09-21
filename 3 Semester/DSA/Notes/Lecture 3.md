[[Lecture3-ER-to-SQL.pdf]]

# From ER to Relational Schema: SQL DDL
Lecture 2 designed the library as an ER diagram with no SQL. This lecture turns that diagram into real tables using a fixed set of translation rules, then implements them in SQL DDL with every constraint intact.

**DDL** (Data Definition Language) is the part of SQL that defines structure: `CREATE TABLE`, `ALTER TABLE`, `DROP TABLE`. It stands in contrast to the queries from Lecture 1 (`SELECT`, `INSERT`), which work on the data itself.

## Recap: The Library ER Model
- **Entities**: `Author`, `Book`, `Copy` (weak), `Member`
- **Writes** (Book - Author): M:N, total participation on both sides
- **Has** (Book - Copy): 1:N, identifying relationship. Copy total, Book partial
- **Borrows** (Member - Copy): M:N, both sides partial, carries `loan_date`, `due_date`, `return_date` and derived `is_overdue`
- `Book.genre` is multi-valued, `Author.name` is composite

An ER diagram is DBMS-independent. It says nothing about how PostgreSQL stores the data. Cardinality and participation are what decide how each relationship gets translated.

## Translation Rules
Five rules cover every ER diagram in this course.

### Rule 1: Entity → Table
Every entity type becomes a table. Every attribute becomes a column. The identifying attribute becomes the `PRIMARY KEY`.

A composite attribute is flattened into its parts. `Author.name (first_name, last_name)` becomes two plain columns.

```sql title:Rule1-Author
CREATE TABLE Author (
    author_id  SERIAL PRIMARY KEY,   -- identifying attribute from the ER diagram
    first_name VARCHAR(50) NOT NULL, -- composite "name" split into two columns
    last_name  VARCHAR(50) NOT NULL,
    birth_year INTEGER               -- only the year is known, so no DATE
);
```

### Rule 2: 1:N Relationship → Foreign Key on the "Many" Side
A 1:N relationship becomes a foreign key column on the "many" side, referencing the primary key of the "one" side. No new table is needed.

Rule of thumb: **the crow's foot end is where the FK lives**. Same idea as `Student.programme_id` in Lecture 1.

```sql title:Rule2-Member-Loan
-- inside Loan: one member has many loans, each loan has exactly one member
member_id INTEGER NOT NULL REFERENCES Member(member_id) ON DELETE RESTRICT
```

This single line is the entire translation of the relationship.

### Rule 3: M:N Relationship → Junction Table
One column cannot point at many rows, so neither side of an M:N relationship can hold the foreign key. The fix is a **junction table** in the middle with a foreign key to each side.

The junction table's primary key is usually the pair of foreign keys together. This composite PK also acts as a constraint, since it forbids recording the same pair twice.

```sql title:Rule3-BookAuthor
-- Writes (M:N, Book <-> Author) becomes its own table
CREATE TABLE BookAuthor (
    book_id   INTEGER NOT NULL REFERENCES Book(book_id)     ON DELETE CASCADE,
    author_id INTEGER NOT NULL REFERENCES Author(author_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id) -- same book-author pair can't appear twice
);
```

Exception: if the pair can legitimately repeat, the pair cannot be the PK. See [[#Why Loan Needs a Surrogate Key]].

### Rule 4: 1:1 Relationship → FK + UNIQUE
A 1:1 relationship is a Rule 2 foreign key plus `UNIQUE` on the FK column. `UNIQUE` collapses "many" down to "at most one".

```sql title:Rule4-LibraryCard
-- hypothetical, not part of the library schema
CREATE TABLE LibraryCard (
    card_id   SERIAL PRIMARY KEY,
    member_id INTEGER UNIQUE REFERENCES Member(member_id) -- UNIQUE makes it 1:1
);
```

```MD
member_id INTEGER REFERENCES ...        // 1:N, one member can have many cards
member_id INTEGER UNIQUE REFERENCES ... // 1:1, one member has at most one card
```

`UNIQUE` is the entire difference between 1:N and 1:1 in SQL.

### Rule 5: Weak Entity → Composite Primary Key
A weak entity's partial key is not unique on its own. Its table's primary key is therefore **the owner's PK (as an FK) plus the partial key**.

```sql title:Rule5-Copy
-- Copy is a weak entity identified by (book_id, copy_number)
CREATE TABLE Copy (
    book_id     INTEGER NOT NULL REFERENCES Book(book_id) ON DELETE CASCADE,
    copy_number INTEGER NOT NULL,  -- partial key, only unique within one book
    status      VARCHAR(20) NOT NULL DEFAULT 'available',
    PRIMARY KEY (book_id, copy_number)
);
```

`book_id` does double duty. It is both a foreign key (link to the owner) and part of the primary key (gives Copy its identity). That double duty is the signature of a weak entity in SQL.

### Multi-Valued Attributes → Own Table
Not one of the five rules, but the same instinct. `Book.genre` can hold several values, so it cannot be a single column. It becomes its own table with an FK back to the owner.

```sql title:BookGenre
-- Book.genre was multi-valued in the ER model
CREATE TABLE BookGenre (
    book_id INTEGER NOT NULL REFERENCES Book(book_id) ON DELETE CASCADE,
    genre   VARCHAR(30) NOT NULL, -- a value, not an entity, so no second FK
    PRIMARY KEY (book_id, genre)
);
```

Shaped like a junction table, but with only one real foreign key.

### Limitation: Total Participation Can't Be Enforced
`Writes` has total participation on both sides (every book has at least one author and vice versa). Plain SQL cannot enforce that.

- `FOREIGN KEY` and `NOT NULL` express **"at most one"** cleanly
- They cannot express **"at least one"** without triggers or application-level checks (out of scope)
- `BookAuthor` will happily accept a book with zero rows in it

This is a known limitation of the relational model, not a design mistake.

### Translation Rules Recap
| ER construct | Relational translation |
| --- | --- |
| Entity | Table, one column per attribute |
| Composite attribute | One column per part |
| 1:N relationship | FK on the "many" side |
| M:N relationship | Junction table, FK to each side |
| 1:1 relationship | FK + `UNIQUE` |
| Weak entity | Composite PK: owner's PK + partial key |
| Multi-valued attribute | Own table, FK back to the owner |
| Derived attribute | No column, computed at query time |

## SQL Data Types
The declared type is what gives **domain integrity** (Lecture 1). A value that doesn't match the type is rejected before it ever reaches the table.

- Too generic (everything as text) throws away that safety net
- Too narrow rejects legitimate data

Choosing types is part of translating the diagram, not an afterthought.

### Numeric Types
- **`INTEGER`** / `INT`: whole numbers up to about ±2.1 billion. Used for most PKs and FKs
- **`BIGINT`**: much larger range. For tables that could realistically exceed `INTEGER` (e.g. high-volume logs)
- **`SERIAL`**: `INTEGER` plus an auto-incrementing sequence. Used for surrogate PKs
- **`NUMERIC(p, s)`**: exact fixed-point decimal. `p` = total digits, `s` = digits after the decimal point. Never rounds unexpectedly, so it is the right type for money

```MD
amount NUMERIC(6, 2) // max 9999.99, 6 digits total, 2 after the point
```

`SERIAL` is only for the PK itself. A foreign key pointing at a `SERIAL` column is plain `INTEGER`, since it must not generate its own values.

### Text Types
- **`VARCHAR(n)`**: variable-length text capped at `n` characters. Use when a sane maximum exists (`title VARCHAR(200)`, `email VARCHAR(100)`)
- **`TEXT`**: no cap. Use for free-form content with no natural limit (e.g. a book synopsis)

PostgreSQL stores both the same way internally. The cap is a **data-quality choice, not a performance one**.

### Date, Time and Boolean Types
- **`DATE`**: calendar date, no time of day (`loan_date`, `due_date`, `join_date`)
- **`TIMESTAMP`**: date and time (e.g. a `created_at` audit column)
- **`BOOLEAN`**: true/false

`Copy.status` is not `BOOLEAN` because it needs more than two states ("available", "on loan", "lost").

## CREATE TABLE Syntax
```sql title:CREATE-TABLE-syntax
CREATE TABLE table_name (
    column1 TYPE [constraints],
    column2 TYPE [constraints],
    ...
);
```

Each column is a name, a type, then zero or more constraints.

## Constraints
A type only checks shape. `VARCHAR` doesn't stop an empty string and `INTEGER` doesn't stop a duplicate. **Constraints** enforce the rules the ER diagram promised.

Six kinds: `PRIMARY KEY`, `FOREIGN KEY ... REFERENCES`, `NOT NULL`, `UNIQUE`, `CHECK`, `DEFAULT`.

#### PRIMARY KEY
Enforces **entity integrity**: unique and never `NULL`. Can be one column or several.

```sql title:PRIMARY-KEY
author_id SERIAL PRIMARY KEY          -- single column, written inline
PRIMARY KEY (book_id, copy_number)    -- composite, written as its own line
```

The composite form goes on its own line after all the columns it covers are declared.

#### FOREIGN KEY ... REFERENCES
Enforces **referential integrity**: the value must match an existing row in the referenced table, or be `NULL`.

```sql title:FOREIGN-KEY
-- inline form, single column
member_id INTEGER NOT NULL REFERENCES Member(member_id) ON DELETE RESTRICT

-- separate line form, required when the key spans several columns
FOREIGN KEY (book_id, copy_number)
    REFERENCES Copy(book_id, copy_number) ON DELETE RESTRICT
```

#### NOT NULL and UNIQUE
- **`NOT NULL`**: every row must supply a value (`Book.title`, `Member.email`)
- **`UNIQUE`**: no two rows may share the value, but `NULL` is still allowed since `NULL` doesn't count as a duplicate

Combine both when a value is required and must be unique:

```MD
isbn  VARCHAR(13) UNIQUE           // may be missing, but never duplicated
email VARCHAR(100) UNIQUE NOT NULL // required and never duplicated
```

`isbn` is not `NOT NULL` because older or informally catalogued books may not have one.

#### CHECK
An arbitrary boolean condition every row must satisfy. Covers rules that don't fit the other constraint kinds.

```sql title:CHECK
-- a book can't be returned before it was borrowed
CHECK (return_date IS NULL OR return_date >= loan_date)
```

```MD
return_date IS NULL          // loan still open, nothing to compare
return_date >= loan_date     // loan closed, dates must be in order
```

#### DEFAULT
Supplies a value automatically when `INSERT` omits the column.

```sql title:DEFAULT
join_date DATE NOT NULL DEFAULT CURRENT_DATE
status VARCHAR(20) NOT NULL DEFAULT 'available'
```

`CURRENT_DATE` is evaluated **at insert time**, not when the table was created.

## ALTER TABLE and DROP TABLE
Schemas change after creation. `ALTER TABLE` edits an existing table in place.

```sql title:ALTER-TABLE
ALTER TABLE Book ADD COLUMN language VARCHAR(20);  -- existing rows get NULL (or the DEFAULT)
ALTER TABLE Member DROP COLUMN join_date;          -- column data deleted permanently

ALTER TABLE Book
    ADD CONSTRAINT book_isbn_unique UNIQUE (isbn); -- add a constraint after the fact
```

- Adding a constraint **fails immediately** if existing data already violates it (e.g. two books sharing an `isbn`)
- Naming the constraint (`book_isbn_unique`) makes it easy to drop or reference later

```sql title:DROP-TABLE
DROP TABLE IF EXISTS BookGenre;
```

- Deletes the table and all its data, irreversibly
- `IF EXISTS` avoids an error if the table is already gone. Useful in scripts you re-run
- `DROP TABLE ... CASCADE` drops a table that others still reference. **Not the same thing** as `ON DELETE CASCADE`

## Cascade Rules
When a referenced row is deleted, the rows pointing at it need a policy. `ON DELETE` sets that policy per foreign key.

```MD
DELETE FROM Book WHERE book_id = 3; // but Copy, BookGenre, BookAuthor still point at book 3
```

#### ON DELETE CASCADE
Delete the referencing rows too. Use when the child **cannot meaningfully outlive** the parent.

Used by `BookGenre`, `Copy` and `BookAuthor` toward `Book`. A genre entry, a physical copy or an authorship record means nothing without its book.

#### ON DELETE RESTRICT
Refuse the delete while referencing rows exist. Use when the child is **history that must not disappear silently**.

Used by `Loan` toward both `Member` and `Copy`. The librarian must deal with the loans explicitly before the member or copy can be deleted.

#### ON DELETE SET NULL
Keep the referencing rows, blank out the FK column. Use for **optional** FKs where losing the link is acceptable.

```sql title:SET-NULL
-- hypothetical: deleting a staff member keeps the loan, just forgets who handled it
handled_by_staff_id INTEGER REFERENCES Staff(staff_id) ON DELETE SET NULL
```

Requires the column to allow `NULL`. Cannot be used on a `NOT NULL` FK.

#### ON DELETE SET DEFAULT
Keep the referencing rows, reset the FK to the column's `DEFAULT`. Requires a declared `DEFAULT`, typically pointing at a sentinel placeholder row.

```sql title:SET-DEFAULT
-- hypothetical: staff_id 0 = "Unassigned"
handled_by_staff_id INTEGER REFERENCES Staff(staff_id)
    DEFAULT 0 ON DELETE SET DEFAULT
```

Not used in the library schema. Inventing a fake "unknown staff" row just to avoid `NULL` is unnecessary complexity.

#### ON UPDATE
Same four options, but triggered when the **referenced key value changes**, not when the row is deleted.

```sql title:ON-UPDATE
book_id INTEGER NOT NULL REFERENCES Book(book_id)
    ON DELETE CASCADE ON UPDATE CASCADE -- if book_id 3 is renumbered, children follow
```

Rarely matters here because every PK is a `SERIAL` surrogate that never changes after insert. Matters more for **natural keys** that can be renamed (e.g. a `country_code` PK).

### Cascade Rules Recap
| Option | Effect | Used for |
| --- | --- | --- |
| `CASCADE` | Delete referencing rows too | BookGenre, Copy, BookAuthor → Book |
| `RESTRICT` | Refuse the delete | Loan → Member, Loan → Copy |
| `SET NULL` | Blank the FK column | Not used (optional FKs only) |
| `SET DEFAULT` | Reset FK to its `DEFAULT` | Not used (needs a sentinel row) |

Read the library schema as a policy: delete a book and everything that only exists because of it goes too. Delete a member or copy and PostgreSQL stops you until the loan history is handled.

## The Complete Library Schema
Tables must be created in **foreign-key order**. A table cannot reference one that doesn't exist yet.

```MD
1. Author, Book, Member        // no FKs, any order
2. BookGenre, Copy, BookAuthor // reference Book (and Author)
3. Loan                        // references Member and Copy, so Copy must exist first
```

Dropping goes in the reverse order for the same reason.

```sql title:Library-Independent-Tables
CREATE TABLE Author (
    author_id  SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    birth_year INTEGER
);

CREATE TABLE Book (
    book_id          SERIAL PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    isbn             VARCHAR(13) UNIQUE,  -- optional, but never duplicated
    publication_year INTEGER
);

CREATE TABLE Member (
    member_id  SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    join_date  DATE NOT NULL DEFAULT CURRENT_DATE
);
```

`BookGenre`, `Copy` and `BookAuthor` are exactly as shown under [[#Translation Rules]].

```sql title:Library-Loan
-- Borrows (M:N, Member <-> Copy) with relationship attributes
CREATE TABLE Loan (
    loan_id     SERIAL PRIMARY KEY,  -- surrogate key, see below
    member_id   INTEGER NOT NULL REFERENCES Member(member_id) ON DELETE RESTRICT,
    book_id     INTEGER NOT NULL,
    copy_number INTEGER NOT NULL,
    loan_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date    DATE NOT NULL,
    return_date DATE,                -- NULL while the loan is still open
    FOREIGN KEY (book_id, copy_number)
        REFERENCES Copy(book_id, copy_number) ON DELETE RESTRICT,
    CHECK (return_date IS NULL OR return_date >= loan_date)
);
```

`Loan` uses five of the six constraint kinds: `PRIMARY KEY`, `FOREIGN KEY` (inline and composite), `NOT NULL`, `DEFAULT`, `CHECK`. Only `UNIQUE` is missing.

### Why Loan Needs a Surrogate Key
Rule 3 says a junction table's PK is the pair of FKs. `Loan` breaks that pattern on purpose.

```MD
(member_id, book_id, copy_number) // looks like the natural composite key
// but a member can borrow the same copy again later
// the combination would repeat, and a PK can never repeat
loan_id SERIAL                    // every loan event gets its own identity
```

General rule: **if the relationship can happen more than once between the same instances, give the junction table a surrogate key**.

### Loan's Composite Foreign Key
A foreign key must **match the shape of the primary key it references**. `Copy` needs two columns to identify a row, so anything pointing at `Copy` also needs two columns. This is Rule 5 showing up again, now in an FK instead of a PK.

### Relation Schema Shorthand
```
Author:     {[ author_id: int, first_name: string, last_name: string, birth_year: int ]}
Book:       {[ book_id: int, title: string, isbn: string, publication_year: int ]}
Member:     {[ member_id: int, first_name: string, last_name: string, email: string, join_date: date ]}
BookGenre:  {[ book_id → Book, genre: string ]}
Copy:       {[ book_id → Book, copy_number: int, status: string ]}
BookAuthor: {[ book_id → Book, author_id → Author ]}
Loan:       {[ loan_id: int, member_id → Member, (book_id, copy_number) → Copy, loan_date: date, due_date: date, return_date: date ]}
```

Primary keys (underlined on paper): `author_id`, `book_id`, `member_id`, `(book_id, genre)`, `(book_id, copy_number)`, `(book_id, author_id)`, `loan_id`.

## Derived Attributes at Query Time
`Borrows.is_overdue` is derived, so it is **not a column**. It is computed whenever it is needed.

```sql title:is_overdue
SELECT *, (return_date IS NULL AND due_date < CURRENT_DATE) AS is_overdue
FROM Loan;
```

A stored column could not work here. PostgreSQL's `GENERATED ALWAYS AS (...) STORED` columns require an **immutable** expression, and `CURRENT_DATE` changes every day without any `UPDATE`.

## SQL Types vs. Java Types
Relevant ahead of JDBC later in the course.

| SQL type | Java type |
| --- | --- |
| `INTEGER` / `SERIAL` | `int` / `Integer` |
| `BIGINT` | `long` / `Long` |
| `VARCHAR(n)` / `TEXT` | `String` |
| `DATE` | `java.time.LocalDate` |
| `TIMESTAMP` | `java.time.LocalDateTime` |
| `BOOLEAN` | `boolean` / `Boolean` |
| `NUMERIC(p, s)` | `java.math.BigDecimal` |

Use the wrapper types (`Integer`, `Boolean`) when the column can be `NULL`, since primitives can't hold `null`.

## Common Traps
```MD
FK on the "one" side                      // wrong, the FK goes on the crow's foot side
M:N as a single FK column                 // wrong, M:N always needs a junction table
SERIAL on a foreign key column            // wrong, FKs are plain INTEGER
Weak entity with only the partial key PK  // wrong, PK = owner's PK + partial key
FK to a composite PK using one column     // wrong, FK must match the PK's shape
SET NULL on a NOT NULL column             // impossible, the column can't hold NULL
Junction PK = FK pair when pairs repeat   // wrong, use a surrogate key (Loan)
Storing a derived attribute               // goes stale, compute it at query time
Creating Loan before Copy                 // fails, create tables in FK order
Confusing DROP TABLE ... CASCADE          // drops dependent objects, not ON DELETE CASCADE
```