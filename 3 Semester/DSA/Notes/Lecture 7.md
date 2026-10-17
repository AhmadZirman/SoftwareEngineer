[[Lecture7-Transactions-ACID-Optimisation.pdf]]

# Transactions, ACID, and Basic Optimisation
Lecture 6 changed data **one statement at a time**. Real work is more complex: operations with several steps, many people writing at once, and tables that grow large. Core question: **how does a database keep data stable, accurate and efficient in complex situations?**

Same framing as Lecture 6: each need splits into what the database provides and what you must decide. See [[Lecture6-DML-Data-Integrity#Course Framing: What Lasts vs. What Changes]].

| Need | Database provides | You decide |
|---|---|---|
| 1 A complex operation, done safely | F1.1 all or nothing, F1.2 save points, F1.3 stable once committed | what goes in one transaction |
| 2 Many people work at once | F2.1 isolation levels, F2.2 row waits, F2.3 deadlock detection | isolation level, check inside the UPDATE, lock order, retry |
| 3 Large data, still fast | F3.1 indexes, F3.2 query plans | which columns deserve an index |

Needs 1 and 2 keep data **stable and accurate** (ACID). Need 3 keeps it **efficient**.

## Recap: The Library Schema
Same seven tables, plus one new table for the indexing demos:

```
Copy: {[ book_id → Book, copy_number: int, status: string ]}
Member: {[ member_id: int, first_name: string, last_name: string, email: string, join_date: date, referred_by → Member ]}
Loan: {[ loan_id: int, member_id → Member, (book_id, copy_number) → Copy, loan_date: date, due_date: date, return_date: date ]}
LoanLog: synthetic event log, 500,000 rows, from extras/sql/05_bulk_loan_log.sql
```

## Need 1: A Complex Operation, Done Safely
Even borrowing a book is **two writes**: mark the copy on loan, and record the loan.

| Function | SQL | What it does |
|---|---|---|
| F1.1 All or nothing | `BEGIN ... COMMIT / ROLLBACK` | everything between counts as one unit |
| F1.2 Go back to a save point | `SAVEPOINT`, `ROLLBACK TO SAVEPOINT` | return to a named point, keep what came before |
| F1.3 Stable once committed | `COMMIT` | returns only after the change is safely on disk |

**Cannot do for you**: design one complete operation as one transaction. What goes inside and when to `COMMIT` is your call.

### The Problem: Two Writes, One Crash
```sql title:Two-Writes-No-Transaction
UPDATE Copy SET status = 'on loan' WHERE book_id = 5 AND copy_number = 1;
-- <- crash or error here
INSERT INTO Loan (member_id, book_id, copy_number, due_date)
VALUES (12, 5, 1, CURRENT_DATE + 14);
```

Without `BEGIN`, each statement is final on its own (**autocommit**). A crash in between leaves the copy "on loan" with no loan saying who has it.

Textbook version: a **bank transfer**. Take 100 from Alice, give 100 to Bob. Crash in between and the 100 is gone.

### F1.1 BEGIN, COMMIT, ROLLBACK
A **transaction** is a unit of work: everything between `BEGIN` and `COMMIT` succeeds or fails together.

```sql title:Borrow-Transaction
BEGIN;                                       -- open the unit
UPDATE Copy SET status = 'on loan'
WHERE book_id = 5 AND copy_number = 1;
INSERT INTO Loan (member_id, book_id, copy_number, due_date)
VALUES (12, 5, 1, CURRENT_DATE + 14);
COMMIT;                                      -- keep both, together
```

```sql title:Borrow-Rollback
BEGIN;
UPDATE Copy SET status = 'on loan'
WHERE book_id = 5 AND copy_number = 1;
-- something goes wrong, the loan should not happen
ROLLBACK;                                    -- throw both away
```

- A crash before `COMMIT` and **neither** write survives, never "just the first"
- Without `BEGIN`, every statement is its own one-statement transaction. The Lecture 6 safety net was a transaction all along

### Errors Abort the Transaction
In PostgreSQL, **any error inside a transaction aborts it**. Every following statement fails with `current transaction is aborted, commands ignored until end of transaction block` until you `ROLLBACK` (or roll back to a save point). You cannot fix the failing statement and carry on.

### F1.2 SAVEPOINT
A **save point** is a named checkpoint inside a transaction. Like saving inside one level of a video game.

| Game | Task | SQL |
|---|---|---|
| Start the level | nothing is final yet | `BEGIN` |
| Save inside the level | mark a point to return to | `SAVEPOINT s` |
| Try and lose | the check says wrong | `UPDATE ...` |
| Load the save | go back to the point | `ROLLBACK TO SAVEPOINT s` |
| Try again until you win | the check says right | `UPDATE ...` |
| Clear the level | final, no going back | `COMMIT` |

```sql title:Savepoint
-- Library closed two weeks: extend loans that fall due in that window
BEGIN;
SAVEPOINT try;
UPDATE Loan SET due_date = due_date + 14
WHERE return_date IS NULL;                     -- UPDATE 63: 36 too many
ROLLBACK TO SAVEPOINT try;                     -- wrong: go back
UPDATE Loan SET due_date = due_date + 14
WHERE return_date IS NULL
  AND due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 13;  -- UPDATE 27
COMMIT;                                        -- right: make it final
```

- Work done **before** the save point stays
- Save points live **inside one transaction**. They vanish at `COMMIT` or `ROLLBACK`. A checkpoint, not a version history

### F1.3 COMMIT Means Confirmed
```sql title:Commit-Durable
COMMIT;  -- returns only after the change is written to the log on disk
```

- **Write-ahead log (WAL)**: every change is written to a log on disk **before** `COMMIT` returns. After a crash, PostgreSQL replays the log
- **"Done" means `COMMIT` returned**, not that the `INSERT` ran
- Before `COMMIT` returns, nothing is promised. After it, everything is

Log internals are 6th semester Database Systems.

## Need 2: Many People Work at Once
Need 1 made one task safe. Now many unfinished transactions run side by side. Two questions:

**1. What does each one see, and what must still hold when it writes?** Three needs:

| Need | Meaning | Library case |
|---|---|---|
| **Real-time** | I always see the latest commits | the desk lending a copy |
| **No overwrite** | one snapshot, and nobody wrote my rows since | overdue clean-up: list, count, mark lost |
| **Valid decision** | one snapshot, and nobody changed what my decision rests on | loan limit: count, then lend |

**2. What if two change the same data?** Two problems: **write conflicts** and **deadlocks**.

### Concurrency Anomalies
| Anomaly | What happens |
|---|---|
| **Dirty read** | you read another transaction's uncommitted change |
| **Non-repeatable read** | the same row read twice gives two values, because someone committed in between |
| **Phantom read** | the same query run twice returns a different **set** of rows (rows appeared or vanished) |
| **Lost update** | two transactions read a value, both write a new value based on it, one overwrites the other |
| **Write skew** | two transactions read the same data, write **different** rows, and together break a rule neither broke alone |

### F2.1 Isolation Levels
| Function | Level | Behaviour | Use for |
|---|---|---|---|
| F2.1.1 Latest commits | `READ COMMITTED` (default) | each **statement** sees what was committed when it started. Never refuses | real-time: the desk |
| F2.1.2 Snapshot + write protection | `REPEATABLE READ` | one snapshot from the first statement. Writing a row someone else changed since: refused | no overwrite: the clean-up |
| F2.1.3 Snapshot + basis protection | `SERIALIZABLE` | also tracks what each transaction **read**. Refuses one if no one-at-a-time order explains the result | valid decision: the loan limit |

```sql title:Set-Isolation
BEGIN ISOLATION LEVEL REPEATABLE READ;   -- set at the start
-- or
BEGIN;
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;  -- must be the first statement
```

**Your decision**:
- Too weak: wrong numbers
- Too strong: needless refusals and retries
- `SERIALIZABLE` only protects if **every** transaction involved uses it

### Which Level Prevents What
| Level | Dirty | Non-repeatable | Phantom | Lost update | Write skew |
|---|---|---|---|---|---|
| `READ UNCOMMITTED` | same as READ COMMITTED in PostgreSQL | | | | |
| `READ COMMITTED` | prevented | possible | possible | possible | possible |
| `REPEATABLE READ` | prevented | prevented | prevented in PostgreSQL | prevented | possible |
| `SERIALIZABLE` | prevented | prevented | prevented | prevented | prevented |

The SQL standard allows phantoms under REPEATABLE READ. PostgreSQL's snapshot implementation prevents them anyway.

### READ COMMITTED: Real-Time
```sql title:Read-Committed
B: BEGIN;
B: SELECT status FROM Copy WHERE book_id = 5 AND copy_number = 1;  -- available
A: UPDATE Copy SET status = 'on loan' WHERE book_id = 5 AND copy_number = 1;  -- commits
B: SELECT status FROM Copy WHERE book_id = 5 AND copy_number = 1;  -- on loan
B: COMMIT;
```

- **No dirty reads.** While A had not committed, B saw `available`. PostgreSQL treats `READ UNCOMMITTED` exactly like this
- **Non-repeatable reads happen.** Each statement takes a fresh snapshot

### REPEATABLE READ: One Snapshot
```sql title:Repeatable-Read
B: BEGIN ISOLATION LEVEL REPEATABLE READ;
B: SELECT status FROM Copy WHERE ...;  -- available (read 1, snapshot taken here)
A: UPDATE Copy SET status = 'on loan' WHERE ...;  -- commits
B: SELECT status FROM Copy WHERE ...;  -- available (read 2, same snapshot)
B: COMMIT;
B: SELECT status FROM Copy WHERE ...;  -- on loan (new transaction, new snapshot)
```

The snapshot is taken at the **first statement**, not at `BEGIN`.

### REPEATABLE READ: No Overwrite
```sql title:Overdue-Cleanup
B: BEGIN ISOLATION LEVEL REPEATABLE READ;
B: CREATE TEMP TABLE todo AS
     SELECT book_id, copy_number FROM Loan
     WHERE return_date IS NULL AND due_date < CURRENT_DATE - 30;  -- 11 copies
A: -- desk takes back copy (47, 2): Loan returned, Copy 'available', COMMIT
B: SELECT count(*) FROM Loan
   WHERE return_date IS NULL AND due_date < CURRENT_DATE - 30;    -- still 11
B: UPDATE Copy SET status = 'lost'
   WHERE (book_id, copy_number) IN (SELECT * FROM todo);
-- ERROR: could not serialize access due to concurrent update
```

| Level | Count | UPDATE |
|---|---|---|
| READ COMMITTED | drops to 10 | `UPDATE 11`: marks (47, 2) lost although it is back on the shelf |
| REPEATABLE READ | stays 11 | refused, because (47, 2) changed after the snapshot |

The refusal aborts the **whole transaction**. Your program must `ROLLBACK` and retry from the start. The new snapshot no longer lists (47, 2).

### SERIALIZABLE: A Valid Decision
```sql title:Loan-Limit
-- Rule: at most 4 open loans. Member 2 has 3. Both desks use SERIALIZABLE.
A: BEGIN ISOLATION LEVEL SERIALIZABLE;
A: SELECT count(*) FROM Loan WHERE member_id = 2 AND return_date IS NULL;  -- 3
B: BEGIN ISOLATION LEVEL SERIALIZABLE;
B: SELECT count(*) FROM Loan WHERE member_id = 2 AND return_date IS NULL;  -- 3
A: INSERT INTO Loan (...) VALUES (2, 4, 1, ...);  COMMIT;
B: INSERT INTO Loan (...) VALUES (2, 5, 1, ...);
-- ERROR: could not serialize access due to read/write dependencies among transactions
```

- Under READ COMMITTED and REPEATABLE READ both desks lend: member 2 ends with **5**. This is **write skew**
- Under SERIALIZABLE desk B is refused: **4**
- **No row is shared**, so no row lock helps. The database tracks what each transaction read
- The rule itself (max 4) lives in your program. The database only guarantees the result is as if the desks ran one at a time
- The error can surface on any statement or at `COMMIT`. Be ready to retry either way

## F2.2 Row Waits: Write Conflicts
### The Check-Then-Write Trap
```sql title:Check-Then-Write-Wrong
-- WRONG: gap between the check and the write
SELECT status FROM Copy WHERE book_id = 5 AND copy_number = 1;  -- available
-- another desk slips in here
UPDATE Copy SET status = 'on loan' WHERE book_id = 5 AND copy_number = 1;
```

Both desks see "available", both lend. Two receipts for one book.

### Check and Write in One Step
```sql title:Check-Inside-Update
BEGIN;
UPDATE Copy SET status = 'on loan'
WHERE book_id = 5 AND copy_number = 1
  AND status = 'available';   -- the check is part of the write
-- program checks the row count
```

- **1 row changed**: this desk won. `INSERT INTO Loan ...`, `COMMIT`
- **0 rows changed**: someone else took it. `ROLLBACK` and tell the member
- Inside one `UPDATE` there is no gap

In JDBC, `executeUpdate()` returns this row count.

### Two Terminals, One Copy
```sql title:Row-Lock-Demo
A: BEGIN;
A: UPDATE Copy SET status = 'on loan'
   WHERE book_id = 1 AND copy_number = 1 AND status = 'available';  -- not committed
B: UPDATE Copy SET status = 'on loan'
   WHERE book_id = 1 AND copy_number = 1 AND status = 'available';
   -- does not return: WAITING
A: COMMIT;
B: -- returns at once: UPDATE 0
```

- A **row lock** is taken automatically on every written row and held until `COMMIT` or `ROLLBACK`
- Only a **second writer** of the same row waits. **Readers never wait for writers**, they read the last committed version
- After A commits, B **re-checks its WHERE** on the new row version, finds `status = 'on loan'` and reports `UPDATE 0`. No failure, no double booking

That re-check is READ COMMITTED behaviour. Under REPEATABLE READ, B would instead get `could not serialize access` and must retry.

## F2.3 Deadlock Detection
A **deadlock** is two transactions each waiting for a lock the other holds.

```sql title:Deadlock
A: BEGIN; UPDATE Copy ... WHERE book_id = 1 AND copy_number = 1;  -- A holds (1,1)
B: BEGIN; UPDATE Copy ... WHERE book_id = 3 AND copy_number = 1;  -- B holds (3,1)
A: UPDATE Copy ... WHERE book_id = 3 AND copy_number = 1;  -- waits for B
B: UPDATE Copy ... WHERE book_id = 1 AND copy_number = 1;  -- waits for A
-- ERROR: deadlock detected
-- DETAIL: Process 144 waits for ShareLock on transaction 761, blocked by process 151.
--         Process 151 waits for ShareLock on transaction 760, blocked by process 144.
```

- After about **one second**, PostgreSQL cancels one transaction and undoes its work. The other continues and commits
- The cancelled one must be **retried by your program**, not by the database

### Avoiding Deadlocks
You cannot stop the database cancelling, but you can stop the cycle forming:
- **Lock in the same order everywhere**: e.g. always the lower copy first. The second desk then just waits, no cycle
- **Keep transactions short**: less time holding locks
- **Be ready to retry**: catch `deadlock detected`, rerun the whole transaction

Behind it: **two-phase locking (2PL)**. A transaction takes locks as it goes (growing phase) and releases them all at `COMMIT` (shrinking phase). That makes it safe, and also why two growing transactions can block each other.

## ACID
A **transaction** is a unit of work. A relational database promises four things about every transaction:

| | Promise | Definition |
|---|---|---|
| **A** | **Atomicity** | all of the changes happen or none do. Never half done, not even after a crash |
| **C** | **Consistency** | from one valid state to another: every declared rule (PK, FK, CHECK, NOT NULL, UNIQUE) holds after COMMIT |
| **I** | **Isolation** | concurrent transactions do not see each other's unfinished work. How strictly is set by the isolation level |
| **D** | **Durability** | once COMMIT returns, the change survives a crash |

### The Auditor's Four Questions
| | Question | What could go wrong | Why it cannot |
|---|---|---|---|
| A | Can a loan be half recorded? | power fails between the two writes | both writes kept, or neither |
| C | Can a loan break the rules? | a loan for member 999 who does not exist | the FK refuses it |
| I | Can a desk act on another's unfinished loan? | two desks lend copy (1, 1) | B waits for A's COMMIT, then `UPDATE 0` |
| D | Once "done", can the loan vanish? | receipt printed, then the server crashes | COMMIT returned, so it is on disk |

### Where Each Letter Came From
| | Rules out | Met in |
|---|---|---|
| A | copy on loan with no loan record | L7 F1.1 `BEGIN ... COMMIT` |
| C | a loan breaking a CHECK or FK being committed | L3 constraints, L6 Need 4 ON DELETE |
| I | two desks corrupting each other, a clean-up reading half-done work | L7 Need 2 locks and isolation levels |
| D | a crash undoing a confirmed loan | L7 F1.3 write-ahead log |

- **None of the four is free.** Stronger isolation means waiting and retries. Durability means writing to disk
- **None of them decides what belongs in one transaction.** Put both writes of a loan in one transaction, or atomicity cannot help
- Consistency only covers **declared** rules. The loan limit is not declared anywhere, which is why it needed SERIALIZABLE plus program logic

## Need 3: Large Data, Still Fast
`LoanLog` reaches 500,000 rows. "Everything member 27 ever borrowed" (10,000 rows) gets noticeably slow.

| Function | SQL | What it does |
|---|---|---|
| F3.1 Find rows without reading all | `CREATE INDEX` (a B-tree) | jumps to matching rows |
| F3.2 See how a query is run | `EXPLAIN`, `EXPLAIN ANALYZE` | shows the plan, ANALYZE also runs and times it |

**Cannot do for you**: choose which columns to index. Every index makes every write a little slower.

### F3.1 What Is an Index?
```sql title:Create-Index
CREATE INDEX idx_loanlog_member_id ON LoanLog(member_id);
DROP INDEX idx_loanlog_member_id;
```

An **index** is a sorted list of a column's values, each pointing at its rows. Like the index at the back of a book.

- Without it, `WHERE member_id = 27` reads every row: a **Seq Scan** (sequential scan)
- A **B-tree** is kept sorted. It serves `=`, `<`, `>`, `BETWEEN`, `ORDER BY` and joins on that column. Lookups grow only slowly as the table grows
- **A separate object**: never changes *what* a query returns, only *how fast*. Updated on every write

```MD
ROOT PAGE                     LEAF PAGE 2 (sorted)
member_id | go to             member_id | rows
1 - 11    | leaf page 1       12        | 7, ...
12 - 30   | leaf page 2  -->  27        | 2, 4, 1210, ... (10,000)
31 - ...  | leaf page 3       30        | ...

Find 27: root -> leaf page 2 -> fetch rows. A few page reads, never the whole table.
```

**PRIMARY KEY and UNIQUE create an index automatically. A FOREIGN KEY does not** in PostgreSQL. `Loan.member_id` has no index unless you create one, even though you join on it constantly.

### Clustered vs. Non-Clustered
```sql title:Cluster
-- Non-clustered: index sits beside the table (every PostgreSQL index)
CREATE INDEX idx_loanlog_member_id ON LoanLog(member_id);
-- Clustered, once: rewrite the table in that index's order
CLUSTER LoanLog USING idx_loanlog_member_id;
```

| | Non-clustered | Clustered |
|---|---|---|
| What | separate structure with values and row pointers | rows themselves stored in index order |
| How many | any number | at most one per table |
| Where | every PostgreSQL index, including the PK's | SQL Server and MySQL, on the primary key |

- **Before CLUSTER**: the index finds member 27's rows, but they are spread over many pages, each a page read
- **After**: they sit together on a few neighbouring pages
- **Only once** in PostgreSQL: new rows go wherever there is space. Rerun `CLUSTER` to restore the order

### F3.2 Reading a Plan
```sql title:Explain
EXPLAIN SELECT * FROM LoanLog WHERE member_id = 27;
-- Seq Scan on loanlog  (cost=0.00..9927.00 rows=9750 width=31)
--   Filter: (member_id = 27)
```

- `EXPLAIN` **only plans**. `cost` is in arbitrary units, for comparing plans, not predicting milliseconds. `rows` is an estimate
- `EXPLAIN ANALYZE` **really runs the query** and adds real times and row counts. On a write it really writes, so wrap it:

```sql title:Explain-Analyze-Safe
BEGIN;
EXPLAIN ANALYZE UPDATE Loan SET ...;  -- really executes
ROLLBACK;                             -- undo the write
```

### An Index That Helps
```sql title:Index-Before-After
-- Before
EXPLAIN ANALYZE SELECT * FROM LoanLog WHERE member_id = 27;
-- Seq Scan on loanlog (actual time=0.005..10.325 rows=10045 loops=1)
--   Filter: (member_id = 27)
--   Rows Removed by Filter: 489955     -- read and thrown away
-- Execution Time: 10.521 ms

-- After CREATE INDEX idx_loanlog_member_id ON LoanLog(member_id)
-- Bitmap Heap Scan on loanlog (actual time=0.578..2.664 rows=10045 loops=1)
--   -> Bitmap Index Scan on idx_loanlog_member_id
-- Execution Time: 2.843 ms
```

- About **3.7 times faster** in this run. Milliseconds vary, the plan does not
- Member 27 is about **2%** of the rows. An index pays off when few rows match
- **Bitmap scan**: the index collects matching row locations first, sorts them by page, then reads each page once. PostgreSQL picks it when the match count is moderate. A plain **Index Scan** is used for very few matches

### Your Decision: Which Columns Deserve an Index?
Index a column only if **both** hold:
1. You often search, join or sort on it (`WHERE`, `JOIN`, `ORDER BY`)
2. A search on it matches **few** rows

| Question | Index helps | Index does not help |
|---|---|---|
| How many rows match? | few: `member_id`, 2% each | many: `event_type`, half the table |
| How big is the table? | large | small: reading it all is already fast |
| How is the column used? | searched often | written a lot: every write updates the index |

**Verified: index on `event_type` (`'borrow'` or `'return'`), all cost, no gain**:
- Writes: 200,000-row INSERT took 827 ms without, 1,065 ms with. About 1.3 times slower
- Reads: counting `'borrow'` rows still read half the table

**Not every column "to be safe"**: writes get slower, reads no faster.

## Exercise Sheet 7 Rule
Every statement comes with three lines:
1. Which Index row (function) it belongs to
2. Why it is right for your schema
3. How you checked it: a second terminal, a row count, a plan

## Common Traps
- Two related writes without `BEGIN` are two separate transactions. A crash between them leaves half the operation
- Any error inside a transaction aborts it. Everything after fails until `ROLLBACK` or `ROLLBACK TO SAVEPOINT`
- Save points vanish at `COMMIT` or `ROLLBACK`. They cannot undo an earlier transaction
- "Done" means `COMMIT` returned, not that the last statement ran
- `SELECT` to check, then `UPDATE` to write, leaves a gap. Put the check in the `UPDATE`'s `WHERE` and test the row count
- `READ UNCOMMITTED` gives no dirty reads in PostgreSQL. It behaves like READ COMMITTED
- The REPEATABLE READ snapshot starts at the first statement, not at `BEGIN`
- REPEATABLE READ does not stop write skew. Only SERIALIZABLE does, and only if every transaction involved uses it
- A serialization failure or deadlock aborts the whole transaction. Retry from `BEGIN`, not just the failing statement
- Lock rows in one agreed order everywhere to avoid deadlocks
- Readers never wait for writers. Only a second writer of the same row waits
- `EXPLAIN ANALYZE` on a write really writes. Wrap it in `BEGIN ... ROLLBACK`
- `cost` is not milliseconds. Compare plans, not numbers across machines
- Foreign keys get no automatic index in PostgreSQL. Primary keys and UNIQUE do
- Indexing a low-selectivity column (few distinct values) slows writes and speeds up nothing
- `CLUSTER` sorts once. New rows are not kept in order