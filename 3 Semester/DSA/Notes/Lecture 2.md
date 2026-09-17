[[Software Engineer/3 Semester/DSA/Slides/Lecture2-ER-Diagrams.pdf]]
# Recap: Keys and Integrity
Quick recall before building on it - see [[Software Engineer/3 Semester/DSA/Slides/Lecture1-Intro-RelationalModel-typst.pdf]] for full detail:
- **Primary key** - uniquely identifies each row
- **Foreign key** - points at another table's primary key (or a unique column); how relationships are represented
- **Domain integrity** - values match their declared type
- **Entity integrity** - every primary key is unique and non-null
- **Referential integrity** - every foreign key matches an existing row, or is NULL

Today's question: before writing tables and foreign keys, how do we find the "right" tables?

### Why Model Before We Build?
Jumping straight to `CREATE TABLE` works for toy examples, but real domains have optional properties, relationship data, and dependencies - skipping design tends to produce schemas that break on edge cases (e.g. "can a book have several authors?").

An **ER diagram** is a DBMS-independent blueprint of entities, attributes, and relationships - readable by a domain expert who has never heard of SQL. It's exactly what gets translated into `CREATE TABLE` statements later in the course.

## Entities and Attributes

#### Entity vs. Entity Type
- **Entity**: a distinguishable real-world thing to record data about - a specific book, author, or member
- **Entity type (entity set)**: a collection of entities sharing attributes - `Book` is an entity type; one shelved copy of a specific title is an entity belonging to it
- Exactly parallel to table and row in the relational model - same idea, one abstraction level earlier

Running example all lecture: a library system with `Book`, `Copy`, `Author`, `Member`.

#### Notation
Entity type = rectangle labelled with its name, key attributes listed inside the box below a rule (crow's foot style - see below).
```MD
Book
book_id : PK
title
isbn
```
A **strong entity** is a single-bordered rectangle. A **weak entity** (later) gets a heavier border / distinct colour, since it can't stand on its own.

### Attributes: Four Kinds

#### Simple (Atomic)
Cannot be split further - e.g. `Book.isbn`, `Book.publication_year`, `Member.email`. Most attributes are this kind, listed plainly in the box.

#### Composite
Groups several simple attributes under one name - `Author.name` decomposes into `first_name` and `last_name`, useful when you need to query/sort on the parts separately. In crow's-foot boxes this is just shown as `name (first_name, last_name)`.

#### Multi-valued
Can hold several values per instance - `Book.genre` is multi-valued (a novel can be both "Fantasy" and "Young Adult"). Classical notation: a heavier/double-outlined oval.

**Strong hint for the relational model**: a multi-valued attribute cannot become a single column - it becomes its own table later.

#### Derived
Computed from other attributes on demand, never stored - classic example `Person.age`, derived from `birth_date` and today's date (never stale). Classical notation: a dashed oval.

Our own example lives on a *relationship*, not an entity: `Borrows.is_overdue`, derived from `due_date` and `return_date`.
```MD
is_overdue → due_date < today() AND return_date IS NULL
```
A value that can go stale is a bug magnet - computing it on the fly is always correct.

### Keys, Revisited
Same definitions as [[Software Engineer/3 Semester/DSA/Slides/Lecture1-Intro-RelationalModel-typst.pdf]], just applied at the ER level:
- **Superkey**: any attribute set that uniquely identifies an instance
- **Candidate key**: a minimal superkey
- **Identifying attribute**: the candidate key the designer chose as that entity type's primary key - underlined inside the entity box (e.g. `book_id`, `author_id`, `member_id`). A weak entity does not get one on its own (more below).

### Two Notations: Chen vs. Crow's Foot
The textbook (Silberschatz et al.) uses **Chen notation** - relationships as diamonds, attributes as ovals branching off entities. Every actual tool (draw.io, PlantUML) uses **crow's foot** instead - relationships as a direct line, no diamond; attributes listed inside the entity box.

**This course uses crow's foot for everything going forward** - Chen notation only shows up for comparison/teaching purposes.

## Relationships, Cardinality, and Participation

#### What Is a Relationship?
- **Relationship**: an association between entity instances - `Writes` links `Book` and `Author`; `Borrows` links `Member` and `Copy`
- **Relationship type (set)**: all instances of the same kind, just like entity type/entity

Relationships are first-class citizens, on equal footing with entities.

#### Degree of a Relationship
- **Binary** - two entity types (e.g. `Book`-`Author`) - the only kind built in depth this course
- **Unary** - an entity related to itself (e.g. `Employee` "manages" `Employee`)
- **Ternary** - three entity types at once (e.g. `Supplier`-`Part`-`Project`)

Binary relationships cover the overwhelming majority of real designs.

### Cardinality
Answers: "for one instance of A, how many instances of B can it relate to, and vice versa?" This is the single most consequential design decision - later it determines whether a relationship becomes a foreign key or a whole new table.

Three patterns:

**1:1** - one instance of A relates to at most one instance of B, and vice versa.
Example: a person has at most one passport; a passport belongs to exactly one person.
```MD
Person -||-- Holds --||- Passport   // double bar at both ends
```
Rarest pattern in practice - often a sign the two entity types should just be merged.

**1:N** - one instance of A relates to many instances of B; each B relates to only one A. The most common cardinality.
```MD
Programme -||-- Enrols --|<- Student   // double bar on "one" side, crow's foot on "many" side
```
This is exactly `Student.programme_id REFERENCES Programme.programme_id` - the crow's foot side is where the foreign key lives later.

**M:N** - many instances of A relate to many instances of B, in both directions.
```MD
Student ->|-- Takes --|<- Course   // crow's foot at both ends
```
Our own example: `Book` and `Author` via `Writes` - a book may have several authors, an author several books.

### Crow's Foot Symbol Reference
Each symbol sits where the line touches an entity box, showing that entity's multiplicity (counted per one instance on the other end):
- **circle** = "zero" is allowed
- **bar** = "exactly one" (mandatory)
- **crow's foot** = "many"

Two symbols combine at each end: circle-or-bar on the inside (participation), nothing-or-crow's-foot on the outside (cardinality). The inner circle-or-bar is exactly *participation* (next).

### Participation: Total vs. Partial
A different question: "must every instance take part in this relationship at all?"
- **Total**: yes - every instance must relate to at least one instance on the other side
- **Partial**: no - an instance can exist without ever appearing in the relationship

Crow's foot: bar = mandatory (total), circle = optional (partial).

**Worked example - Book and Copy, related by Has:**
```MD
Book -||-- Has --*<- Copy
```
- Near `Book`: double bar → every `Copy` has exactly one `Book` → makes `Copy` **total**
- Near `Copy`: circle-plus-fork → a `Book` can have zero or many copies → makes `Book` **partial**

A `Copy` cannot exist without a `Book` (total), but a `Book` can sit in the catalogue with zero copies (partial).

### Attributes on Relationships
Sometimes a fact belongs to the relationship itself, not either entity. `Borrows` (`Member`-`Copy`): `loan_date`, `due_date`, `return_date` describe one loan event, not a permanent property of either side.

**Chen notation** can attach an attribute oval directly to the relationship diamond. **Crow's foot has no equivalent** - the fix is to promote the relationship to its own entity:

```MD
Member                    Copy
member_id : PK            copy_number
name                      status

Borrows  (promoted to its own entity)
member_id : PK, FK
copy_number : PK, FK
loan_date, due_date, return_date
```
The M:N relationship becomes two 1:N relationships instead. **This is exactly what happens when translating to SQL** - a many-to-many relationship always becomes its own table.

## Weak Entities and Identifying Relationships

#### Strong vs. Weak Entities
- **Strong entity**: has its own identifying attribute, exists independently - everything covered so far
- **Weak entity**: no attribute of its own is guaranteed unique - identified only by combining a **partial key** (discriminator) with its **owner** (identifying) entity

`Copy` is the running example: `copy_number` is only unique *within one book* - books 1 and 2 can both have a "copy 1." `Copy` means nothing without knowing which `Book` it belongs to.

**Why bother?** Could just give every `Copy` a global `copy_id` - many real systems do, as a shortcut. But weak entities capture something true: a copy's identity is inherently relative to its book, like "chapter 3" only means something once you say which book.

#### Identifying Relationships
The relationship connecting a weak entity to its owner.
- The weak entity has **total participation** - a `Copy` cannot exist without its `Book`
- The weak entity's identity depends on it - `copy_number` only disambiguates combined with the owning book

Notation: heavier-bordered, distinctly coloured rectangle (literal double border in Chen notation). Its partial key has **no underline** - alone, it doesn't uniquely identify anything.

**A weak entity's real key, once translated to the relational model, becomes a composite key: owner's key + partial key together.**

#### Worked Example: Order and OrderLine
Same shape of problem as `Copy`/`Book`:
- `Order` (strong) - `order_id` (PK), `order_date`
- `OrderLine` (weak) - `line_number` (partial key), `quantity`, `unit_price`
- `Contains` - the identifying relationship, 1:N

```MD
Order -||-- Contains --|<- OrderLine
```
Both ends mandatory here: every line needs exactly one order, AND every order needs at least one line (assumption). Standard shape of an identifying relationship: strong entity on one end, weak entity on the other, both total.

**Contrast with Book/Copy**: there, `Book`'s participation was *partial* (a book can sit with zero copies). What makes an entity weak is its own total participation + partial key - not the owner's participation.

## ER vs. UML Class Diagrams

Similar-looking, different purpose: **ER models persistent data**; **UML models software structure**, including behaviour.

| ER term | UML term |
|---|---|
| Entity / entity type | Object / class |
| Attribute | Attribute (field) |
| Relationship | Association |
| Cardinality (crow's foot) | Multiplicity (text, e.g. `0..*`) |
| Weak entity + identifying relationship | Composition (closest analogue, not identical) |
| (no equivalent) | Operations/methods, inheritance |

Key differences: UML class boxes add an operations compartment (ER has none - data only, no behaviour); UML multiplicities are text (`1`, `0..1`, `0..*`, `1..*`) not crow's-foot symbols; UML supports inheritance, classical ER has no standard notation for it.

## Tools for Drawing ER Diagrams
- **draw.io (diagrams.net)** - free, browser/desktop, dedicated ER shapes with crow's-foot connectors
- **PlantUML** - text-based, write a script and render it. Matches this course's crow's-foot syntax.

#### PlantUML Crow's-Foot Syntax
```plantuml title:PlantUML-ER-example
entity Book {
  * book_id : int <<PK>>
  --
  title : string
  isbn : string
}
entity Copy {
  * copy_number : int <<partial key>>
  --
  status : string
}
Book ||--o{ Copy : Has
```
- `||` = "exactly one"
- `o{` = "zero or many"

Same bar/circle/fork vocabulary as the crow's-foot legend, just written left-to-right.

## Java Connection: From ER to Classes
Language-agnostic (Java/Python/C# all work the same way):
- **Entity type → class**: `Book`, `Copy`, `Author`, `Member` each become a class; attributes become fields
- **Relationship → reference or collection**: `Book`'s `Has` toward `Copy` becomes a `List<Copy>` field; an M:N relationship like `Writes` typically becomes a collection on both sides
- **Weak entity's dependency on owner → a constructor or field requirement** - no `Copy` object without a `Book` reference

Revisited concretely with the Repository and DAO patterns later in the course.

## Full Worked Example: Library System
Built up across the lecture, piece by piece:

**Step 1 - entities only:**
```MD
Author   Book   Copy (weak)   Member
```

**Step 2 - add attributes:**
```MD
Author: author_id (PK), name
Book: book_id (PK), title
Copy: copy_number, status
Member: member_id (PK), name
```

**Step 3 - add relationships:**
```MD
Book --- Writes (M:N, total both sides) --- Author
Book --- Has (1:N, Copy's identifying relationship) --- Copy
```

**Step 4 - complete diagram:**
```MD
Author --- Writes --- Book --- Has --- Copy
                        |
                     Borrows
                        |
                     Member
```
`Borrows` also carries `loan_date`, `due_date`, `return_date`, and derived `is_overdue`.

**Final relationship summary:**
| Relationship | Cardinality | Participation |
|---|---|---|
| `Writes` (Author-Book) | M:N | Total both sides |
| `Has` (Book-Copy) | 1:N | Copy total, Book partial (Copy is weak) |
| `Borrows` (Member-Copy) | M:N | (carries attributes → becomes its own entity in crow's foot) |