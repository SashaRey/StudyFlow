package com.studyflow.service;

import com.studyflow.exception.ValidationException;
import com.studyflow.model.Task;
import com.studyflow.model.TaskPriority;
import com.studyflow.model.TaskStatus;
import com.studyflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {

    @Test
    void createTaskShouldDelegateToRepository() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(1L, "Finish assignment", "Math chapter 3", LocalDate.now(), TaskStatus.PENDING, TaskPriority.MEDIUM, "Homework");

        Task saved = service.createTask(task);

        assertEquals(task, saved);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void updateTaskShouldDelegateToRepository() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(5L, "Revise notes", "Biology unit 2", LocalDate.now(), TaskStatus.PENDING, TaskPriority.MEDIUM, "Study");
        repository.save(task);

        Task updated = new Task(5L, "Revise notes", "Updated", LocalDate.now(), TaskStatus.PENDING, TaskPriority.HIGH, "Study");

        Task saved = service.updateTask(updated);

        assertEquals(updated, saved);
        assertEquals(1, repository.findAll().size());
        assertEquals(TaskPriority.HIGH, repository.findById(5L).get().getPriority());
    }

    @Test
    void markTaskAsCompletedShouldUpdateStatus() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(10L, "Read chapter", "History", LocalDate.now(), TaskStatus.PENDING, TaskPriority.LOW, "Reading");
        repository.save(task);

        Task updated = service.markTaskAsCompleted(10L);

        assertEquals(TaskStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void createTaskShouldRejectBlankTitle() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(1L, " ", "Math", LocalDate.now(), TaskStatus.PENDING, TaskPriority.MEDIUM, "Homework");

        assertThrows(ValidationException.class, () -> service.createTask(task));
    }

    @Test
    void getTaskByIdShouldReturnOptionalValue() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = new Task(7L, "Revise notes", "Biology unit 2", LocalDate.now(), TaskStatus.PENDING, TaskPriority.MEDIUM, "Study");
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
        public Task update(Task entity) {
            return save(entity);
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
