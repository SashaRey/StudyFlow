package com.studyflow.repository;

import com.studyflow.model.Exam;

import java.util.List;

public interface ExamRepository extends BaseRepository<Exam> {
    Exam update(Exam exam);

    List<Exam> findUpcoming();

    List<Exam> findBySubjectId(Long subjectId);
}
