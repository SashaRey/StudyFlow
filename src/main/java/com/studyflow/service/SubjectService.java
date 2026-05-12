package com.studyflow.service;

import com.studyflow.model.Subject;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.impl.SqliteSubjectRepository;

import java.util.List;
import java.util.Optional;

public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public SubjectService() {
        this(new SqliteSubjectRepository());
    }

    public Subject saveSubject(Subject subject) {
        // TODO Add duplicate-subject checks and academic term validation.
        return subjectRepository.save(subject);
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getOrCreateSubjectByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Subject name is required");
        }
        String trimmed = name.trim();
        return subjectRepository.findByName(trimmed)
                .orElseGet(() -> subjectRepository.save(new Subject(null, trimmed, null, null)));
    }
}
