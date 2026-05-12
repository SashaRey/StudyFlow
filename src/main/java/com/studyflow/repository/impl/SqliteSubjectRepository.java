package com.studyflow.repository.impl;

import com.studyflow.database.DatabaseManager;
import com.studyflow.exception.DatabaseException;
import com.studyflow.model.Subject;
import com.studyflow.repository.SubjectRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SqliteSubjectRepository is an implementation of the SubjectRepository interface
 * that uses an SQLite database to persist subjects.
 */
public class SqliteSubjectRepository implements SubjectRepository {

    private static final String INSERT_SQL = """
            INSERT INTO subjects (name, instructor, color_code)
            VALUES (?, ?, ?)
            """;
    private static final String DELETE_SQL = "DELETE FROM subjects WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM subjects WHERE id = ?";
    private static final String FIND_BY_NAME_SQL = "SELECT * FROM subjects WHERE name = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM subjects ORDER BY name ASC";

    private final DatabaseManager databaseManager;

    public SqliteSubjectRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public SqliteSubjectRepository() {
        this(DatabaseManager.getInstance());
    }

    @Override
    public Subject save(Subject subject) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindSubjectFields(statement, subject);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    subject.setId(keys.getLong(1));
                }
            }

            return subject;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save subject", exception);
        }
    }

    @Override
    public Optional<Subject> findById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapSubject(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find subject by id", exception);
        }
    }

    @Override
    public Optional<Subject> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            statement.setString(1, trimmed);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapSubject(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find subject by name", exception);
        }
    }

    @Override
    public List<Subject> findAll() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<Subject> subjects = new ArrayList<>();
            while (resultSet.next()) {
                subjects.add(mapSubject(resultSet));
            }
            return subjects;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find all subjects", exception);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete subject", exception);
        }
    }

    private void bindSubjectFields(PreparedStatement statement, Subject subject) throws SQLException {
        statement.setString(1, subject.getName());

        if (subject.getInstructor() == null) {
            statement.setNull(2, Types.VARCHAR);
        } else {
            statement.setString(2, subject.getInstructor());
        }

        if (subject.getColorCode() == null) {
            statement.setNull(3, Types.VARCHAR);
        } else {
            statement.setString(3, subject.getColorCode());
        }
    }

    private Subject mapSubject(ResultSet resultSet) throws SQLException {
        Subject subject = new Subject();
        subject.setId(resultSet.getLong("id"));
        subject.setName(resultSet.getString("name"));
        subject.setInstructor(resultSet.getString("instructor"));
        subject.setColorCode(resultSet.getString("color_code"));
        return subject;
    }
}
