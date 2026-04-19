
---

## 2. `docs/roadmap.md`

```md
# StudyFlow Roadmap

## Goal

Build a portfolio-ready desktop application for students in about 1.5 months.

## Stage 0 — Project Initialization
Status: completed

- create GitHub repository
- create Maven project
- configure JavaFX
- connect SQLite
- prepare base project structure
- verify first JavaFX launch

## Stage 1 — Architecture Stabilization
- review generated scaffold
- clean package structure
- verify responsibilities of each layer
- fix naming inconsistencies
- prepare project documentation
- add Copilot instructions and docs

## Stage 2 — Tasks Module
Goal: build the first full usable feature

Planned work:
- task entity review
- task repository CRUD
- task service validation rules
- task list UI
- add/edit/delete task dialogs
- task filtering by status/priority
- deadline display

Done when:
- user can create, edit, delete and view tasks
- data persists in SQLite
- UI is stable and readable

## Stage 3 — Schedule Module
Goal: support weekly university planning

Planned work:
- schedule entry CRUD
- weekly schedule screen
- subject linking
- study blocks
- overlap validation
- simple calendar-like UI

Done when:
- user can manage classes and study sessions
- schedule is visible by day/week

## Stage 4 — Exams Module
Goal: track upcoming exams and deadlines

Planned work:
- exams CRUD
- exam date and subject linking
- countdown to exam
- upcoming exams widget
- overdue/past exam state

Done when:
- user can view all exams and upcoming deadlines clearly

## Stage 5 — Analytics Module
Goal: show useful learning insights

Planned work:
- total study time
- sessions per week
- tasks completed
- tasks overdue
- charts by subject
- dashboard summary cards

Done when:
- dashboard gives meaningful visual feedback

## Stage 6 — UX Polish
Planned work:
- better navigation
- improved CSS
- dark theme
- cleaner forms
- empty states
- validation messages
- icons

## Stage 7 — Reliability and Packaging
Planned work:
- basic tests for services and repositories
- fix edge cases
- improve error handling
- package application
- final README
- screenshots / demo materials

## Nice to Have
- Pomodoro timer
- streak system
- export to CSV or PDF
- recurring tasks
- grade tracking
- Google Calendar sync
- cloud sync

## Priority Rule

Always prioritize:
1. stable architecture
2. one finished module
3. clean code
4. polish later