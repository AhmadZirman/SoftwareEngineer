[[SD03 - Behaviour.pdf]]

## Previous Session
- Classes and Events, selecting elements of a problem domain model and representing them in an event table
- Structure, describing relations between classes through a class diagram

## Behavioural Patterns - State Charts
A behavioural pattern describes the possible event traces for all objects in a class. State charts are used to represent them, built from four basic forms.

### Sequence
Events in a set occur one by one.
State 1 goes through events a, b, ... z in order and ends in State n.

### Selection
Exactly one out of a set of events occur.
From State 1, only one of the events a, b, ... z happens.

### Iteration
An event occurs zero or more times.
Event a loops back to State 1, so it can happen any number of times.

### Conditional
Transition happens when a stated condition becomes true.
From State 1, event a leads to State 2, event b leads back, and the condition [z] decides when event c can happen.

## An Example: Employee
- The lifespan starts with the event Employed and ends with the event Fired
- In between, the employee is in the superstate Employment active
- Inside Employment active, the employee is Working by default
- From Working, the event Left leads to either On leave or On holiday, and Returned leads back to Working
- Assigned and Unassigned are iterations, so they can occur any number of times while Working

## Tips and Tricks
For describing behavioural patterns for a class:
- Events as the point of departure
- Identify first and last events in an object's lifespan
- Don't go for "Create" and "Delete" (system events)

Also, ask yourselves:
- Is the overall form structured or unstructured?
- Which events occur in a sequence?
- Are there any alternative events?
- Can a given event occur more than once?

## Sufficient, but Simple
"Sufficient, but simple." (Mathiassen et al., 2000)

When making behavioural patterns there are two goals that sometimes conflict:
- The pattern should be sufficiently precise to describe all legal, and thus all illegal, event traces
- The pattern should provide an overview and thus be as simple as possible

The conflict can usually be avoided by excluding very specialized and detailed aspects from the diagram. When necessary, describe typical behaviour in a simple diagram and supplement it with a comment about the specialized aspects. Start by describing a typical object's behaviour and integrate more unusual event traces later.

## Update the Event Table
Once the behavioural patterns are made, the event table is updated. The checkmarks are replaced with symbols that show how often each event can occur in the behavioural pattern.
- \+ means the event occurs at most once (sequence or selection)
- \* means the event can occur zero or more times (iteration)

## Behaviour Example (Moodle)
Based on the Moodle example from the previous lecture.

### Class Structure
- Person is the superclass of Student and Employee
- Employee is the superclass of Teaching Assistant, Lecturer and Secretary
- Teaching Assistant is also a specialization of Student

### Employee
Same state chart as the Employee example above. Employed and Fired as first and last events, with Working, On leave and On holiday inside Employment active.

### Lecturer
The Lecturer specializes the Working part of the Employee behaviour.
- Lecturer active contains two states, Lecturing and Researching
- Task switched moves the lecturer between Lecturing and Researching
- While Lecturing, Assigned, Unassigned and Graded can occur
- While Researching, Paper published and Study registered can occur
- Lecturer inactive covers the periods where the lecturer is not active, with Assigned still possible

### Updated Event Table

| Events | Employee | Lecturer | Student | Person |
|---|---|---|---|---|
| Signed up | | | + | |
| Graduated | | | + | |
| Employed | + | + | | |
| Assigned | * | * | * | * |
| Fired | + | + | | |
| Unassigned | * | * | * | * |
| Graded | | * | * | |
| Course completed | | | * | |
| Left | * | * | * | * |
| Returned | * | * | * | * |
| Task switched | | * | | |
| Study registered | | * | | |
| Paper published | | * | | |

## Attributes
An attribute is a descriptive property of a class or an event.

### Three Types of Attributes
- Information connected to single events
- General information connected to objects as a whole
- Derived information based on other attribute values

### Identifying Attributes for a Class
Ask yourselves:
- What are the general characteristics of the class?
- How is the class described in the Problem Domain?
- What basic data must be captured from this class?
- What results from events must be captured?
- What states can the object be in?

### Example: Employee
Class attributes:
- Name
- Address
- Phone
- Email
- Holiday weeks
- Leave weeks

Event attributes:
- Employed (date, institute)
- Fired (date)
- Left (date)
- Returned (date)
- Assigned (date, task type)
- Unassigned (date)