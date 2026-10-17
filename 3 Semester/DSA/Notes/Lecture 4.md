[[]]

# SQL: Single-Table Queries and Aggregation
Lecture 3 built the seven library tables and every constraint on them. This lecture is about retrieval: turning a question a librarian would ask into one precise `SELECT` statement. Every query touches exactly one table. Combining tables comes later with joins.

**DQL** (Data Query Language) is the retrieval side of SQL, centred on `SELECT`. It reads data and never changes it, in contrast to DDL (`CREATE TABLE`) and DML (`INSERT`, `UPDATE`, `DELETE`).

## Recap: The Library Schema
No new tables this lecture. Every query runs against the schema from [[Lecture3-ER-to-SQL.pdf]].

```
Book: {[ book_id: int, title: string, isbn: string, publication_year: int ]}
Author: {[ author_id: int, first_name: string, last_name: string, birth_year: int ]}
Member: {[ member_id: int, first_name: string, last_name: string, email: string, join_date: date ]}
Loan: {[ loan_id: int, member_id → Member, book_id, copy_number, loan_date: date, due_date: date, return_date: date ]}
```

Plus the junction and weak-entity tables `BookGenre`, `Copy`, `BookAuthor` from Rule 3 and Rule 5.

Questions this lecture answers, each as a single statement:
- Which books came out in the 1950s?
- Which loans are still outstanding?
- How many books do we have per genre?
- Which genres have more than five books?

## SELECT, FROM, WHERE
```sql title:Anatomy
SELECT column1, column2   -- which columns to return, or * for all
FROM table_name           -- which table to read from
WHERE condition;          -- which rows to keep
```

- **SELECT** picks columns
- **FROM** picks the table
- **WHERE** picks rows. It is evaluated **per row, independently**, never against the result set as a whole. Only rows where the condition is `TRUE` survive.

```sql title:Basic-Filter
SELECT title, publication_year
FROM Book
WHERE publication_year > 1950;  -- each row is tested on its own
```

### Comparison Operators
| Operator | Meaning |
|---|---|
| `=` | equal to |
| `!=` or `<>` | not equal to |
| `<` `>` | less / greater than |
| `<=` `>=` | less / greater than or equal to |

All six work on numbers, dates and text. Text compares alphabetically.

```sql title:Comparison
SELECT last_name, birth_year
FROM Author
WHERE birth_year < 1900;
```

### Logical Operators: AND, OR, NOT
- **AND** needs both sides true
- **OR** needs at least one side true
- **NOT** negates the condition after it

`AND` binds tighter than `OR`, exactly like `&&` and `||` in Java. Once both appear in one `WHERE`, add parentheses.

```sql title:AND-OR-Precedence
-- Without parentheses this reads as: A OR (B AND C)
SELECT title FROM Book
WHERE publication_year < 1940 OR publication_year > 1990 AND isbn IS NOT NULL;

-- What was probably meant: (A OR B) AND C
SELECT title FROM Book
WHERE (publication_year < 1940 OR publication_year > 1990) AND isbn IS NOT NULL;
```

`NOT` is most useful when the condition is not a simple comparison, such as `NOT (title LIKE '...')` or `NOT (genre IN (...))`. For a plain comparison, just flip the operator.

### Special Operators
#### LIKE
Pattern matching on text with two wildcards:
- `%` matches any number of characters, including zero
- `_` matches exactly one character

```sql title:LIKE
SELECT last_name FROM Author WHERE last_name LIKE 'Tol%';   -- starts with "Tol"
SELECT last_name FROM Author WHERE last_name LIKE '%kien%'; -- contains "kien"
SELECT title FROM Book WHERE title LIKE '_nimal%';          -- any 1st char, then "nimal"
```

`LIKE` is **case-sensitive** in PostgreSQL. `ILIKE` is the case-insensitive variant.

#### IN
Shorthand for "matches any value in this list". Pure readability, not a new capability.

```sql title:IN
SELECT book_id, genre
FROM BookGenre
WHERE genre IN ('Fantasy', 'Science Fiction', 'Mystery');
-- same as: genre = 'Fantasy' OR genre = 'Science Fiction' OR genre = 'Mystery'
```

#### BETWEEN
Range check, **inclusive on both ends**. Forgetting the inclusivity is a classic off-by-one.

```sql title:BETWEEN
SELECT title, publication_year
FROM Book
WHERE publication_year BETWEEN 1940 AND 1960;
-- same as: publication_year >= 1940 AND publication_year <= 1960
```

#### IS NULL / IS NOT NULL
**NULL** means "no value recorded". It is not zero and not an empty string. In `Loan`, `return_date IS NULL` means "still on loan", not "returned on an unknown date".

```sql title:Active-Loans
SELECT loan_id, member_id, book_id, due_date
FROM Loan
WHERE return_date IS NULL;  -- never returned yet
```

## NULL and Three-Valued Logic
### Why = NULL Never Works
```MD
WHERE return_date = NULL      -- WRONG: evaluates to UNKNOWN for every row, matches nothing
WHERE return_date IS NULL     -- RIGHT
WHERE NOT (return_date = NULL) -- ALSO WRONG: NOT UNKNOWN is still UNKNOWN
```

NULL is an unknown value. Comparing anything to "unknown" gives "unknown", not true. No error is raised; the rows just silently disappear.

SQL uses **three-valued logic**: every condition evaluates to `TRUE`, `FALSE` or `UNKNOWN`. `WHERE` keeps only `TRUE` rows. Both `FALSE` and `UNKNOWN` are dropped.

### Truth Tables
**AND**: one `FALSE` decides everything.

| AND | TRUE | FALSE | NULL |
|---|---|---|---|
| **TRUE** | TRUE | FALSE | NULL |
| **FALSE** | FALSE | FALSE | FALSE |
| **NULL** | NULL | FALSE | NULL |

**OR**: one `TRUE` decides everything.

| OR | TRUE | FALSE | NULL |
|---|---|---|---|
| **TRUE** | TRUE | TRUE | TRUE |
| **FALSE** | TRUE | FALSE | NULL |
| **NULL** | TRUE | NULL | NULL |

**NOT**: cannot turn unknown into an answer.

| Operand | NOT |
|---|---|
| TRUE | FALSE |
| FALSE | TRUE |
| NULL | NULL |

Memory trick: if the known side alone settles the result (`FALSE AND ...`, `TRUE OR ...`), the NULL does not matter. Otherwise the result stays NULL.

Do not confuse the logical `NOT NULL` (still NULL) with the `NOT NULL` column constraint from Lecture 3.

### Operator Recap
| Category | Operators |
|---|---|
| Comparison | `=`, `!=`, `<`, `>`, `<=`, `>=` |
| Logical | `AND`, `OR`, `NOT` |
| Pattern matching | `LIKE` with `%` and `_` |
| Set membership | `IN (...)` |
| Range | `BETWEEN ... AND ...` |
| NULL testing | `IS NULL`, `IS NOT NULL` |

## Ordering, Limiting and Aliases
### ORDER BY
`WHERE` decides which rows come back. **ORDER BY** decides their order. `ASC` is the default, `DESC` reverses it.

```sql title:Order-By-Multiple
SELECT last_name, first_name, birth_year
FROM Author
ORDER BY birth_year ASC, last_name ASC;  -- ties on birth_year broken by last_name
```

Each column gets its own direction. Ties are broken left to right.

### LIMIT and OFFSET
- **LIMIT n** returns only the first n rows of the already filtered, already sorted result
- **OFFSET m** skips the first m rows

Together they give **pagination**, which is how most web result pages work.

```sql title:Pagination
-- Page 2 with 3 results per page
SELECT title
FROM Book
ORDER BY book_id   -- required for a stable order
LIMIT 3 OFFSET 3;  -- skip page 1, take the next 3
```

General formula: `LIMIT page_size OFFSET (page_number - 1) * page_size`.

`LIMIT` without `ORDER BY` is close to meaningless. "The first 3 rows" of an unordered result is whatever order PostgreSQL happens to store them in, and that is not guaranteed to be stable.

### Aliases
A **column alias** renames a column or expression in the result. The table itself is untouched.

```sql title:Column-Alias
SELECT last_name AS surname, birth_year AS born
FROM Author
WHERE birth_year < 1900;
```

A **table alias** shortens a table name. Barely useful in a single-table query, essential once joins arrive.

```sql title:Table-Alias
SELECT b.title, b.publication_year  -- "title, from the table aliased b"
FROM Book AS b
WHERE b.publication_year > 1950;
```

`AS` is optional in PostgreSQL but required by some other DBMSs. Write it out.

### NULLs in ORDER BY
PostgreSQL treats NULL as **larger than every other value** when sorting.

```MD
ORDER BY publication_year ASC   -- ..., 1954, 1997, NULL      (NULLs last)
ORDER BY publication_year DESC  -- NULL, 1997, 1954, ...      (NULLs first)
ORDER BY publication_year ASC NULLS FIRST  -- explicit override
```

### Putting It Together
```sql title:Combined
SELECT title, publication_year
FROM Book
WHERE publication_year IS NOT NULL  -- drop undated books
ORDER BY publication_year DESC      -- newest first
LIMIT 5 OFFSET 0;                   -- top five
```

## Aggregate Functions
Everything so far returns rows. **Aggregate functions** collapse many rows into one summary value. Used alone, they collapse the whole table to a single row.

| Function | Computes | NULL behaviour |
|---|---|---|
| `COUNT(*)` | number of rows | NULLs counted |
| `COUNT(column)` | non-NULL values in that column | NULLs skipped |
| `SUM(column)` | total of a numeric column | NULLs skipped |
| `AVG(column)` | mean of a numeric column | NULLs excluded from sum and count |
| `MIN` / `MAX` | smallest / largest value | NULLs skipped, works on numbers, dates and text |

### COUNT(*) vs. COUNT(column)
The most common aggregate mistake is assuming they always agree. They only agree when the column has no NULLs.

```MD
Loan
loan_id | member_id | book_id | return_date
1       | 1         | 3       | 2026-05-14
2       | 2         | 5       | NULL        -- still on loan
3       | 3         | 2       | 2026-06-01
4       | 4         | 8       | NULL        -- still on loan
```

```sql title:Count-Gotcha
SELECT COUNT(*)           AS all_loans,      -- 4: every row
       COUNT(return_date) AS returned_loans  -- 2: only non-NULL return_date
FROM Loan;
```

Always ask which one the question needs:
- "How many loans exist?" is `COUNT(*)`
- "How many loans have been returned?" is `COUNT(return_date)`

### SUM and AVG
Subtracting two `DATE` values in PostgreSQL gives a plain integer number of days. That makes loan lengths easy to aggregate.

```sql title:Sum-Avg
SELECT SUM(due_date - loan_date) AS total_days_borrowed,  -- e.g. 1988
       AVG(due_date - loan_date) AS avg_loan_days         -- e.g. 14.6
FROM Loan;
```

### MIN and MAX
```sql title:Min-Max
SELECT MIN(join_date) AS earliest_member,
       MAX(join_date) AS newest_member
FROM Member;
```

## GROUP BY and HAVING
### GROUP BY
**GROUP BY** splits the table into buckets by a column's value, then runs each aggregate inside each bucket separately. Result: **one row per distinct value**.

```sql title:Group-By
SELECT genre, COUNT(*) AS num_books
FROM BookGenre
GROUP BY genre;  -- one row per genre
```

| genre | num_books |
|---|---|
| Fantasy | 38 |
| Science Fiction | 31 |
| Dystopian | 12 |
| Mystery | 9 |

Any number of aggregates can sit next to `GROUP BY`, each computed per group.

```sql title:Group-By-Multiple-Aggregates
SELECT member_id,
       COUNT(*)                  AS num_loans,
       AVG(due_date - loan_date) AS avg_days
FROM Loan
GROUP BY member_id;  -- one row per member
```

### HAVING
- **WHERE** filters **rows**, before grouping
- **HAVING** filters **groups**, after aggregation

`HAVING` is the only place an aggregate function may appear in a filter condition.

```sql title:Having
SELECT genre, COUNT(*) AS num_books
FROM BookGenre
GROUP BY genre
HAVING COUNT(*) > 5      -- keep only genres with more than 5 books
ORDER BY num_books DESC;
```

Both can appear in the same query, each doing its own job at its own stage.

```sql title:Where-And-Having
-- Members with more than 3 loans that started in 2026
SELECT member_id, COUNT(*) AS loans_2026
FROM Loan
WHERE loan_date >= '2026-01-01'  -- row filter: raw column
GROUP BY member_id
HAVING COUNT(*) > 3;             -- group filter: aggregate
```

### The SQL Execution Order
The key fact of the lecture: SQL is **executed in a different order than it is written**.

```MD
Written:  SELECT -> FROM -> WHERE -> GROUP BY -> HAVING -> ORDER BY -> LIMIT
Executed: FROM -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY -> LIMIT
```

This explains the WHERE vs. HAVING rule:
- `WHERE` runs before `GROUP BY` exists, so no aggregate has been computed yet
- `HAVING` runs after grouping, so aggregates already exist

```sql title:Where-Aggregate-Error
SELECT genre, COUNT(*)
FROM BookGenre
WHERE COUNT(*) > 1   -- ERROR: aggregate functions are not allowed in WHERE
GROUP BY genre;
```

Fix: move the condition to `HAVING`.

Rule of thumb: filter on a raw column with `WHERE`, filter on an aggregate result with `HAVING`.

### Full SELECT Syntax
The typed order is fixed by SQL grammar. Getting it wrong is a syntax error.

```sql title:Full-Skeleton
SELECT column1, column2  -- required
FROM table_name          -- required
WHERE condition          -- optional, filters rows
GROUP BY column1         -- optional, collapses rows into groups
HAVING condition         -- optional, filters groups
ORDER BY column1         -- optional, sorts
LIMIT n                  -- optional, caps rows
OFFSET m;                -- optional, skips rows
```

Typed order is a syntax requirement. Execution order is what explains the behaviour.

## Java Connection: JDBC Preview
Connecting from Java, using the same credentials as the lecture's `docker-compose.yml`:

```java title:JDBC-Connection
String url = "jdbc:postgresql://localhost:5432/dbs";
Connection connection = DriverManager.getConnection(url, "dbs", "dbs");
```

The PostgreSQL JDBC driver (`org.postgresql:postgresql`) must be on the classpath. Without it the code compiles fine but throws `SQLException: No suitable driver found` at runtime, even with the database running.

A **parameterized query** keeps the value separate from the SQL string:

```java title:Prepared-Statement
String sql = "SELECT * FROM Member WHERE member_id = ?"; // ? is a placeholder
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setInt(1, memberId);          // value supplied separately, never concatenated
ResultSet rs = stmt.executeQuery();
```

That separation is also what prevents **SQL injection**. Covered properly in a later lecture.

## Common Traps
- `= NULL` or `!= NULL` matches nothing. Use `IS NULL` / `IS NOT NULL`
- `BETWEEN` is inclusive on both ends
- `AND` binds tighter than `OR`. Parenthesise mixed conditions
- `LIKE` is case-sensitive in PostgreSQL. Use `ILIKE` for case-insensitive
- `COUNT(*)` counts rows, `COUNT(column)` skips NULLs. They differ whenever the column has NULLs
- Aggregates in `WHERE` are an error. Move them to `HAVING`
- `LIMIT` without `ORDER BY` gives an unstable result
- NULL sorts as the largest value: last in `ASC`, first in `DESC`
- Every non-aggregated column in `SELECT` must appear in `GROUP BY`. `SELECT genre, book_id, COUNT(*) ... GROUP BY genre` fails because one group has many `book_id` values
- A `SELECT` alias cannot be used in `WHERE` or `HAVING`, since both run before `SELECT` in the execution order. It can be used in `ORDER BY`, which runs after. That is why the examples write `HAVING COUNT(*) > 5` but `ORDER BY num_books`