package com.studyflow.service;

import com.studyflow.model.Exam;
import com.studyflow.repository.ExamRepository;
import com.studyflow.repository.impl.SqliteExamRepository;

import java.util.List;
import java.util.Optional;

public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public ExamService() {
        this(new SqliteExamRepository());
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public List<Exam> getUpcomingExams() {
        return examRepository.findUpcoming();
    }

    public Optional<Exam> getExamById(int id) {
        validateId(id);
        return examRepository.findById((long) id);
    }

    public Exam createExam(Exam exam) {
        validateExam(exam);
        return examRepository.save(exam);
    }

    public void updateExam(Exam exam) {
        if (exam == null || exam.getId() == null) {
            throw new IllegalArgumentException("Exam id is required for update");
        }
        validateExam(exam);
        examRepository.update(exam);
    }

    public void deleteExam(int id) {
        validateId(id);
        examRepository.deleteById((long) id);
    }

    public List<Exam> getExamsBySubjectId(int subjectId) {
        validateSubjectId(subjectId);
        return examRepository.findBySubjectId((long) subjectId);
    }

    private void validateExam(Exam exam) {
        if (exam == null) {
            throw new IllegalArgumentException("Exam is required");
        }
        if (exam.getTitle() == null || exam.getTitle().isBlank()) {
            throw new IllegalArgumentException("Exam title must not be blank");
        }
        if (exam.getExamDate() == null) {
            throw new IllegalArgumentException("Exam date must not be null");
        }
        if (exam.getStatus() == null) {
            throw new IllegalArgumentException("Exam status must not be null");
        }
        if (exam.getSubjectId() != null && exam.getSubjectId() <= 0) {
            throw new IllegalArgumentException("Exam subject id must be positive when provided");
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Exam id must be positive");
        }
    }

    private void validateSubjectId(int subjectId) {
        if (subjectId <= 0) {
            throw new IllegalArgumentException("Subject id must be positive");
        }
    }
}
