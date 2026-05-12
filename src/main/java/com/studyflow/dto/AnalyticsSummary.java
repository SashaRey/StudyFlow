package com.studyflow.dto;

/**
 * Immutable summary data for the analytics dashboard.
 */
public class AnalyticsSummary {
    private final int totalTasks;
    private final int completedTasks;
    private final int pendingTasks;
    private final int overdueTasks;
    private final int totalStudyMinutes;
    private final int studySessionsCount;

    public AnalyticsSummary(int totalTasks,
                            int completedTasks,
                            int pendingTasks,
                            int overdueTasks,
                            int totalStudyMinutes,
                            int studySessionsCount) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.overdueTasks = overdueTasks;
        this.totalStudyMinutes = totalStudyMinutes;
        this.studySessionsCount = studySessionsCount;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public int getOverdueTasks() {
        return overdueTasks;
    }

    public int getTotalStudyMinutes() {
        return totalStudyMinutes;
    }

    public int getStudySessionsCount() {
        return studySessionsCount;
    }
}

