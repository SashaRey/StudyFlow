# StudyFlow Architecture

## 1. Project Overview

StudyFlow is a desktop productivity application for students.

The goal of the project is to combine in one application:
- task management
- university schedule planning
- exam tracking
- productivity analytics

Technology stack:
- Java 21
- JavaFX
- SQLite
- Maven

The project is designed as a portfolio-ready desktop application with a modular structure and clear separation of responsibilities.

---

## 2. Architectural Goal

The main architectural goal is to build a project that is:

- understandable for one developer
- easy to extend with new modules
- suitable for a university project
- strong enough for a junior Java portfolio

The architecture should avoid chaos, duplicated logic, and tight coupling between GUI, business logic, and database code.

---

## 3. Architectural Style

StudyFlow uses a layered architecture.

Main layers:
- UI layer
- Controller layer
- Service layer
- Repository layer
- Database layer
- Domain model layer

This approach makes the project easier to maintain, test, and scale.

---

## 4. Package Structure

```text
com.studyflow
├── app
├── config
├── controller
├── database
├── dto
├── exception
├── model
├── repository
├── repository.impl
├── service
├── ui
├── ui.components
├── ui.screens
└── util
```

# 5. Responsibility of Each Package

## app

Contains the application entry point and global application startup logic.

### Examples:
- MainApplication
- SceneManager

### Responsibilities:
- starting JavaFX application
- loading the main window
- global navigation coordination

---

## config

Contains application-level configuration and constants.

### Examples:
- AppConfig

### Responsibilities:
- app title
- default window sizes
- configuration constants
- resource paths if needed

---

## controller

Contains JavaFX controllers that handle interaction between UI and business logic.

### Examples:
- MainWindowController
- TaskController
- ScheduleController

### Responsibilities:
- handle user actions
- read values from UI controls
- call service methods
- update UI state

### Controllers must stay thin.

### Controllers must not:
- contain SQL queries
- contain complex business rules
- store unrelated application state

---

## database

Contains database initialization and connection logic.

### Examples:
- DatabaseManager

### Responsibilities:
- create SQLite connection
- initialize schema
- manage DB setup

This layer isolates low-level database details from the rest of the application.

---

## dto

Contains data transfer objects if needed.

### Examples:
- TaskSummaryDto
- AnalyticsSummaryDto

### Responsibilities:
- transfer aggregated or UI-specific data
- avoid overloading domain entities with presentation concerns

This package is optional in early stages and may grow later.

---

## exception

Contains project-specific exceptions.

### Examples:
- AppException
- ValidationException
- DatabaseException

### Responsibilities:
- represent application errors in a controlled way
- avoid exposing raw technical errors directly to UI when possible

---

## model

Contains the core domain entities of the application.

### Examples:
- Task
- Subject
- ScheduleEntry
- Exam
- StudySession

### Responsibilities:
- represent the core data of the system
- store business-related state
- remain independent from UI

Models should not contain JavaFX UI code.

---

## repository

Contains repository interfaces and data access contracts.

### Examples:
- TaskRepository
- SubjectRepository
- ExamRepository

### Responsibilities:
- declare CRUD operations
- define persistence contract
- isolate storage access behind interfaces

---

## repository.impl

Contains concrete SQLite implementations of repositories.

### Examples:
- TaskRepositoryImpl
- SubjectRepositoryImpl
- ExamRepositoryImpl

### Responsibilities:
- execute SQL queries
- map database rows to model objects
- save and load data from SQLite

### Rules:
- use prepared statements
- keep SQL readable
- do not place UI logic here

---

## service

Contains business logic of the application.

### Examples:
- TaskService
- ScheduleService
- ExamService
- AnalyticsService

### Responsibilities:
- validate user actions
- coordinate repositories
- enforce business rules
- prepare application-level operations

### Examples of business logic:
- checking task validity
- detecting overlapping schedule entries
- calculating upcoming exams
- computing study statistics

Services must not depend on JavaFX UI classes.

---

## ui

Contains reusable JavaFX UI elements and screen-level UI logic support.

### Subpackages:
- ui.components
- ui.screens

### Responsibilities:
- reusable panels and controls
- screen layouts
- visual structure

---

## util

Contains small helper utilities.

### Examples:
- date formatting
- mapping helpers
- input utility helpers

### Rules:
- no business logic in util
- no SQL in util
- avoid turning util into a dump package

---

# 6. Data Flow

Typical flow of user interaction:

```text
User -> Controller -> Service -> Repository -> Database
```

## Example

1. User creates a new task in the UI  
2. TaskController reads form values  
3. TaskService validates the task  
4. TaskRepositoryImpl saves it into SQLite  
5. UI refreshes task list  

This flow keeps the architecture predictable and maintainable.

---

# 7. Main Functional Modules

The project is divided into the following functional modules:

## 1. Dashboard

Shows summary information:

- upcoming tasks  
- upcoming exams  
- today schedule  
- productivity overview  

---

## 2. Tasks

Task management subsystem:

- create task  
- edit task  
- delete task  
- set priority  
- set deadline  
- filter by status  

---

## 3. Schedule

Schedule planning subsystem:

- class timetable  
- study sessions  
- weekly planning  
- overlap checking  

---

## 4. Exams

Exam management subsystem:

- exam dates  
- subject relation  
- countdown  
- upcoming exams list  

---

## 5. Analytics

Productivity subsystem:

- completed tasks count  
- overdue tasks  
- total study time  
- study sessions by period  
- charts and summaries  

---

## 6. Settings

Application configuration:

- theme  
- preferences  
- future export/import settings  

---

# 8. Database Design (High-Level)

SQLite is used as the local embedded database.

## Expected main tables:

- tasks  
- subjects  
- schedule_entries  
- exams  
- study_sessions  

## Possible relations:

- a task may belong to a subject  
- an exam belongs to a subject  
- a schedule entry may be linked to a subject  
- a study session may be linked to a subject  

The database is local because the first version of the project is a single-user desktop app.

---

# 9. Architectural Rules

The following rules must be preserved during development:

- No SQL inside controllers  
- No JavaFX code inside repositories  
- Services should contain business logic, not UI code  
- Models should stay independent from UI  
- Repositories should isolate persistence details  
- Avoid unnecessary abstractions  
- Prefer constructor injection  
- Keep class responsibilities small and explicit  
- Prefer readability over cleverness  
- Add new features through the proper layer, not shortcuts  

---

# 10. Why This Architecture Is Suitable

This architecture is suitable for StudyFlow because it:

- is realistic for one developer  
- fits a 1.5 month implementation plan  
- looks strong for a university project  
- demonstrates Java, JavaFX, SQL and clean code skills  
- allows future expansion without rewriting the whole project  

---

# 11. Future Extension Possibilities

The architecture allows adding new features later, for example:

- notifications and reminders  
- recurring tasks  
- pomodoro timer  
- grade tracking  
- export to CSV or PDF  
- Google Calendar synchronization  
- cloud sync  
- mobile or web companion app  

Because responsibilities are separated, these features can be added incrementally.

---

# 12. Summary

StudyFlow is built as a modular desktop application with layered architecture.

## The main design idea is simple:

- UI handles presentation  
- controllers handle interaction  
- services handle business logic  
- repositories handle persistence  
- models represent the domain  

This makes the project cleaner, easier to support, and more impressive as a student portfolio project.