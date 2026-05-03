package com.studyflow.repository.impl;

import com.studyflow.database.DatabaseManager;
import com.studyflow.exception.DatabaseException;
import com.studyflow.model.Task;
import com.studyflow.model.TaskPriority;
import com.studyflow.model.TaskStatus;
import com.studyflow.repository.TaskRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SqliteTaskRepository is an implementation of the TaskRepository interface
 * that uses an SQLite database to persist tasks.
 */
public class SqliteTaskRepository implements TaskRepository {

    private static final String INSERT_SQL = """
            INSERT INTO tasks (title, description, due_date, status, priority)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE tasks
            SET title = ?, description = ?, due_date = ?, status = ?, priority = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = "DELETE FROM tasks WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM tasks WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM tasks";

    private final DatabaseManager databaseManager;

    public SqliteTaskRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public SqliteTaskRepository() {
        this(DatabaseManager.getInstance());
    }

    @Override
    public Task save(Task task) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindTaskFields(statement, task, false);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getLong(1));
                }
            }

            return task;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save task", exception);
        }
    }

    public Task update(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task id is required for update");
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            bindTaskFields(statement, task, true);
            statement.executeUpdate();
            return task;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to update task", exception);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete task", exception);
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapTask(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find task by id", exception);
        }
    }

    @Override
    public List<Task> findAll() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<Task> tasks = new ArrayList<>();
            while (resultSet.next()) {
                tasks.add(mapTask(resultSet));
            }
            return tasks;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find all tasks", exception);
        }
    }

    private void bindTaskFields(PreparedStatement statement, Task task, boolean includeId) throws SQLException {
        statement.setString(1, task.getTitle());
        statement.setString(2, task.getDescription());

        LocalDate dueDate = task.getDueDate();
        if (dueDate == null) {
            statement.setNull(3, java.sql.Types.VARCHAR);
        } else {
            statement.setString(3, dueDate.toString());
        }

        TaskStatus status = task.getStatus() == null ? TaskStatus.PENDING : task.getStatus();
        statement.setString(4, status.name());

        TaskPriority priority = task.getPriority() == null ? TaskPriority.MEDIUM : task.getPriority();
        statement.setString(5, priority.name());

        if (includeId) {
            statement.setLong(6, task.getId());
        }
    }

    private Task mapTask(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getLong("id"));
        task.setTitle(resultSet.getString("title"));
        task.setDescription(resultSet.getString("description"));

        String dueDate = resultSet.getString("due_date");
        task.setDueDate(dueDate == null ? null : LocalDate.parse(dueDate));

        String status = resultSet.getString("status");
        task.setStatus(status == null ? TaskStatus.PENDING : TaskStatus.valueOf(status));

        String priority = resultSet.getString("priority");
        task.setPriority(priority == null ? TaskPriority.MEDIUM : TaskPriority.valueOf(priority));

        return task;
    }
}

