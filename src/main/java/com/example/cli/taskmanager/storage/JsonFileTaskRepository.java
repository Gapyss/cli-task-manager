package com.example.cli.taskmanager.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.cli.taskmanager.domain.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonFileTaskRepository implements TaskRepository {

    private static final Logger log = LoggerFactory.getLogger(JsonFileTaskRepository.class);
    private static final Path FILE = Path.of(System.getProperty("user.home"), ".tasks.json");

    private final ObjectMapper mapper;
    private final Map<String, Task> store;
    private final Path filePath;

    public JsonFileTaskRepository() {
        this(FILE);
    }

    public JsonFileTaskRepository(Path filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()).registerModule(new Jdk8Module());
        this.store = loadFromDisk();
    }

    private Map<String, Task> loadFromDisk() {
        if (!filePath.toFile().exists()) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(filePath.toFile(), new TypeReference<Map<String, Task>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to read tasks file, starting fresh", e);
            return new HashMap<>();
        }
    }

    private void saveToDisk() {
        try {
            mapper.writeValue(filePath.toFile(), store);

        } catch (IOException e) {
            log.error("Error " +e);
            throw new RuntimeException();
        }
    }

    @Override
    public Task save(Task task) {
        store.put(task.id(), task);
        saveToDisk();
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findAll() {

        Map<String,Task> valueFromDisk = loadFromDisk();

        return new ArrayList<Task>(valueFromDisk.values());
    }

    @Override
    public void delete(String id) {
        store.remove(id);
        saveToDisk();
    }
}