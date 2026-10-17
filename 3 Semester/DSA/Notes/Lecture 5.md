[[Lecture5-Joins-Subqueries.pdf]]

# SQL: Joins and Subqueries
Lecture 4 queried one table at a time. Real questions rarely stay inside one table. "Which members currently have an overdue book, and what is it called?" needs `Member`, `Loan` and `Book` at once. This lecture covers two ways to combine data: **JOIN** glues rows from several tables together, and **subqueries** nest one complete `SELECT` inside another.

## Recap: The Library Schema
Same seven tables as Lectures 3 and 4. One new column: `Member.referred_by`, a self-reference to another member.

```
Book: {[ book_id: int, title: string, isbn: string, publication_year: int ]}
Author: {[ author_id: int, first_name: string, last_name: string, birth_year: int ]}
Member: {[ member_id: int, first_name: string, last_name: string, email: string, join_date: date, referred_by → Member ]}
Loan: {[ loan_id: int, member_id → Member, book_id, copy_number, loan_date: date, due_date: date, return_date: date ]}
```

Plus `BookGenre`, `Copy`, `BookAuthor`. Dataset sizes used in the examples: 80 books, 157 copies, 42 authors, 50 members, 258 loans.

### Why One Table Is Never Enough
- `Member` has names but not who borrowed what
- `Loan` has `member_id` and `book_id` but no names or titles
- `Book` has titles but not who holds which copy

The answer only exists in the **combination** of rows across tables.

## JOIN Types
### General Shape
```sql title:Join-Shape
SELECT columns
FROM table1
JOIN table2 ON table1.column = table2.column;  -- almost always an FK = PK match
```

- `JOIN` lives inside `FROM`. It builds one combined table **before** `WHERE` and `GROUP BY` run
- The `ON` condition says how rows line up
- The JOIN types differ only in **what happens to rows with no match**

### INNER JOIN
Plain `JOIN` means **INNER JOIN**. A row survives only if both sides have a match. Unmatched rows are silently dropped.

```sql title:Inner-Join
SELECT b.title, c.copy_number, c.status
FROM Book b
JOIN Copy c ON b.book_id = c.book_id  -- one output row per (book, copy) pair
ORDER BY b.title, c.copy_number;
```

Every book has at least one copy, so nothing is lost here. The next example shows what happens when that is not true.

### LEFT JOIN
**LEFT JOIN** keeps every row from the left table. Where the right side has no match, its columns are **padded with NULL**.

```sql title:Left-Join
SELECT b.book_id, b.title, l.loan_id
FROM Book b
LEFT JOIN Loan l ON b.book_id = l.book_id;
```

| Join type (Book / Loan) | Rows |
|---|---|
| INNER JOIN | 258 |
| LEFT JOIN | 260 |

The 2 extra rows are the books never borrowed. Find them by testing the padded column:

```sql title:Left-Join-Unmatched
SELECT b.book_id, b.title
FROM Book b
LEFT JOIN Loan l ON b.book_id = l.book_id
WHERE l.loan_id IS NULL;  -- only the padded rows: Guards! Guards! and Beloved
```

LEFT JOIN is the natural shape for "everything on the left, annotated with matching data on the right when it exists".

### RIGHT JOIN
Mirror image of LEFT JOIN. Keeps every row from the **second** table.

```sql title:Right-Join
SELECT b.book_id, b.title, l.loan_id
FROM Loan l
RIGHT JOIN Book b ON b.book_id = l.book_id;
-- identical to: FROM Book b LEFT JOIN Loan l ON b.book_id = l.book_id
```

Rare in practice. Swap the table order and write LEFT JOIN instead, it reads more naturally.

### FULL OUTER JOIN
Keeps every row from **both** sides, padding whichever side has no match. `OUTER` is optional.

```sql title:Full-Join
SELECT b.title, l.loan_id
FROM Book b
FULL JOIN Loan l ON b.book_id = l.book_id;  -- 260 rows, same as LEFT JOIN here
```

Same count as LEFT JOIN because `Loan.book_id` is `NOT NULL` and FK-enforced, so no loan can be unmatched. FULL JOIN only adds rows when **both** sides can have orphans. Foreign keys usually rule that out, which makes it the least used of the four.

### All Four With an Extra ON Condition
Adding a filter to `ON` changes **which right-side rows count as a match**, and each JOIN type then reacts differently to the unmatched rows.

```sql title:Join-Extra-On
SELECT b.title, l.due_date
FROM Book b
JOIN Loan l                       -- swap for LEFT / RIGHT / FULL
  ON b.book_id = l.book_id
 AND l.return_date IS