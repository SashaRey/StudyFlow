package com.studyflow.model;

import java.time.LocalDateTime;

public class ScheduleEntry {
    private Long id;
    private Long subjectId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String notes;

    public ScheduleEntry() {
    }

    public ScheduleEntry(Long id, Long subjectId, LocalDateTime startTime, LocalDateTime endTime, String location, String notes) {
        this.id = id;
        this.subjectId = subjectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.notes = notes;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
