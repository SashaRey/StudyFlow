package com.studyflow.repository.impl;

import com.studyflow.database.DatabaseManager;
import com.studyflow.exception.DatabaseException;
import com.studyflow.repository.AnalyticsRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * SqliteAnalyticsRepository provides read-only analytics queries backed by SQLite.
 */
public class SqliteAnalyticsRepository implements AnalyticsRepository {

    private static final String COUNT_TOTAL_TASKS_SQL = "SELECT COUNT(*) FROM tasks";
    private static final String COUNT_COMPLETED_TASKS_SQL = "SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'";
    private static final String COUNT_PENDING_TASKS_SQL = "SELECT COUNT(*) FROM tasks WHERE status = 'PENDING'";
    private static final String COUNT_OVERDUE_TASKS_SQL = """
            SELECT COUNT(*)
            FROM tasks
            WHERE due_date < ?
              AND status != 'COMPLETED'
            """;
    private static final String COUNT_STUDY_SESSIONS_SQL = "SELECT COUNT(*) FROM study_sessions";
    private static final String SUM_STUDY_MINUTES_SQL = "SELECT SUM(duration_minutes) FROM study_sessions";

    private final DatabaseManager databaseManager;

    public SqliteAnalyticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public SqliteAnalyticsRepository() {
        this(DatabaseManager.getInstance());
    }

    @Override
    public int countTotalTasks() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_TOTAL_TASKS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return readSingleInt(resultSet);
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to count total tasks", exception);
        }
    }

    @Override
    public int countCompletedTasks() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_COMPLETED_TASKS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return readSingleInt(resultSet);
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to count completed tasks", exception);
        }
    }

    @Override
    public int countPendingTasks() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_PENDING_TASKS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return readSingleInt(resultSet);
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to count pending tasks", exception);
        }
    }

    @Override
    public int countOverdueTasks(LocalDate today) {
        if (today == null) {
            throw new IllegalArgumentException("Today is required to count overdue tasks");
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_OVERDUE_TASKS_SQL)) {
            statement.setString(1, today.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return readSingleInt(resultSet);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to count overdue tasks", exception);
        }
    }

    @Override
    public int getTotalStudyMinutes() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SUM_STUDY_MINUTES_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int total = resultSet.getInt(1);
                return resultSet.wasNull() ? 0 : total;
            }
            return 0;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to sum study minutes", exception);
        }
    }

    @Override
    public int countStudySessions() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_STUDY_SESSIONS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return readSingleInt(resultSet);
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to count study sessions", exception);
        }
    }

    private int readSingleInt(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }
}

