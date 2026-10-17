[[SD07 - Functions.pdf]]

## Function
A facility for making a model useful for actors.

Functions determine what the system must do so actors may interact with your model of the Problem Domain, e.g. "Make schedule", "Make course", "Edit lecture topic" etc.

## Function Types
Function types express the relation between the Problem Domain model and the Application Domain.
- Update. Function that is activated by a Problem Domain event and results in a change in the model's state
- Signal. Function that is activated by a change in the model's state and results in a reaction (display for an actor or intervention in the Problem Domain)
- Read. Function that is activated by a need for information by an actor
- Compute. Function that consists of computation involving information provided by an actor or the model

Functions are often a mix of the above. "Pure" functions are usually simpler to implement, mixed functions more complex.

The system is divided into Interface (I), Function (F) and Model (M). The Application Domain (AD) and the Problem Domain (PD) connect to the system through these parts.

## Examples (Moodle)

### Update Example
Consider events. Event: Assigned.
In the Lecturer state chart, the event Assigned happens in the Problem Domain and changes the state of the Lecturer object in the model.

### Signal Example
Consider model states. State: Lecturing.
When a Lecturer object is in the state Lecturing, the system reacts by showing the lecturer's courses on the Moodle course overview.

### Read Example
Consider actor information needs, e.g. Lecturer state (holiday etc.).
The Secretary actor needs information from the model about an Employee.
- Employee attributes: Name, Address, Phone, Email, Holiday weeks, Leave weeks, Current state
- Related use cases: Check Employee State, Retrieve Students, Create Page

### Compute Example
Consider actor computational needs, e.g. find students from a given semester.
The Secretary actor needs the system to carry out a computation, for example in the use case Retrieve Students.

### Lecturer State Chart
- Assigned leads into Lecturer active, starting in Lecturing
- Task switched moves between Lecturing and Researching in both directions
- Graded can occur any number of times while Lecturing
- Study registered and Paper published can occur any number of times while Researching
- Unassigned leads from Lecturer active to Lecturer inactive
- Assigned leads from Lecturer inactive back to Lecturing

## Questions When Identifying Functions
"The source for identifying functions are partly the problem domain description, as expressed by its classes and events, and partly the application domain, as expressed by its use cases." (Mathiassen et al., 2000)

### Update (consider events)
- How is the event observed and how is it registered? In which use cases does this happen?
- Which objects, attributes and objects are affected by the event?

### Signal (consider model)
- What are the critical states for the model?
- What are the consequences when they occur?
- What signals does each critical state give rise to?

### Read (consider actors)
- Given the work of the actors, what do the actors need to know about the state of the model?
- Given the model, which object structures will the actors need information about?

### Compute (consider actors and model)
- Which computations do the actors need to have carried out?
- Does the computational basis come from the actors, model or both?

## Function List
"Detailed specifications are for special cases... specify functions as briefly as possible during analysis." (Mathiassen et al., 2000)

Functions are described briefly and informally in a list with their complexity and type.

### Example: Hair Salon (Figure 7.6)

| Function | Complexity | Type |
|---|---|---|
| Make schedule | Very complex | Update |
| Calculate schedule consequences | Complex | Signal |
| Find working hours from previous period | Medium | Read |
| Enter contents into schedule | Complex | Update |
| Erase schedule | Simple | Update |
| Query earlier schedules | Medium | Read |
| Make appointment | Medium | Update |
| Cancellation | Simple | Update |
| Query possible appointments | Complex | Read |
| Register treatment | Simple | Update |
| Create customer | Simple | Update |
| Query customer information | Medium | Read |
| Employment | Simple | Update |
| Retirement | Simple | Update |
| Update apprentice information | Simple | Update |

## Specifying Complex Functions
When you need a detailed specification more than a list. Three alternatives:

### Mathematical Expression
Output (o) as a function of input (i), o = f(i).
E.g. a function to compute number of students in a course.

### Pseudo Code
Retrieve list of students:
    if right to access
        locate all students on a course
        list students...

### Partitioning
Splitting a complex function into subfunctions.
E.g. subfunctions for "Checking credentials", "Find students related to course" etc.