

# Exercise 1.1: PD vs AD
Imagine a system that is intended to administer teaching activities at individual departments of a university (Department of Computer Science, Electronic Systems etc.). It should be able to handle and organize all central administrative processes around students and lecturers as well as the planning and delivery of courses. The system will be used by semester secretaries, lecturers and students. Note however, that booking of rooms will handled by a different centralised system as each department is able to book rooms across all other departments.

1. Identify objects relevant to consider for this system
	Based on the description, the following objects seem relevant to consider:
	- Student
	- Lecturer
	- Course
	- Course offering (a specific instancce/class of a course in a given semester)
	- Enrollment (a student's registration for a course)
	- Exam / assessment
	- Grade
	- Study Programme / Curriculum
	- Department (Computer Science, Electronic System, etc.)
	- Semester secretary (User/role)
	- Schedule / teaching plan (the *planning* of teaching, not room booking itself)

2. Discuss which objects belongs to the problem domain of this system, and what belongs to the application domain. Is there anything that belongs to both?
	
	**Problem Domain**
	- Student (as a data object, personal data, enrollments)
	- Lecturer (as a data objects, which courses they teach)
	- Course / Course offering
	- Enrollment
	- Exam and grade
	- Study programme
	- Department

	**Application Domain**
	- Semester secretaries (use the system to administer courses, enrollemtns, etc.)
	- Lecturers (use the system, e.g. to plan teaching or enter grades)
	- Students (use the system to enroll in courses, view their schedule, etc.)

	**Belongs to both?**
	- Student
	- Lecturer
	In the **problem domain**, they are objects the system hold data about (which courses a lecturer teaches, which courses as a student is enrolled in)
	
	In the **application domain**, they are simultaneously actors who actively use the system themselves (the lecturer enters grades, the student enrolled in a course)
	
	They are both the subject of administration and users of the system.


# Exercise 1.2: System Definition
Make a system definition of the system to administer a university (same system as imagined in exercise 1.1). Use **FACTOR** to formulate the system definition.

### FACTOR breakdown

**Functionality**  
The system supports registering and maintaining course information, enrolling students in courses, planning and scheduling the delivery of teaching, recording exams and grades, and handling general administrative processes related to students and lecturers.

**Application domain**  
The semester secretaries, lecturers, and students within the individual departments of the university (e.g. Department of Computer Science, Department of Electronic Systems) who carry out and support the administration and delivery of courses.

**Conditions**  
The system must be introduced without disrupting ongoing teaching activities, must be usable across multiple departments with potentially different administrative practices, and must integrate with the university's separate, centralised room-booking system rather than duplicating its functionality.

**Technology**  
The system is expected to be a web-based application accessible to secretaries, lecturers, and students, and must be able to exchange data (e.g. schedules) with the external, centralised room-booking system.

**Objects**  
Student, lecturer, course, course offering, enrollment, exam, grade, and study programme.

**Responsibility**  
The system is responsible for maintaining consistent and up-to-date records of courses, enrollments, and grades, and for supporting correct and coordinated planning of teaching activities across departments - but is _not_ responsible for room allocation or booking.


# Exercise 1.3: SD - Semester Project
