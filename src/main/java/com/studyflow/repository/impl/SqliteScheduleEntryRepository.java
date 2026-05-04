package com.studyflow.repository.impl;

import com.studyflow.database.DatabaseManager;
import com.studyflow.exception.DatabaseException;
import com.studyflow.model.ScheduleEntry;
import com.studyflow.repository.ScheduleEntryRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SqliteScheduleEntryRepository is an implementation of the ScheduleEntryRepository interface
 * that uses an SQLite database to persist schedule entries.
 */
public class SqliteScheduleEntryRepository implements ScheduleEntryRepository {

    private static final String INSERT_SQL = """
            INSERT INTO schedule_entries (subject_id, start_time, end_time, location, notes)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE schedule_entries
            SET subject_id = ?, start_time = ?, end_time = ?, location = ?, notes = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = "DELETE FROM schedule_entries WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM schedule_entries WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM schedule_entries ORDER BY start_time ASC";

    private final DatabaseManager databaseManager;

    public SqliteScheduleEntryRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public SqliteScheduleEntryRepository() {
        this(DatabaseManager.getInstance());
    }

    @Override
    public ScheduleEntry save(ScheduleEntry entry) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindScheduleEntryFields(statement, entry, false);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    entry.setId(keys.getLong(1));
                }
            }

            return entry;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save schedule entry", exception);
        }
    }

    @Override
    public ScheduleEntry update(ScheduleEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Schedule entry is required for update");
        }
        if (entry.getId() == null) {
            throw new IllegalArgumentException("Schedule entry id is required for update");
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            bindScheduleEntryFields(statement, entry, true);
            statement.executeUpdate();
            return entry;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to update schedule entry", exception);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete schedule entry", exception);
        }
    }

    @Override
    public Optional<ScheduleEntry> findById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapScheduleEntry(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find schedule entry by id", exception);
        }
    }

    @Override
    public List<ScheduleEntry> findAll() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<ScheduleEntry> entries = new ArrayList<>();
            while (resultSet.next()) {
                entries.add(mapScheduleEntry(resultSet));
            }
            return entries;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to find all schedule entries", exception);
        }
    }

    private void bindScheduleEntryFields(PreparedStatement statement, ScheduleEntry entry, boolean includeId)
            throws SQLException {
        if (entry.getSubjectId() == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setLong(1, entry.getSubjectId());
        }

        LocalDateTime startTime = entry.getStartTime();
        if (startTime == null) {
            statement.setNull(2, Types.VARCHAR);
        } else {
            statement.setString(2, startTime.toString());
        }

        LocalDateTime endTime = entry.getEndTime();
        if (endTime == null) {
            statement.setNull(3, Types.VARCHAR);
        } else {
            statement.setString(3, endTime.toString());
        }

        if (entry.getLocation() == null) {
            statement.setNull(4, Types.VARCHAR);
        } else {
            statement.setString(4, entry.getLocation());
        }

        if (entry.getNotes() == null) {
            statement.setNull(5, Types.VARCHAR);
        } else {
            statement.setString(5, entry.getNotes());
        }

        if (includeId) {
            statement.setLong(6, entry.getId());
        }
    }

    private ScheduleEntry mapScheduleEntry(ResultSet resultSet) throws SQLException {
        ScheduleEntry entry = new ScheduleEntry();
        entry.setId(resultSet.getLong("id"));

        long subjectId = resultSet.getLong("subject_id");
        entry.setSubjectId(resultSet.wasNull() ? null : subjectId);

        String startTime = resultSet.getString("start_time");
        entry.setStartTime(startTime == null ? null : LocalDateTime.parse(startTime));

        String endTime = resultSet.getString("end_time");
        entry.setEndTime(endTime == null ? null : LocalDateTime.parse(endTime));

        entry.setLocation(resultSet.getString("location"));
        entry.setNotes(resultSet.getString("notes"));

        return entry;
    }
}
