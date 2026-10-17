[[SD05 - Usage.pdf]]

## Application Domain
The organization that administrates, monitors or controls a problem domain.
The user interacts with the system inside the application domain.

## Relative Stability
The parts of a system differ in how stable they are over time.
- Model is the most stable
- Functions are less stable
- Interfaces are the most transient

### Architecture
The generic architecture from the course book (Figure 10.5) has the following components:
- Interface, split into User interface and System interface
- Function
- Model
- Technical platform, with UIS, DBS and NS

The interface depends on the function and the model, and the function depends on the model. All of them use the technical platform.

"When you change the model, you must change the functions and interfaces." (Mathiassen et al., 2000)

## Use Case
A pattern for interaction between the system and actors in the Application Domain.

### Use Case Specification
Brief, precise and structured text focusing on the actor, actions and system states.
- Use Case Title
- Use case: Actions performed by an actor that leads to changes in system states, and so on

### Procedural Use Case Pattern
A UML state chart where the actions happen in a fixed order.
System state 1 goes through Action 1, Action 2 and so on until System state n is reached. Action n+1 then continues from there.

### Material Use Case Pattern
A UML state chart built around a general state.
A start action leads into the general state, where different processes (Process x, Process y) can be performed and return to the general state. A final action ends the use case.

### Example: Cash Withdrawal
State chart for "cash withdrawal" from the course book (Figure 6.5).
- Insert card leads to Card inserted
- Prompt for code leads to Waiting for code
- Enter code leads to Code entered
- From Code entered, reject ends the use case, and code accepted leads to Waiting for amount
- Choose amount leads to Amount chosen
- From Amount chosen, amount not approved goes back to Waiting for amount, and approve amount leads to Amount approved
- Payment ends the use case
- Cancel from Waiting for amount ends the use case

### Example: Moodle, Create Session Topic
Use Case: Create session topic is initiated by a lecturer when preparing the next lecture. The lecturer opens the Moodle page and presses "Turn editing on". Then selects "Add topics" followed by the number of new topics to be added in the lecture plan. The new topic(s) are then listed ready to be edited.

State chart:
- Open course page leads to Course page open
- Turn editing on leads to Editing course page
- Add topic leads to Number of topics, where Increase/Decrease no. of topics can happen any number of times
- Approve/Cancel goes back to Editing course page
- Turn editing off goes back to Course page open
- Close course page ends the use case, from either Course page open or Editing course page

## Evaluate Systematically

### Who and What
Identify individual actors and their system interactions for each use case.

### Simple and Coherent Whole
Must be sufficiently detailed, i.e. developers must be able to identify requirements for functions and interfaces.

### Not too Detailed
Further elaboration happens through detailed user interface designs, e.g. wireframes, prototypes etc.

## Actor
An abstraction of users or other systems that interact with the target system.

### Identify Actors
To identify actors, you must determine the division of labor and the task-related roles in the Application Domain.

### Actor Description
Actor name
- Goal: The actor's role in the target system. E.g. "A lecturer needs to manage teaching activities..."
- Characteristics: Important aspects of the actor's use of the system. E.g. "There are several lecturers that are supposed to use the system to provide lecture plans with teaching materials in order to align with student schedules and learning goals..."
- Examples: General characteristics can be supplemented by concrete examples, e.g. short Persona-like descriptions.

### Dissimilarities
A criterion for determining different actors is the dissimilarity of roles, as expressed by the use cases in which actors are involved. Much like Classes that share all of their Events would be candidates for merging.

## Use Case Diagram
Overview of relations between actors and use cases.

### Example: Automatic Payment System
Use case diagram from the course book (Figure 6.3).
- Actors: Account owner, Creditor, Liquidity monitor, Administrator
- Use cases: payment, cash withdrawal, money transfer, account information, credit information, registration, monitoring, error correction
- Each actor is connected with lines to the use cases it takes part in, e.g. Account owner to cash withdrawal, and Administrator to registration, monitoring and error correction