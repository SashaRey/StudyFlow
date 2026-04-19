package com.studyflow.service;

import com.studyflow.model.Exam;
import com.studyflow.repository.ExamRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public Exam saveExam(Exam exam) throws SQLException {
        // TODO Add exam reminder and countdown business rules.
        return examRepository.save(exam);
    }

    public Optional<Exam> getExamById(Long id) throws SQLException {
        return examRepository.findById(id);
    }

    public List<Exam> getAllExams() throws SQLException {
        return examRepository.findAll();
    }
}
