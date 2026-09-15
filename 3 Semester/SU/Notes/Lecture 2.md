

## Classification of Objects and Events
Classification is a daily human activity. Children use words to describe phenomena according to categories. Adults create order in their activities by classifying and structuring reality, and to communicate with others many classifications are part of daily life. Sometimes we use classification as a more conscious means of structuring or creating order. This is especially true for certain professionals, such as librarians and system developers. Librarians, for example, carefully structure libraries to make books and information easy to find.

In the class activity, the focus is on objects of the users' work with the goal of creating and selecting relevant abstractions.

Principle: Classify objects in the problem domain.

## Selecting Elements of a Problem Domain Model

Object (look for nouns): An entity with identity, behaviour and state.

Class (look for nouns): A collection of objects sharing structure, behavioural pattern and attributes.

Event (look for verbs): An instantaneous incident involving one or more objects.

## Classes Activity, The Event Table

Example, Event table for the Hair Salon System.

| Events    | Customer | Assistant | Apprentice | Appointment |
| --------- | -------- | --------- | ---------- | ----------- |
| reserved  | x        | x         |            | x           |
| cancelled | x        | x         |            | x           |
| treated   | x        |           |            | x           |
| employed  |          | x         | x          |             |
| resigned  |          | x         | x          |             |
| graduated |          |           | x          |             |
| agreed    |          |           | x          | x           |

![[Pasted image 20260915205807.png]]

A second example, using a Moodle style problem domain.

| Events       | Student | Lecturer | Course | ... |
| ------------ | ------- | -------- | ------ | --- |
| Signed up    | x       |          |        |     |
| Employed     |         | x        |        |     |
| Exam planned |         | x        | x      |     |
| Created      |         |          | x      |     |
| ...          |         |          |        |     |

## Affirmation Criteria

Used to assess whether identified events and objects are correctly chosen.

For events, ask:
- Is it instantaneous?
- Is it atomic?
- Is it identifiable when it happens?

For objects, ask:
- Can you identify objects?
- Is it unique?
- Are there multiple objects?
- Is there a suitable number of events?

## Structure Activity, The Class Diagram

Example class diagram for the Hair Salon System, showing structural relations between Customer, Appointment, Employee (Apprentice, Assistant), Day Schedule, Time Period, and Work, Free, Other.

![[Pasted image 20260915205716.png]]

### Classes, Moodle Example
Identified classes: Student, Semester, Room, Lecturer, Secretary, Page Resource, Document, File, Teaching Assistant, Employee, Quiz, Course, Calendar, Person, Semester Bulletin.

### Classes, Quick Clustering
The identified classes can be grouped into rough clusters before structuring them properly, for example a Persons group and a Coordination tools group.

## Structure Through a Class Diagram

Class: A description of a collection of objects sharing structure, behavioural pattern and attributes.

Cluster: A collection of related classes.

### Generalization (Is-A)
A general class (superclass) describes properties common to a group of specialized classes (subclasses).

Example: Student, Lecturer, Secretary, and Teaching Assistant are all specializations of Employee, which is itself a specialization of Person.

### Aggregation (Has-A)
A superior object (the whole) consists of a number of inferior objects (parts).

Example: A Semester has 1..* Students and 1..* Rooms. A Course has 1..* Page Resources and a Calendar has 0..* Semester Bulletins.

### Association ("Just-Related")
A meaningful relation between a number of objects. Not a defining property between objects.

Example: A Room is associated with 1..* Employees, and an Employee is associated with 0..* Courses. This is the lecturer's home made term for relations that are neither generalization nor aggregation.

Note: there are other relations possible, for example "a Course Has-A Lecturer" or "a Semester Has-A Semester Bulletin", but the example is kept simple for pedagogical purposes.

### Full Structure Class Diagram (Moodle example)
Combines all classes, clusters (Persons, Coordination tools), and their generalization, aggregation, and association relations into one diagram.

![[Pasted image 20260915205922.png]]
![[Pasted image 20260915205937.png]]






## Evaluation Criteria

Use Structures Correctly: Do not mix up generalization and aggregation. Use the correct linguistic expressions, Is-A, Has-A, etc.

Conceptually True Structures: Names and structural relations must reflect the users' understanding.

Structures Must Be Simple: Go for few classes and few important relations. Avoid unnecessary generalizations and aggregations. Ask whether the structure adds relevance.