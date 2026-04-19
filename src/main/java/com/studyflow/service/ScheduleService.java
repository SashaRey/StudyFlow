package com.studyflow.service;

import com.studyflow.model.ScheduleEntry;
import com.studyflow.repository.ScheduleEntryRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ScheduleService {

    private final ScheduleEntryRepository scheduleEntryRepository;

    public ScheduleService(ScheduleEntryRepository scheduleEntryRepository) {
        this.scheduleEntryRepository = scheduleEntryRepository;
    }

    public ScheduleEntry saveEntry(ScheduleEntry entry) throws SQLException {
        // TODO Add overlap detection for class and study blocks.
        return scheduleEntryRepository.save(entry);
    }

    public Optional<ScheduleEntry> getEntryById(Long id) throws SQLException {
        return scheduleEntryRepository.findById(id);
    }

    public List<ScheduleEntry> getAllEntries() throws SQLException {
        return scheduleEntryRepository.findAll();
    }
}
