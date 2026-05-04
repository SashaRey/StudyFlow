package com.studyflow.service;

import com.studyflow.exception.ValidationException;
import com.studyflow.model.ScheduleEntry;
import com.studyflow.repository.ScheduleEntryRepository;
import com.studyflow.repository.impl.SqliteScheduleEntryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ScheduleService {

    private final ScheduleEntryRepository scheduleEntryRepository;

    public ScheduleService(ScheduleEntryRepository scheduleEntryRepository) {
        this.scheduleEntryRepository = scheduleEntryRepository;
    }

    public ScheduleService() {
        this(new SqliteScheduleEntryRepository());
    }

    public ScheduleEntry createEntry(ScheduleEntry entry) {
        validateEntry(entry);
        ensureNoOverlap(entry);
        return scheduleEntryRepository.save(entry);
    }

    public ScheduleEntry updateEntry(ScheduleEntry entry) {
        if (entry == null || entry.getId() == null) {
            throw new ValidationException("Schedule entry id is required for update");
        }
        validateEntry(entry);
        ensureNoOverlap(entry);
        return scheduleEntryRepository.update(entry);
    }

    public ScheduleEntry saveEntry(ScheduleEntry entry) {
        if (entry != null && entry.getId() != null) {
            return updateEntry(entry);
        }
        return createEntry(entry);
    }

    public void deleteEntry(Long id) {
        validateId(id);
        scheduleEntryRepository.deleteById(id);
    }

    public Optional<ScheduleEntry> getEntryById(Long id) {
        validateId(id);
        return scheduleEntryRepository.findById(id);
    }

    public List<ScheduleEntry> getAllEntries() {
        return scheduleEntryRepository.findAll();
    }

    private void validateEntry(ScheduleEntry entry) {
        if (entry == null) {
            throw new ValidationException("Schedule entry is required");
        }
        if (entry.getSubjectId() == null) {
            throw new ValidationException("Schedule entry subject id must not be null");
        }
        LocalDateTime startTime = entry.getStartTime();
        LocalDateTime endTime = entry.getEndTime();
        if (startTime == null || endTime == null) {
            throw new ValidationException("Schedule entry start and end time are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new ValidationException("Schedule entry start time must be before end time");
        }
    }

    private void ensureNoOverlap(ScheduleEntry entry) {
        List<ScheduleEntry> existingEntries = scheduleEntryRepository.findAll();
        for (ScheduleEntry existing : existingEntries) {
            if (existing == null) {
                continue;
            }
            if (entry.getId() != null && entry.getId().equals(existing.getId())) {
                continue;
            }
            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd = existing.getEndTime();
            if (existingStart == null || existingEnd == null) {
                continue;
            }
            boolean overlaps = entry.getStartTime().isBefore(existingEnd)
                    && entry.getEndTime().isAfter(existingStart);
            if (overlaps) {
                throw new ValidationException("Schedule entry overlaps with another entry");
            }
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("Schedule entry id is required");
        }
    }
}
