# StudyFlow Coding Style

## General Principles

- Write simple and readable Java code
- Prefer maintainability over clever solutions
- Avoid overengineering
- Keep classes focused on one responsibility
- Use clear names

## Naming

### Classes
Use descriptive nouns:
- `TaskService`
- `DatabaseManager`
- `MainWindowController`

### Methods
Use clear action names:
- `saveTask`
- `getAllTasks`
- `deleteExam`

### Variables
Use meaningful names:
- `taskRepository`
- `selectedSubject`
- `studySessions`

Avoid:
- `data`
- `obj`
- `temp`
- `manager1`

## Layer Rules

### Controllers
- handle user actions
- prepare data for UI
- call services
- do not write SQL
- do not store heavy business logic

### Services
- contain business rules
- validate input
- coordinate repositories
- should not depend on JavaFX UI classes

### Repositories
- work with database only
- use prepared statements
- keep SQL readable
- do not contain UI logic

## Methods

- prefer short methods
- extract repeated logic
- avoid methods that do too many things
- return `Optional` where appropriate instead of `null`

## Exceptions

- handle exceptions clearly
- avoid swallowing exceptions silently
- prefer application-specific exceptions in higher layers

## JavaFX

- keep UI layout separate from logic where possible
- prefer FXML for screens
- keep styling in CSS
- avoid putting too much logic into FXML controllers

## Database

- use SQLite through repository layer
- avoid duplicated SQL
- keep schema changes controlled
- use explicit column names

## Comments

- do not comment obvious code
- use comments for intent, not for trivial operations
- add JavaDoc for public classes and important methods when useful

## Formatting

- keep consistent indentation
- avoid very long methods
- avoid very large classes
- keep imports clean

## Project Rule

When adding a new feature, think in this order:

1. model
2. repository
3. service
4. controller
5. UI
6. tests