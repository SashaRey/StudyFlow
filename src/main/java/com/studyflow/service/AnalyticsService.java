package com.studyflow.service;

import com.studyflow.model.StudySession;
import com.studyflow.repository.StudySessionRepository;

import java.sql.SQLException;
import java.util.List;

public class AnalyticsService {

    private final StudySessionRepository studySessionRepository;

    public AnalyticsService(StudySessionRepository studySessionRepository) {
        this.studySessionRepository = studySessionRepository;
    }

    public List<StudySession> getStudySessions() throws SQLException {
        // TODO Calculate productivity metrics and summary charts.
        return studySessionRepository.findAll();
    }
}
