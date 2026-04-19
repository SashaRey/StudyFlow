# StudyFlow
A desktop productivity app for students: tasks, schedule, exams and analytics. Built with Java, JavaFX, SQLite and Maven.

## Project Structure

```text
StudyFlow/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/studyflow/
│   │   │   ├── app/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── database/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   │   └── impl/
│   │   │   ├── service/
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   └── screens/
│   │   │   └── util/
│   │   └── resources/
│   │       ├── db/
│   │       ├── fxml/
│   │       ├── icons/
│   │       └── styles/
│   └── test/java/com/studyflow/service/
└── README.md
```

## Run

```bash
mvn javafx:run
```
