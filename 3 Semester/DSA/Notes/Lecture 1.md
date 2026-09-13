
obsidian://open?vault=Software&file=Software%20Engineering%2F3%20Semester%2FDSA%2FSlides%2FLecture1-Intro-RelationalModel-typst.pdf
# Core Vocabulary (formal $\leftrightarrow$ everyday use)
- Relation = Table
- Tuple = Row
- Attribute = Column
- Domain = The set of legal values for a column
- Degree = Number of columns
- Cardinality = Number of rows
These get used interchangeably in exams and later lectures, so both way translation should be instant.

# Database Management System (DBMS)

A DBMS is a software program that lets you **create**, **store**, **change**, and **retrieve** data in a database.

## Why DBMS's exist at all
Because a DBMS is software that manages all of this for you, out of the box:
- A structured way to describe your data: A data model
- A query language to retrieve and change that data
- Mechanisms that actively guard against redundancy and inconsistency
- Access Control for different users
- Safe concurrent access, through transactions
In short, you stop reinventing all of this by hand, in every application you write, regardless of language.


## Files vs. a Shared DB
For each program/application, you'd need their own file, which could lead to data duplications and mismatch, and also inconsistency or redundancy.

A shared DB solves this problem by having one system for all applications that use this specific data, which is shared and consistent.


## Database vs. DBMS vs. Database System
Three related terms are worth pinning down precisely, because all three will be used throughout the course:
- Database: A collection of related data that models some part of the real world
- DBMS: The software that lets you define, create, query, update, and administer databases
- Database System: The combination of a database *and* the DBMS managing it

Examples of DBMS Software we will encounter in practice include PostgreSQL, MySQL, Oracle, SQL Server, SQLite.


## History of Data Models
A data model is a collection of abstract concepts for describing data, essentially, the theory that dictates how a database is structured. Databases did not start out relational: The field went through three major eras.
- Hierarchical Model: 1960s
- Network Model: Late 1960s to 1970s
- Relational Model: 1970 onward

The relational model is what virtually every mainstream database, including PostgreSQL, still uses today.