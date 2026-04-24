package com.studyflow.model;

import java.time.LocalDateTime;

public class Exam {
    private Long id;
    private Long subjectId;
    private String title;
    private LocalDateTime examDate;
    private String location;
    private ExamType type;

    public Exam() {
    }

    public Exam(Long id, Long subjectId, String title, LocalDateTime examDate, String location, ExamType type) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.examDate = examDate;
        this.location = location;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ExamType getType() {
        return type;
    }

    public void setType(ExamType type) {
        this.type = type;
    }
}
