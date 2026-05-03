# StudyFlow Copilot Instructions



## Project Overview

StudyFlow is a desktop student productivity application built with:



\- Java 17

\- JavaFX

\- SQLite

\- Maven



Main modules:

\- Tasks

\- Schedule

\- Exams

\- Analytics

\- Settings



## Architecture Rules



Use layered architecture:



- ui/           -> JavaFX views and reusable UI components

- controller/   -> handles user actions and UI coordination

- service/      -> business logic

- repository/   -> data access

- database/     -> SQLite connection and schema init

- model/        -> domain entities

- util/         -> small reusable helpers only



## Important Constraints



- Never place SQL queries in controllers.

- Never place JavaFX UI code in repositories.

- Keep services independent from JavaFX classes.

- Controllers should remain thin.

- Prefer constructor injection.

- Use clear class names.

- Avoid unnecessary abstractions.

- Do not generate god classes.



## Coding Style



- Use readable Java code.

- Prefer composition over inheritance.

- Keep methods short.

- Add JavaDoc for public classes when useful.

- Use Optional instead of null where appropriate.



## JavaFX Rules



- Prefer FXML + controller separation.

- Keep styling in CSS files.

- Reuse components.



## Database Rules



- Use repository classes for CRUD.

- Keep SQL simple and readable.

- Use prepared statements.



## When generating new features



Always propose:

1\. affected packages

2\. classes to create/update

3\. database changes

4\. UI changes

5\. tests if needed



## Priority



Maintainability > cleverness  

Readability > overengineering  

Scalability > shortcuts

