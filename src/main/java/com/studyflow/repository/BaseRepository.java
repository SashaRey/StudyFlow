package com.studyflow.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    T save(T entity) throws SQLException;

    Optional<T> findById(Long id) throws SQLException;

    List<T> findAll() throws SQLException;

    void deleteById(Long id) throws SQLException;
}
