
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
INSERT INTO Employee (name) VALUES ('Alice'), ()

```