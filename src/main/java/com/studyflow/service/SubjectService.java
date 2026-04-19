package com.studyflow.service;

import com.studyflow.model.Subject;
import com.studyflow.repository.SubjectRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject saveSubject(Subject subject) throws SQLException {
        // TODO Add duplicate-subject checks and academic term validation.
        return subjectRepository.save(subject);
    }

    public Optional<Subject> getSubjectById(Long id) throws SQLException {
        return subjectRepository.findById(id);
    }

    public List<Subject> getAllSubjects() throws SQLException {
        return subjectRepository.findAll();
    }
}
