package com.studyflow.service;

import com.studyflow.model.Task;
import com.studyflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {

    @Test
    void saveTaskShouldDelegateToRepository() throws SQLException {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(1L, "Finish assignment", "Math chapter 3", LocalDate.now(), "PENDING");

        Task saved = service.saveTask(task);

        assertEquals(task, saved);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void getTaskByIdShouldReturnOptionalValue() throws SQLException {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(7L, "Revise notes", "Biology unit 2", LocalDate.now(), "PENDING");
        repository.save(task);

        Optional<Task> found = service.getTaskById(7L);

        assertTrue(found.isPresent());
        assertEquals("Revise notes", found.get().getTitle());
    }

    private static class InMemoryTaskRepository implements TaskRepository {
        private final List<Task> tasks = new ArrayList<>();

        @Override
        public Task save(Task entity) {
            tasks.removeIf(task -> task.getId().equals(entity.getId()));
            tasks.add(entity);
            return entity;
        }

        @Override
        public Optional<Task> findById(Long id) {
            return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
        }

        @Override
        public List<Task> findAll() {
            return new ArrayList<>(tasks);
        }

        @Override
        public void deleteById(Long id) {
            tasks.removeIf(task -> task.getId().equals(id));
        }
    }
}
