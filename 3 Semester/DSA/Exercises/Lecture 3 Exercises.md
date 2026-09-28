[[exerciseSheet-03-ER-to-SQL-SOLUTIONS.pdf]]

# Exercise 1: Warm-up, Picking the Right Type
For each attribute description below, pick the PostgreSQL data type that best fits it, from: `NUMERIC(p, s), BOOLEAN, TEXT, TIMESTAMPZ, BIGINT`. Briefly justify your choice.

**Q1**: A Product's price, which must be stored exactly, in whole cents (no rounding errors), with up to two decimal places.

**A1**: `NUMERIC(10, 2)`, 10 total numbers allowed, number of digits allowed after decimal point, 2 digits.

**Q2**: Whether a customer has opted in to marketing emails: a plain yes/no flag.

**A2**: `BOOLEAN`, no need for further explanation.

**Q3**: The full text of a customer review, which could be a single word or several paragraphs – there is no reasonable fixed maximum length.

**A3**: `TEXT`, There's no maximum length, so a `VARCHAR(n)` would be the limiting factor.

**Q4**: The exact moment an order was placed, including the time zone it was placed in (the company has customers across several time zones).

**A4**: `TIMESTAMPZ`, An exact moment that stays correct across time zones.
Exam trap: PostgreSQL converts it to UTC when storing. It doesn't keep original zone, but it does record the exact moment correctly.

**Q5**: A social-media post’s view counter, which for a very popular post could exceed 2 billion.

**A5**: `BIGINT`, because `INTEGER` stops at about 2.1 billion, so it could overflow.



# Exercise 2: Translating a 1:1 Relationship

Consider the following small scenario at a company:
```MD
Each Employee (employee_id, name) may be assigned at most one ParkingSpot (spot_id, location), and each ParkingSpot is assigned to at most one Employee. Not every employee has a parking spot, and not every parking spot is currently assigned.
```
This is a 1:1 relationship between `Employee` and `ParkingSpot` (with partial participation on both sides).

**Q1**: Write the two CREATE TABLE statements that implement this, including the foreign key that realizes the 1:1 relationship, and the constraint that stops the same foreign key value from being used by more than one row (i.e. what actually enforces the “at most one” on the referencing side).

**A1**: 

```SQL
CREATE SCHEMA ex2;
SET search_path TO ex2;

CREATE TABLE Employee(
	employee_id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL
);


CREATE TABLE ParkingSpot(
	spot_id SERIAL PRIMARY KEY,
	location VARCHAR(50) NOT NULL,
	employee_id INTEGER UNIQUE REFERENCES Employee(employee_id) ON DELETE SET NULL
);


-- Quick test:
INSERT INTO Employee (name) VALUES ('Alice'), ('Bo');
INSERT INTO ParkingSpot (location, employee_id) VALUES ('P1-A', 1), ('P1-B', NULL), ('P1-C', NULL);

DO $$ BEGIN
	INSERT INTO ParkingSpot (location, employee_id) VALUES ('P1-D', 1);
	RAISE EXCEPTION 'TEST FAILED: Second spot for employee rejected by UNIQUE';
EXCEPTION WHEN unique_violation THEN
	RAISE NOTICE 'OK (ex2): Second spot for the same employee rejected by UNIQUE'
END $$;
```

**Q2**: Which table did you put the foreign key on, and would it have worked just as well on the other table instead? Briefly justify your choice.

**A2**: I put the foreign key on **ParkingSpot** (`employee_id INTEGER UNIQUE REFERENCES Employee(employee_id) ON DELETE SET NULL`)
`UNIQUE` is what makes it 1:1. Without it, the same foreign key is a normal 1:N relationship.
`UNIQUE` ignores NULLs, so any number of spots can be assigned.

**Q3**: Would the other table work?

**A3**: Yes, participation is partial on both sides, so the foreign key column is allowed to be NULL whichever table it's on.


# Exercise 3: The University Schema, from ER to SQL
Recall the university ER model from Lecture 2's exercise sheet:

- The university offers several programs (e.g. DAT, SWT, DVML). Each program is organized into semesters, numbered within the program (e.g. semester 2 of DVML is informally called “DVML2”, semester 5 of DAT is “DAT5”); a semester only makes sense together with the program it belongs to.
- A course is offered as part of one or more semesters, and those semesters can belong to different programs – e.g. the same “Databases” course might be part of both DAT’s semester 5 and SWT’s semester 3.
- A teacher teaches one or more courses. A course is taught by one or more teachers (at least one).
- Each semester has exactly one teacher assigned as its coordinator. A teacher coordinates at most one semester (possibly none).
- A student is enrolled in exactly one program.
- A student is registered for (“takes”) many courses.
- For every course a student takes, the student can have zero or more grades on record for that course, each with the date of the exam it was awarded for – a student can sit the exam for the same course more than once (e.g. after failing).

Draw a full ER diagram that captures all of this. For every entity, list its attributes and mark the key (or partial key, for a weak entity). For every relationship, give its name, the entities it connects, its cardinality, and the participation constraint on each side.

_Hint: three things need extra care here. (1) Semester is a weak entity, the same way Topic was in Exercise 3 – work out what it depends on. (2) One fact here cannot be modelled as a plain relationship with attributes, the same way Borrows could not in the lecture – and it needs something extra beyond just being promoted to an entity. Which fact, and why? (3) Look for a relationship here that is 1:1, not 1:N or M:N._

```SQL
CREATE SCHEMA ex3;
SET search_path TO ex3;

CREATE TABLE Teacher(
	teacher_id SERIAL PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Program(
	program_id VARCHAR(10) PRIMARY KEY,
	name VARCHAR()
);
```