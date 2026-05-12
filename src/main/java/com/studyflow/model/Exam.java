package com.studyflow.model;

import java.time.LocalDate;

public class Exam {
    private Long id;
    private Long subjectId;
    private String title;
    private LocalDate examDate;
    private String location;
    private ExamStatus status;

    public Exam() {
    }

    public Exam(Long id, Long subjectId, String title, LocalDate examDate, String location, ExamStatus status) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.examDate = examDate;
        this.location = location;
        this.status = status;
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

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ExamStatus getStatus() {
        return status;
    }

    public void setStatus(ExamStatus status) {
        this.status = status;
    }
}
