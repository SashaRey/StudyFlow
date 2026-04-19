package com.studyflow.model;

public class Subject {
    private Long id;
    private String name;
    private String instructor;
    private String colorCode;

    public Subject() {
    }

    public Subject(Long id, String name, String instructor, String colorCode) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
        this.colorCode = colorCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
