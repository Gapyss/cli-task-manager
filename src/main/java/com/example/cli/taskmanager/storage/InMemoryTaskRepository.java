package com.example.cli.taskmanager.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.cli.taskmanager.domain.Task;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Task> store = new HashMap<>();

    @Override
    public Task save(Task task) {
        store.put(task.id(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<Task>(store.values());
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }
}