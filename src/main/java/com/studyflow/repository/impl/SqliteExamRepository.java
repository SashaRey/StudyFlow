package com.studyflow.repository.impl;

import com.studyflow.database.DatabaseManager;
import com.studyflow.exception.DatabaseException;
import com.studyflow.model.Exam;
import com.studyflow.model.ExamStatus;
import com.studyflow.repository.ExamRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SqliteExamRepository is an implementation of the ExamRepository interface
 * that uses an SQLite database to persist exams.
 */
public class SqliteExamRepository implements ExamRepository {

    private static final String INSERT_SQL = """
            INSERT INTO exams (subject_id, title, exam_date, location, status)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE exams
            SET subject_id = ?, title = ?, exam_date = ?, location = ?, status = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = "DELETE FROM exams WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM exams WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM exams ORDER BY exam_date ASC";
    private static final String FIND_UPCOMING_SQL = """
            SELECT * FROM exams
            WHERE exam_date >= ? AND status NOT IN (?, ?)
            ORDER BY exam_date ASC
            """;
    private static final String FIND_BY_SUBJECT_SQL = """
            SELECT * FROM exams
            WHERE subject_id = ?
            ORDER BY exam_date ASC
            """;

    private final DatabaseManager databaseManager;

    public SqliteExamRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public SqliteExamRepository() {
        this(DatabaseManager.getInstance());
    }

    @Override
    public Exam save(Exam exam) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindExamFields(statement, exam, false);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    exam.setId(keys.getLong(1));
                }
            }

            return exam;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save exam", exception);
        }
    }

    @Override
    public Exam update(Exam exam) {
        if (exam == null) {
            throw new IllegalArgumentException("Exam is required for update");
        }
        if (exam.getId() == null) {
            throw new IllegalArgumentException("Exam id is required for update");
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            bindExamFields(statement, exam, true);
            statement.executeUpdate();
            return exam;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to update exam", exception);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete exam", exception);
        }
    }

    @Override
    public Optional<Exam> findById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapExam(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find exam by id", exception);
        }
    }

    @Override
    public List<Exam> findAll() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<Exam> exams = new ArrayList<>();
            while (resultSet.next()) {
                exams.add(mapExam(resultSet));
            }
            return exams;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find all exams", exception);
        }
    }

    @Override
    public List<Exam> findUpcoming() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_UPCOMING_SQL)) {
            statement.setString(1, LocalDate.now().toString());
            statement.setString(2, ExamStatus.COMPLETED.name());
            statement.setString(3, ExamStatus.CANCELLED.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Exam> exams = new ArrayList<>();
                while (resultSet.next()) {
                    exams.add(mapExam(resultSet));
                }
                return exams;
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find upcoming exams", exception);
        }
    }

    @Override
    public List<Exam> findBySubjectId(Long subjectId) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_SUBJECT_SQL)) {
            if (subjectId == null) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setLong(1, subjectId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Exam> exams = new ArrayList<>();
                while (resultSet.next()) {
                    exams.add(mapExam(resultSet));
                }
                return exams;
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find exams by subject", exception);
        }
    }

    private void bindExamFields(PreparedStatement statement, Exam exam, boolean includeId) throws SQLException {
        if (exam.getSubjectId() == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setLong(1, exam.getSubjectId());
        }

        statement.setString(2, exam.getTitle());

        LocalDate examDate = exam.getExamDate();
        if (examDate == null) {
            statement.setNull(3, Types.VARCHAR);
        } else {
            statement.setString(3, examDate.toString());
        }

        statement.setString(4, exam.getLocation());

        ExamStatus status = exam.getStatus() == null ? ExamStatus.UPCOMING : exam.getStatus();
        statement.setString(5, status.name());

        if (includeId) {
            statement.setLong(6, exam.getId());
        }
    }

    private Exam mapExam(ResultSet resultSet) throws SQLException {
        Exam exam = new Exam();
        exam.setId(resultSet.getLong("id"));

        long subjectId = resultSet.getLong("subject_id");
        exam.setSubjectId(resultSet.wasNull() ? null : subjectId);

        exam.setTitle(resultSet.getString("title"));

        String examDate = resultSet.getString("exam_date");
        exam.setExamDate(examDate == null ? null : LocalDate.parse(examDate));

        exam.setLocation(resultSet.getString("location"));

        String status = resultSet.getString("status");
        exam.setStatus(status == null ? ExamStatus.UPCOMING : ExamStatus.valueOf(status));

        return exam;
    }
}

