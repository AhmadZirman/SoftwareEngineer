[[Lecture1-Intro-RelationalModel-typst.pdf]]
# Core Vocabulary (formal $\leftrightarrow$ everyday term)
- Relation = Table
- Tuple = Row
- Attribute = Column
- Domain = The set of legal values for a column
- Degree = Number of columns
- Cardinality = Number of rows
These get used interchangeably in exams and later lectures, so both way translation should be instant.

# A Table Is A Mathematical Relation
Formally, a table is a relation: a subset of the Cartesian product of a set of domains, or value ranges. Practically, it is easier to think of a table as rows and columns - one row per real-world fact or entity instance, and one column per property recorded about each instance. You will hear both vocabularies throughout the course: the formal terms relation, tuple, and attribute mean exactly the same thing as the everyday terms table, row, and column.

### Database Management System (DBMS)
A DBMS is a software program that lets you **create**, **store**, **change**, and **retrieve** data in a database.

### Why DBMS's exist at all
Because a DBMS is software that manages all of this for you, out of the box:
- A structured way to describe your data: A data model
- A query language to retrieve and change that data
- Mechanisms that actively guard against redundancy and inconsistency
- Access Control for different users
- Safe concurrent access, through transactions
In short, you stop reinventing all of this by hand, in every application you write, regardless of language.

### Files vs. a Shared DB
For each program/application, you'd need their own file, which could lead to data duplications and mismatch, and also inconsistency or redundancy.

A shared DB solves this problem by having one system for all applications that use this specific data, which is shared and consistent.

### Database vs. DBMS vs. Database System
Three related terms are worth pinning down precisely, because all three will be used throughout the course:
- Database: A collection of related data that models some part of the real world
- DBMS: The software that lets you define, create, query, update, and administer databases
- Database System: The combination of a database *and* the DBMS managing it

Examples of DBMS Software we will encounter in practice include PostgreSQL, MySQL, Oracle, SQL Server, SQLite.

### History of Data Models
A data model is a collection of abstract concepts for describing data, essentially, the theory that dictates how a database is structured. Databases did not start out relational: The field went through three major eras.

- Hierarchical Model: 1960s
The hierarchical model, data is organized as a tree: every record has exactly one parent. A *DEPARTMENT* has many *Employees*, and each *Employee* has many *Payslips*, for example. That structure is fast to navigate along that one fixed path, but it is rigid - it struggles to represent data that naturally has more than one parent, such as a student enrolled in two courses at once.

- Network Model: Late 1960s to 1970s
The network model generalizes the tree into a graph, so a record can have multiple parents rather than just one. That is more flexible than the hierarchical model, but navigation is still done by following explicit, hand-coded pointers between records. Both of these earlier models require the programmer to know the physical structure of the data just to query it.

- Relational Model: 1970 onward
The relational model was proposed by E. F. Codd at IBM in 19691 , and its key idea was to represent all data uniformly as simple tables. Crucially, it separates the logical view of the data - the tables you see - from the physical storage underneath - the files, indexes, and disk layout the DBMS actually uses. That separation is exactly why SQL is a declarative language: you describe the result you want, and the DBMS figures out how to get it, rather than you writing out the navigation path yourself.

The relational model is what virtually every mainstream database, including PostgreSQL, still uses today.

## Keys
A table is technically just a set of tuples, but in practice we constantly need to answer questions like “give me the one row for student Anna Jensen.” The trouble is that two students could easily share the same name, so names alone cannot reliably identify a row. Keys are how the relational model guarantees that we can always uniquely identify - and refer back to - one specific tuple.

#### Super Key
A superkey is any set of attributes that uniquely identifies a tuple in a relation - no two rows can ever share the same combination of values for those attributes. For Student:
```MD
{student_id} // Is a superkey
{student_id, email} // IS also a superkey
{student_id, first_name, last_name} // Is also a superkey
```
Once a set of attributes is already unique, adding more attributes on top of it is still unique - so it is still a superkey. That is why a relation can have many superkeys at once.


#### Candidate Key
A candidate key is a minimal superkey - remove any single attribute from it and it stops being unique.
```MD
{student_id} // on its own is a candidate key for Student 
{student_id, email} // is not a candidate key: it is a valid superkey, but not minimal, since {student_id} alone already does the job
{email} // could also qualify as a candidate key, provided every student has a unique, non-null email address
```
A table can have several candidate keys at once; the database designer then picks one of them to serve as the primary key.


#### Primary Key
The primary key is whichever candidate key gets chosen as the main identifier for a table, and every table should have exactly one. Once you declare a primary key, the DBMS automatically enforces two guarantees on it:
```MD
uniqueness // no two rows may ever share the same primary key value
not-null // a primary key value must always be present
```
For our running example, Student.student_id and Programme.programme_id will be our primary keys.


#### Foreign Key
A foreign key is an attribute - or set of attributes - in one table that refers to the primary key of another table (or, occasionally, the same table). It is how the relational model represents relationships between tables without duplicating data.

A foreign key does not strictly have to reference a primary key - any *UNIQUE*-constrained column works, since guaranteed uniqueness is the real requirement. Every foreign key in this course references a primary key, the common case.

In our example, *Student.programme_id* refers to *Programme.programme_id*: instead of copying the full programme name and duration into every single student row, we just store a reference to the programme they are enrolled in.


### Schema vs. Instance
The **schema** is the structure - the table names, attribute names, and their domains - and it rarely changes once a system is running. The **instance** is the actual data sitting in the tables at a given moment, and it changes constantly, as new students enroll every semester.

Analogy from OOP: the schema is like a class definition, and an instance is like the objects created from that class at runtime.

### Degree and Cardinality (recap)
- **Degree (arity)**: the number of attributes a relation has - `Student(student_id, first_name, last_name, email)` has degree 4.
- **Cardinality**: the number of tuples a relation currently holds - if Student has 30 rows, its cardinality is 30.

Cardinality changes constantly as data is added/removed. Degree only changes when you deliberately alter the schema.

## Integrity Rules
The DBMS enforces three integrity rules automatically, so your data can never silently drift into inconsistency:

#### Domain Integrity
Every attribute value must belong to its declared domain (type). Enforced directly by the column type - `INTEGER`, `VARCHAR(50)`, `DATE`, etc. Trying to insert `"three"` into a `duration_years INTEGER` column gets rejected outright.

#### Entity Integrity
Every table's primary key must be unique and not null, for every row. This guarantees every tuple can always be unambiguously identified.

#### Referential Integrity
Every foreign key value must either match an existing value in the column it references, or be `NULL` (if the relationship allows it). Guarantees no dangling references - e.g. `Student.programme_id = 99` can't point at a programme that doesn't exist.

## Relation Schema Notation (shorthand)
A compact way to sketch a design on paper, before writing full `CREATE TABLE` statements. Notation:
- **Underline** → primary key
- **→ RelationName** → foreign key, referencing that relation's primary key
- No underline, no arrow → an ordinary attribute

```
Programme: {[ programme_id: int, programme_name: string, duration_years: int ]}
Student: {[ student_id: int, first_name: string, last_name: string, email: string, 
             birth_date: date, programme_id → Programme ]}
```

## SQL from the Live Demo

#### CREATE TABLE
```sql title:CREATE-TABLE
CREATE TABLE Programme (
    programme_id    SERIAL PRIMARY KEY,
    programme_name  VARCHAR(100) NOT NULL,
       duration_years  INTEGER NOT NULL
);

CREATE TABLE Student (
    student_id    SERIAL PRIMARY KEY,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    birth_date    DATE,
    programme_id  INTEGER NOT NULL REFERENCES Programme(programme_id)
);
```
- `SERIAL` → auto-incrementing integer; PostgreSQL fills it in automatically
- `PRIMARY KEY` → enforces entity integrity for free
- `NOT NULL` → value always required
- `UNIQUE` → no two rows can share this value
- `REFERENCES` → declares the foreign key

#### INSERT
```sql title:INSERT
INSERT INTO Programme (programme_name, duration_years) VALUES
    ('Software Engineering', 3),
    ('Computer Science', 2),
    ('Data Science', 2);
```
Note: `programme_id` is not specified - PostgreSQL assigns it automatically (1, 2, 3...) via `SERIAL`.

#### SELECT (with WHERE and ORDER BY)
```sql title:SELECT
-- all rows, all columns
SELECT * FROM Student;

-- filtering
SELECT first_name, last_name
FROM Student
WHERE programme_id = 1;

-- filtering + sorting
SELECT first_name, last_name, birth_date
FROM Student
WHERE programme_id = 1
ORDER BY birth_date;
```

#### A Taste of JOIN (proper treatment in Lecture 5)
```sql title:JOIN
SELECT s.first_name, s.last_name, p.programme_name
FROM Student s, Programme p
WHERE s.programme_id = p.programme_id;
```
Combines rows from two tables using the FK relationship. Read aloud: for every student, look up the name of the programme they're enrolled in.