[[exerciseSheet-01-Intro-RelationalModel-SOLUTIONS.pdf]]

## Exercise 1: Warm-up - Querying Student and Programme

Recall the demo schema from the lecture:
```MD
Programme: {[ programme_id: int, programme_name: string, duration_years: int ]}
Student: {[ student_id: int, first_name: string, last_name: string, email: string, 
             birth_date: date, programme_id -> Programme ]}
```
Student.programme_id records which degree programme a student is enrolled in, a genuine one-to-many relationship, since each student has exactly one programme, and each programme has many students.

**Q1**: List the first name, last name, and birth date of all students.

**A1**:
```sql
SELECT first_name, last_name, birth_date
FROM Student;
```

**Q2**: List the name and duration (in years) of all programmes longer than 2 years, ordered by duration descending.

**A2**:
```sql
SELECT programme_name, duration_years
FROM Programme
WHERE duration_years > 2
ORDER BY duration_years DESC;
```

**Q3**: List the first and last name of every student enrolled in the programme with programme_id = 1, together with the name of that programme (a first taste of JOIN, covered properly in Lecture 5).

**A3**:
```sql
SELECT s.first_name, s.last_name, p.programme_name
FROM Student s, Programme p
WHERE s.programme_id = p.programme_id
  AND s.programme_id = 1;
```

## Exercise 2: More Practice - Querying and Modifying Student/Programme

**Q1**: Write an INSERT statement that adds a new student, Mette Poulsen (mette.poulsen@student.aau.dk, born 2003-05-20), enrolled in the "Computer Science" programme (programme_id = 2).

**A1**:
```sql
INSERT INTO Student (first_name, last_name, email, birth_date, programme_id)
VALUES ('Mette', 'Poulsen', 'mette.poulsen@student.aau.dk', '2003-05-20', 2);
```

**Q2**: List the name and duration (in years) of every programme that is shorter than 3 years, ordered alphabetically by programme name.

**A2**:
```sql
SELECT programme_name, duration_years
FROM Programme
WHERE duration_years < 3
ORDER BY programme_name;
```

**Q3**: Without running it, explain in one sentence why the following INSERT would be rejected by the database.
```sql
INSERT INTO Student (first_name, last_name, email, birth_date, programme_id)
VALUES ('Test', 'Student', 'anna.jensen@student.aau.dk', '2005-01-01', 1);
```

**A3**: It violates the UNIQUE constraint on Student.email. The value 'anna.jensen@student.aau.dk' is already used by the existing row for Anna Jensen, and email was declared UNIQUE NOT NULL back in the live demo, so no two students may share the same email address, even though every other column value in the new row is otherwise perfectly valid.

## Exercise 3: Spot the Problem - A Flawed CREATE TABLE

**Flawed original**:
```sql
CREATE TABLE Employee (
    emp_id      INTEGER,
    first_name  VARCHAR(50) NOT NULL,
    last_name   VARCHAR(50) NOT NULL,
    salary      VARCHAR(20)
);

CREATE TABLE Department (
    dept_id     INTEGER,
    dept_name   VARCHAR(100),
    emp_id      INTEGER,
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id)
);
```

**Q**: Find as many problems as you can, in terms of the keys and integrity rules from the lecture, and rewrite the statements to fix them.

**A - Problems in the original**:

- `Employee.emp_id` has no PRIMARY KEY, nothing enforces entity integrity, so two employees could end up with the same id, or none at all.
- `Department.dept_id` has no PRIMARY KEY either, for the same reason.
- `Department.dept_name` is missing NOT NULL, a department without a name should not be allowed.
- `salary VARCHAR(20)` is the wrong data type, a salary is a number, so nothing stops someone inserting 'not telling', which breaks domain integrity.
- The foreign key is on the wrong table and points the wrong way. `Department.emp_id REFERENCES Employee(emp_id)` would mean each department links to at most one employee. The real relationship is the other way around, many employees belong to one department, so the foreign key belongs on Employee, referencing Department.

**Corrected version**:
```sql
CREATE TABLE Department (
    dept_id     SERIAL PRIMARY KEY,
    dept_name   VARCHAR(100) NOT NULL
);

CREATE TABLE Employee (
    emp_id      SERIAL PRIMARY KEY,
    first_name  VARCHAR(50) NOT NULL,
    last_name   VARCHAR(50) NOT NULL,
    salary      DECIMAL(10,2) NOT NULL,
    dept_id     INTEGER NOT NULL REFERENCES Department(dept_id)
);
```

## Exercise 4: Identify the Keys - A Gym's Members and Branches

**Scenario**: A small gym chain wants a database for its members and branches (locations). Every member signs up at exactly one branch when they join, and every branch has many members. A member has a name, an email address, and a join date. A branch has a name and a city.

**Q**: What are the two tables and their columns, what is a sensible primary key for each, where do you need a foreign key, and describe the cardinality of the relationship.

**A**:
```MD
Branch: {[ branch_id: int, branch_name: string, city: string ]}
Member: {[ member_id: int, name: string, email: string, join_date: date, branch_id -> Branch ]}
```
`Branch.branch_id` is the primary key of `Branch`. `Member.member_id` is the primary key of `Member`. `Member.branch_id` is a foreign key referencing `Branch.branch_id`. The relationship is one-to-many, each member belongs to exactly one branch, but a branch can have many members, exactly the same shape as `Student` and `Programme` from the lecture.

## Exercise 5: Designing a Bookshop Schema

**Scenario**: A small online bookshop wants to keep track of the books it sells, the authors who wrote them, and the orders customers place.

**Q**: On paper, design a simple relational schema with 2-3 tables, Book, Author, and Order.

**A** (one valid design, assuming for simplicity exactly one author per book, a full solution allowing multiple authors per book needs an extra many-to-many table, discussed once relationships of that kind are covered in Lecture 2):

```MD
Author: {[ author_id: int, name: string, country: string ]}
Book: {[ isbn: string, title: string, price: decimal, author_id -> Author ]}
Order: {[ order_id: int, isbn -> Book, customer_name: string, quantity: int, order_date: date ]}
```

In words:
- `Author.author_id` is the primary key of `Author`.
- `Book.isbn` is the primary key of `Book`. `Book.author_id` is a foreign key referencing `Author.author_id`, one author can write many books, but here each book has exactly one author.
- `Order.order_id` is the primary key of `Order`. `Order.isbn` is a foreign key referencing `Book.isbn`, one book can appear in many orders, but each order (row) is for one book.

## Exercise 6: Implementing the Bookshop Schema

**Task**: Create a PostgreSQL database. Write CREATE TABLE statements for Author, Book, and Order from Exercise 5, insert at least 5 rows into each table, and write one SELECT query joining all three tables.

**A - Tables**:
```sql
CREATE TABLE Author (
    author_id   SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    country     VARCHAR(60)
);

CREATE TABLE Book (
    isbn        VARCHAR(20) PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    price       DECIMAL(6,2) NOT NULL CHECK (price >= 0),
    author_id   INTEGER NOT NULL,
    FOREIGN KEY (author_id) REFERENCES Author(author_id)
);

CREATE TABLE "Order" (
    order_id        SERIAL PRIMARY KEY,
    isbn            VARCHAR(20) NOT NULL,
    customer_name   VARCHAR(100) NOT NULL,
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    order_date      DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (isbn) REFERENCES Book(isbn)
);
```
Note: ORDER is a reserved SQL keyword, so the table name must be quoted as "Order" wherever it is used.

**A - Data** (at least 5 rows per table):
```sql
INSERT INTO Author (name, country) VALUES
('J.K. Rowling', 'United Kingdom'),
('George Orwell', 'United Kingdom'),
('Haruki Murakami', 'Japan'),
('Isabel Allende', 'Chile'),
('Yuval Noah Harari', 'Israel');

INSERT INTO Book (isbn, title, price, author_id) VALUES
('978-0-7475-3269-9', 'Harry Potter and the Philosopher''s Stone', 12.99, 1),
('978-0-452-28423-4', '1984', 9.99, 2),
('978-0-307-59313-3', 'Norwegian Wood', 11.50, 3),
('978-0-06-088328-7', 'The House of the Spirits', 13.25, 4),
('978-0-06-231609-7', 'Sapiens', 15.00, 5);

INSERT INTO "Order" (isbn, customer_name, quantity, order_date) VALUES
('978-0-7475-3269-9', 'Anna Nielsen', 2, '2026-01-10'),
('978-0-452-28423-4', 'Peter Jensen', 1, '2026-01-11'),
('978-0-307-59313-3', 'Mette Sorensen', 3, '2026-01-12'),
('978-0-06-088328-7', 'Anna Nielsen', 1, '2026-01-15'),
('978-0-06-231609-7', 'Lars Christensen', 2, '2026-01-16');
```

**A - Join query**:
```sql
SELECT o.customer_name, b.title, a.name AS author_name
FROM "Order" o
JOIN Book b ON o.isbn = b.isbn
JOIN Author a ON b.author_id = a.author_id;
```