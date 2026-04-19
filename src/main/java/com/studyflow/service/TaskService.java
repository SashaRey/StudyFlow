package com.studyflow.service;

import com.studyflow.model.Task;
import com.studyflow.repository.TaskRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task saveTask(Task task) throws SQLException {
        // TODO Add task validation and reminder scheduling rules.
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) throws SQLException {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() throws SQLException {
        return taskRepository.findAll();
    }

    public void deleteTask(Long id) throws SQLException {
        taskRepository.deleteById(id);
    }
}
