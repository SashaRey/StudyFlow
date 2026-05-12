package com.studyflow.repository;

import com.studyflow.model.Subject;
import java.util.Optional;

public interface SubjectRepository extends BaseRepository<Subject> {
    Optional<Subject> findByName(String name);
}
