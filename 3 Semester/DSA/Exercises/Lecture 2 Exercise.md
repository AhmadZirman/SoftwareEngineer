
### The Weak Entity Test (2 conditions, BOTH must hold)
Don't just say "it's on the many side of a 1:N relationship." That's not enough.

An entity is weak only if:
1. It has no attribute of its own that's globally unique
2. It cannot be identified without referencing its owner's key

Quick check: does it have its own ID that works app-wide? Then it's strong, even if it sits on the "many" side of a 1:N relationship.

| Example | Weak? | Why |
|---|---|---|
| Topic (per Course) | Weak | topic_number repeats across courses |
| Course (per Professor) | Strong | has its own course_id, works fine alone |
| Copy (per Book) | Weak | copy_number repeats across books |
| Student (per Program) | Strong | has its own student_id |

Weak entity's real key = owner's key + partial key, always.
Copy becomes (book_id, copy_number). Semester becomes (program_id, semester_number).

### Relationship-with-Attributes Always Gets Promoted to an Entity
Crow's foot cannot attach an attribute to a line. If a fact belongs to the relationship itself, not either entity, promote it.

```MD
M:N relationship with attributes  becomes its own entity
                                  linked by two 1:N relationships instead
```
Example: Borrows (loan_date, due_date, return_date) between Member and Copy.

Normal case: the promoted entity's key is the two FKs combined.
Borrows key = (member_id, copy_number).

Special case, repeatable events need an extra partial key:
If the same pair can legitimately repeat (e.g. a student re-sits an exam after failing), (FK1, FK2) alone isn't unique enough. Add a third partial-key attribute to tell repeats apart.

Grade key = (student_id, course_id, exam_date), not just (student_id, course_id), because a student can retake the same course's exam.

Rule of thumb: ask "can this same pair happen more than once over time?" If yes, add a disambiguating attribute to the key.

### Cardinality Sanity Check
Ask: "could this be true only for one instant, or is it true always?"

"At the moment of pickup, it's one car and one customer" feels like 1:1, but that's wrong. Real question: over time, can one customer rent many cars? Can one car be rented by many customers? If yes to both, it's M:N.

1:1 is rare. Only use it when both sides are genuinely capped at one, permanently (e.g. Teacher and Semester coordinator: one teacher coordinates at most one semester, one semester has exactly one coordinator).

### Simplified Attribute Classification
| Question | Kind |
|---|---|
| Single atomic value? | Simple |
| Made of named sub-parts (first/last name)? | Composite |
| Can hold several values at once? | Multi-valued |
| Computed from other stored data, never stored itself? | Derived |

An attribute is simple, composite, or multi-valued, pick exactly one of these three, and separately, may or may not also be derived.

### Don't Duplicate Data Across Relationships
If fact X is already captured by relationship A, don't repeat it as an attribute on relationship B. It'll drift out of sync.

Example: which semester a course belongs to is captured by Offered_In (Course-Semester). Don't also add a semester attribute to Takes (Student-Course).

### Full Worked Patterns (for quick pattern matching in exam)

Pattern A, simple weak entity:
Owner (strong) with a 1:N identifying relationship to Weak entity (partial key)
e.g. Book to Copy, Program to Semester, Course to Topic

Pattern B, relationship with attributes:
EntityA and EntityB in an M:N relationship with facts about the pairing, promote to AB (PK: FK_A plus FK_B)
e.g. Member and Copy become Borrows

Pattern C, relationship with attributes and repeatable events:
Same as B, but add one more partial key for "which occurrence"
e.g. Student and Course become Grade (student_id, course_id, exam_date)

Pattern D, rare 1:1:
Both sides capped at exactly one (or zero), permanently, not just momentarily
e.g. Teacher and Semester.coordinator