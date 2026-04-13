package com.example.cli.taskmanager.storage;

import java.util.List;
import java.util.Optional;

import com.example.cli.taskmanager.domain.Task;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(String id);
    List<Task> findAll();
    void delete(String id);
}
