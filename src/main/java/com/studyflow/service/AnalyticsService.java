package com.studyflow.service;

import com.studyflow.dto.AnalyticsSummary;
import com.studyflow.repository.AnalyticsRepository;
import com.studyflow.repository.impl.SqliteAnalyticsRepository;

import java.time.LocalDate;

public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        if (analyticsRepository == null) {
            throw new IllegalArgumentException("AnalyticsRepository is required");
        }
        this.analyticsRepository = analyticsRepository;
    }

    public AnalyticsService() {
        this(new SqliteAnalyticsRepository());
    }

    public AnalyticsSummary getSummary() {
        int totalTasks = analyticsRepository.countTotalTasks();
        int completedTasks = analyticsRepository.countCompletedTasks();
        int pendingTasks = analyticsRepository.countPendingTasks();
        int overdueTasks = analyticsRepository.countOverdueTasks(LocalDate.now());
        int totalStudyMinutes = analyticsRepository.getTotalStudyMinutes();
        int studySessionsCount = analyticsRepository.countStudySessions();
        return new AnalyticsSummary(totalTasks, completedTasks, pendingTasks, overdueTasks, totalStudyMinutes, studySessionsCount);
    }

    public String formatStudyTime(int totalMinutes) {
        if (totalMinutes <= 0) {
            return "0 min";
        }
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        }
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (minutes == 0) {
            return hours + " h";
        }
        return hours + " h " + minutes + " min";
    }

    public double calculateCompletionRate(AnalyticsSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException("AnalyticsSummary is required");
        }
        int totalTasks = summary.getTotalTasks();
        if (totalTasks == 0) {
            return 0.0;
        }
        return summary.getCompletedTasks() * 100.0 / totalTasks;
    }

    public double calculateCompletionProgress(AnalyticsSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException("AnalyticsSummary is required");
        }
        int totalTasks = summary.getTotalTasks();
        if (totalTasks == 0) {
            return 0.0;
        }
        return summary.getCompletedTasks() * 1.0 / totalTasks;
    }
}
