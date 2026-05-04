package com.studyflow.service;

import com.studyflow.exception.ValidationException;
import com.studyflow.model.Task;
import com.studyflow.model.TaskStatus;
import com.studyflow.repository.TaskRepository;
import com.studyflow.repository.impl.SqliteTaskRepository;

import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskService() {
        this(new SqliteTaskRepository());
    }

    public Task createTask(Task task) {
        validateTask(task);
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        if (task == null || task.getId() == null) {
            throw new ValidationException("Task id is required for update");
        }
        validateTask(task);
        return taskRepository.update(task);
    }

    public Task markTaskAsCompleted(Long id) {
        validateId(id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Task not found: " + id));
        task.setStatus(TaskStatus.COMPLETED);
        validateTask(task);
        return taskRepository.update(task);
    }

    public Task saveTask(Task task) {
        if (task != null && task.getId() != null) {
            return updateTask(task);
        }
        return createTask(task);
    }

    public Optional<Task> getTaskById(Long id) {
        validateId(id);
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void deleteTask(Long id) {
        validateId(id);
        taskRepository.deleteById(id);
    }

    private void validateTask(Task task) {
        if (task == null) {
            throw new ValidationException("Task is required");
        }
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new ValidationException("Task title must not be blank");
        }
        if (task.getStatus() == null) {
            throw new ValidationException("Task status must not be null");
        }
        if (task.getPriority() == null) {
            throw new ValidationException("Task priority must not be null");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("Task id is required");
        }
    }
}
