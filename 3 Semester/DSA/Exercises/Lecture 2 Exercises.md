[[exerciseSheet-02-ER-Diagrams-SOLUTIONS.pdf]]

## Exercise 1: Warm-up - Entities, Attributes, and a Relationship

**Scenario**: A car rental company rents out cars to customers. Each car has a licence plate, a model name, and a daily rental price. Each customer has a name and a driver's licence number. When a customer rents a car, the company records the pickup date and the return date for that rental.

**Q1**: Which nouns are entities?

**A1**: Two entities, `Car` and `Customer`. "Rental" is arguably a third candidate, but at this stage it is most naturally modelled as a relationship between `Car` and `Customer`.

**Q2**: For each entity, which nouns/phrases are its attributes?

**A2**:
```MD
Car: licence_plate, model_name, daily_price
Customer: name, licence_number
```

**Q3**: Spot one relationship. Which two entities does it connect, and what would you name it?

**A3**: The relationship connects `Car` and `Customer`, natural name is `Rents`. The pickup date and return date belong to this relationship (a particular car being rented by a particular customer on a particular occasion), not to `Car` or `Customer` alone. This is an example of an attribute that lives on the relationship itself, like `Borrows` in the library example from the lecture.

## Exercise 2: Classify the Attributes

**Scenario**: A `Lecturer` entity with these attributes:
```MD
staff_id
full_name (made up of first_name and last_name)
email
office_phone_numbers (a lecturer may have more than one)
years_of_service (calculated from start_date and current date)
start_date
```

**Q**: Classify each as simple, composite, multi-valued, or derived.

**A**:

| Attribute            | Kind         | Why                                                            |
| -------------------- | ------------ | -------------------------------------------------------------- |
| staff_id             | simple       | single atomic value                                            |
| full_name            | composite    | made up of first_name and last_name                            |
| email                | simple       | one atomic value, nothing said about multiple emails           |
| office_phone_numbers | multi-valued | a lecturer can have more than one at the same time             |
| years_of_service     | derived      | computed from start_date and current date, not stored directly |
| start_date           | simple       | single atomic date value                                       |

## Exercise 3: Weak Entities - Course, Topic, and Professor

**Scenario**: A `Course` has a `course_id` and a `title`. Each course is organized into one or more weekly `Topics`, each `Topic` has a `topic_number`, `topic_title`, and `duration_minutes`, but a topic only makes sense together with its course, and `topic_number` is only unique within its own course. Every course also has exactly one `Professor` responsible for teaching it, identified by `professor_id` and `name`, and the same professor can teach several courses.

**Q1**: Which entity is the weak entity?

**A1**: `Topic` is the weak entity. It has no attribute of its own that uniquely identifies it across the whole database, and it cannot exist without being linked to an owning `Course`. `Course` and `Professor` both have their own id (`course_id`, `professor_id`) and are strong entities.

**Q2**: What is `Topic`'s partial key? Why is it not enough on its own?

**A2**: The partial key is `topic_number`. On its own it's not enough because `topic_number` only distinguishes topics within the same course, topic 1 of one course and topic 1 of another course are two different rows. `Topic`'s real key is the composite `(course_id, topic_number)`.

**Q3**: Describe the identifying relationship between `Course` and `Topic`.

**A3**: The identifying relationship is `Covers`, between `Course` and `Topic`, cardinality `1:N` (one course covers many topics, one topic belongs to exactly one course). Participation is total on both sides. A topic cannot exist without its course. A course also requires at least one topic (since the scenario says each course is organized into one or more topics), so `Course`'s participation is total too.

**Q4**: Is the `Professor`-`Course` relationship (also 1:N) identifying? Is `Course` weak with respect to `Professor`?

**A4**: No on both counts. `Teaches` (`Professor`-`Course`) is an ordinary, non-identifying `1:N` relationship, and `Course` is not weak with respect to `Professor`. `Course` already has its own key, `course_id`, that uniquely identifies it with no help from `Professor`.

**General rule**: being on the "many" side of a 1:N relationship is necessary but nowhere near sufficient for weakness. Most 1:N relationships (like `Teaches` here) connect two perfectly ordinary strong entities. An entity is weak only if both hold:
- it has no attribute or attribute combination of its own that is globally unique
- it cannot be identified, even in principle, without referring to the owning entity's key

## Exercise 4: Spot the Problem - A Flawed Car-Rental ER Model

**Flawed description**: "I have an entity `Car` with attributes `licence_plate`, `model_name`, and `daily_price`. I have an entity `Customer` with attributes `customer_id`, `name`, and `licence_number`. Every customer can have several phone numbers, so I gave `Customer` a simple attribute called `phone_number`. I connected `Car` and `Customer` with a relationship `Rents`, which I made one-to-one, since at the moment a customer picks up a car, it's just the one customer and the one car. I also added a `RentalBranch` entity, for the pickup location, with just one attribute, `city`, I didn't give it an id, since I don't really need to look branches up individually."

**Problems found**:

**1. Multi-valued attribute drawn as simple**: `phone_number` on `Customer` holds several values per customer, but is declared as simple. It should be multi-valued, e.g. `phone_numbers`.

**2. Missing identifying attribute**: `RentalBranch` has only `city` and no id. If two branches can be in the same city, or the branch needs to be referenced from a rental record, it needs its own key, e.g. `branch_id`. With a `branch_id` of its own, it is a normal strong entity, not a weak one. The classmate's instinct to skip giving it an id is the mistake.

**3. Wrong cardinality**: `Rents` is declared 1:1 between `Car` and `Customer`. But over time, one customer can rent many different cars, and one car can be rented by many different customers, the relationship should be M:N, with pickup/return dates as attributes on the relationship (as in Exercise 1) to distinguish one rental occasion from another. A 1:1 reading only holds for a single instant in time, which is not what an ER model captures.

**4. Missing entity/relationship for the branch link**: even after fixing `RentalBranch` to have a key, the description never says how `RentalBranch` connects to the rest of the model, e.g. a `Located_At` or `Picked_Up_From` relationship between `RentalBranch` and `Car` is missing entirely and should be added.

## Exercise 5: Designing the Library ER Model

**Requirements**:
- Library holds books. Each book may exist as one or more physical copies. Each copy's status (available, on loan, lost) is tracked separately.
- Each book may have one or more authors, an author may have written several books.
- Members can borrow copies of books.
- For every loan, record when the copy was borrowed, when it is due back, and when (if at all) it was returned.
- Books can belong to more than one genre.
- Books need title, publication_year, isbn. Authors need name and birth_year. Members need name, email, join_date.

**Entities and attributes**:

| Entity | Attribute | Kind | Key |
|---|---|---|---|
| Book (strong) | book_id | simple | PK |
| | title | simple | |
| | isbn | simple | |
| | publication_year | simple | |
| | genre | multi-valued | |
| Copy (weak, owned by Book) | copy_number | simple | partial key |
| | status | simple | |
| Author (strong) | author_id | simple | PK |
| | name | composite (first_name + last_name) | |
| | birth_year | simple | |
| Member (strong) | member_id | simple | PK |
| | name | composite (first_name + last_name) | |
| | email | simple | |
| | join_date | simple | |
| Borrows (promoted from a relationship with attributes) | member_id | simple | PK, FK |
| | copy_number | simple | PK, FK |
| | loan_date | simple | |
| | due_date | simple | |
| | return_date | simple | |
| | is_overdue | derived | |

**Relationships**:

| Name | Entities | Cardinality | Participation |
|---|---|---|---|
| Writes | Book - Author | M:N | total on both sides |
| Has (identifying) | Book - Copy | 1:N | Copy total, Book partial |
| (none) | Member - Borrows | 1:N | Borrows total, Member partial |
| (none) | Copy - Borrows | 1:N | Borrows total, Copy partial |

**Notes**:

`Copy` is a weak entity. Two copies of the same book are distinguished only by `copy_number`, which is not globally unique on its own (copy 1 of book_id=5 and copy 1 of book_id=9 are different rows). Copy's full key is `(book_id, copy_number)`. `Has` is its identifying relationship, `Copy` has total participation (a copy cannot exist without its book), `Book` has partial participation (a book record could exist with zero physical copies currently on the shelf, e.g. all lost).

`genre` on `Book` is multi-valued, a book can have several genres.

`Borrows` began life as a relationship with attributes (`loan_date`, `due_date`, `return_date`) between `Member` and `Copy`, a loan is a fact about a particular member borrowing a particular copy on a particular occasion, not about `Member` or `Copy` alone. Crow's foot notation has no way to attach an attribute to a relationship line, so `Borrows` is promoted to its own entity, keyed by `(member_id, copy_number)` and linked to `Member` and `Copy` by two ordinary 1:N relationships instead of one M:N relationship. `is_overdue` is a derived attribute on `Borrows`, computed from `due_date` vs today, only meaningful while `return_date` is still empty.

`Writes` has total participation on both sides because every book in the library must have at least one author on record, and every author on record must have written at least one book in the collection.

## Exercise 6: A Larger Model - University

**Requirements**:
- University offers several programs (DAT, SWT, DVML). Each program is organized into semesters, numbered within the program (semester 2 of DVML is "DVML2"). A semester only makes sense together with its program.
- A course is offered as part of one or more semesters, and those semesters can belong to different programs.
- A teacher teaches one or more courses. A course is taught by one or more teachers.
- Each semester has exactly one teacher assigned as its coordinator. A teacher coordinates at most one semester, possibly none.
- A student is enrolled in exactly one program.
- A student takes many courses.
- For every course a student takes, the student can have zero or more grades on record for that course, each with the exam date. A student can retake the exam for the same course.

**Entities and attributes**:

| Entity | Attribute | Kind | Key |
|---|---|---|---|
| Teacher (strong) | teacher_id | simple | PK |
| | name | composite | |
| | email | simple | |
| Program (strong) | program_id | simple | PK |
| | name | simple | |
| Semester (weak, owned by Program) | semester_number | simple | partial key |
| | start_date | simple | |
| Course (strong) | course_id | simple | PK |
| | title | simple | |
| | ects | simple | |
| Student (strong) | student_id | simple | PK |
| | name | composite | |
| | email | simple | |
| Grade (weak, owned by Student and Course, promoted from a relationship with attributes) | exam_date | simple | partial key |
| | grade_value | simple | |

**Relationships**:

| Name | Entities | Cardinality | Participation |
|---|---|---|---|
| Teaches | Teacher - Course | M:N | total on both sides |
| Coordinates | Teacher - Semester | 1:1 | Semester total, Teacher partial |
| Has (identifying) | Program - Semester | 1:N | Semester total, Program partial |
| Enrolled_In | Program - Student | 1:N | Student total, Program partial |
| Takes | Student - Course | M:N | partial on both sides |
| Offered_In | Course - Semester | M:N | total on both sides |
| (none) | Student - Grade | 1:N | Grade total, Student partial |
| (none) | Course - Grade | 1:N | Grade total, Course partial |

**Notes**:

`Semester` is a weak entity. `semester_number` only distinguishes semesters within the same program, semester 2 of DVML and semester 2 of DAT are different rows. Its full key is `(program_id, semester_number)`, `Has` is its identifying relationship.

`Grade` began life as a relationship with attributes (`grade_value`, `exam_date`) between `Student` and `Course`, following the same reasoning as `Borrows`, it is promoted to its own entity. Unlike `Borrows`, `(student_id, course_id)` alone is not enough as a key, since the scenario allows a student to sit the exam for the same course more than once, more than one `Grade` row can exist for the same `(student, course)` pair. `exam_date` is added as an extra partial key to tell repeated attempts apart. `Grade`'s full key is `(student_id, course_id, exam_date)`.

`Coordinates` is a rare 1:1 relationship. Every semester requires exactly one coordinating teacher (`Semester`'s participation is total), but a teacher coordinates at most one semester and might coordinate none at all (`Teacher`'s participation is partial).

`Student` sits on the many side of `Enrolled_In` (a 1:N relationship from `Program`), but `Student` is not a weak entity, it already has its own key, `student_id`.

There is no `semester` attribute directly on `Takes`. Which semester(s) a course belongs to is already captured by `Offered_In`, repeating it on `Takes` would just be redundant, unsynchronized data.

## Exercise 7: Model a Domain of Your Choice

**Task**: Model any domain (e.g. your semester project) as an ER diagram, aiming for:
- at least 4 entity types
- at least 1 weak entity, with identifying relationship and partial key marked
- at least 1 M:N relationship
- every entity's attributes listed with key/partial key marked, and kind noted for anything not simple
- every relationship named, with entities connected, cardinality, and participation on each side

**Self-check checklist**:
```MD
[ ] At least 4 entity types
[ ] At least 1 weak entity, with partial key and identifying relationship marked
[ ] At least 1 M:N relationship
[ ] Every attribute's kind is clear (simple attributes need no label, composite/multi-valued/derived ones should be marked)
[ ] Every relationship has a cardinality and a participation constraint on each side
```

**Note**: this exercise is open-ended, no single fixed solution. When self-checking, make sure the weak entity is genuinely weak (cannot be identified by its own attributes alone, depends on an owner entity), and the M:N relationship is genuinely M:N and not actually a 1:N relationship in disguise.