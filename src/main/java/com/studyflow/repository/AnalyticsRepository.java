package com.studyflow.repository;

import java.time.LocalDate;

public interface AnalyticsRepository {
    int countTotalTasks();

    int countCompletedTasks();

    int countPendingTasks();

    int countOverdueTasks(LocalDate today);

    int getTotalStudyMinutes();

    int countStudySessions();
}

