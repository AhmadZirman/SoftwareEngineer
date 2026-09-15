
## Course Structure
- 11 Sessions
- Object Oriented Analysis and Design
- Video material available this semester
- Interactive — no lengthy talks, few slides, emphasis on exercises
- Exam: Written, 7-grade scale, multiple choice, 3 hrs, analog/printed aids only

## Goals
This module has the purpose of giving the student knowledge and skills in modelling the structure and behaviour of objects within problem and application domains. Models will be applied to create a component design specifying the architecture of a given system.

## What's a Model?
A representation of a part of the real world.
Emphasises certain aspects, i.e. those useful for the current purpose.

### Different Models (example)
Different models of the same building (AAU Cassiopeia) can emphasize different things — a simplified map, a detailed map, or a satellite photo — depending on what's useful for the current purpose.

![[Pasted image 20260914_different_models.png]]

### A Part of a Model — Behind the Scenes
Example class diagram for the Hair Salon system (from the course book), showing structural relations between classes: Customer, Appointment, Employee (Apprentice/Assistant), Day Schedule, Time Period, Work/Free/Other.

![[Pasted image 20260914_hair_salon_class_diagram.png]]

## Why Modelling?
- Provides Overview
- Supports Communication
- Prompts Questions
- Ensures Structure
- Supports Collaboration

## UML
Unified Modelling Language, creating OO structures since the late 90's.

### Standardized Representations
- Event tables
- State charts
- Class diagrams
- Actor tables
- Use case diagrams
- Sequence diagrams

![[Pasted image 20260914122131.png]]

## Main Activities and Results
The overall SD process cycles between four activities:
- **Problem-domain analysis** → produces a **Model** (Class, Structure, Behaviour)
- **Application-domain analysis** → produces **Requirements for use** (Usage, Functions, Interfaces)
- **Architectural design** → produces **Specifications of architecture** (Criteria, Components, Processes)
- **Component design** → produces **Specifications of components** (Model component, Function component, Connecting components)

![[Pasted image 20260914_main_activities.png]]

## A Classic Sequential Software Development Lifecycle (SDLC)
Requirements Engineering → Design Engineering → Coding → Testing → Operation and Maintenance

![[Pasted image 20260914122217.png]]

## A Classic Agile Software Development Lifecycle (SDLC)
Repeating iterations, each cycling through: Plan → Requirements → Design → Build → Test → Feedback

![[Pasted image 20260914122250.png]]

## Processes as Per the Course Book
- **Sequential**: Problem-domain analysis, Application-domain analysis, Architectural design, Component design, Programming, and Quality assurance run across Phase 1 → Phase 2 → Phase 3, producing an Analysis document, then a Design document, then Software.
- **Iterative**: The same activities repeat across Phase 1 ... Phase n, each phase producing a Software release.

![[Pasted image 20260914122325.png]]

## AI Software Development Lifecycle (AI-DLC)
Tools: Claude Code, OpenAI Codex, MS Co-Pilot, etc.

Core loop: **Intent → Plan → Human Gates → Agent Loop**, where the Agent Loop consists of:
- Generate
- Verify
- Correct

## Problem Domain and Application Domain
![[Pasted image 20260914_problem_application_domain.png]]

### Problem Domain
> Part of the context that is administered, monitored or controlled by a system

### Application Domain
> The organization that administrates, monitors or controls a problem domain

## System Definition
A concise description of a computerized system expressed in natural language.

### FACTOR criterion
- **Functionality**: System functions to support the application domain tasks
- **Application Domain**: Parts of an organization that administrate, monitor or control a problem domain
- **Conditions**: Conditions under which the system will be developed and used
- **Technology**: Technology used to develop the system and technology on which the system will operate
- **Objects**: Main objects in the problem domain (consider the types of objects, i.e. the classes)
- **Responsibility**: The system's overall responsibility in relation to its context

## Objects and Classes
Consider: Objects that we can identify that should be administrated by the system.

### Example: Movie Theater
A class called `Movie` is created to hold the information of all the movies that have been shown and will be shown in the movie theater. Each movie in the movie theater will be an instance/object in the class.

**Objects**: An entity with identity, state, and behavior. An object belongs to a class.
**Classes**: A description of a collection of objects sharing structure, behavioral pattern, and attributes. Each class consists of a number of objects.

| Class    | Example Objects (instances)                                  |
| -------- | -------------------------------------------------------------- |
| Movie    | Avatar, The Godfather, Hard to Kill                             |
| Customer | Sophie James, Olivia Rose, Michael David                        |
| Showing  | Tuesday 20-11-2025 6pm, Friday 10-10-2025 5pm, Friday 10-10-2025 7pm |
| Ticket   | VIP, Standard Adult, Standard Child                              |
| Theater  | Nordic Film Aalborg, Nordic Film Aarhus                          |
