package com.example.cli.taskmanager.cli;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.cli.taskmanager.domain.Status;
import com.example.cli.taskmanager.domain.Task;
import com.example.cli.taskmanager.storage.TaskRepository;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

enum SortType {
    DUE,
    PRIORITY,
}

@Command(name = "list", description = "List tasks")
class ListCommand implements Runnable {

    private final TaskRepository repository;

    @Option(names = "--status")
    private Status status;

    @Option(names = "--sort")
    private SortType sort; // "due" or "priority"

    ListCommand(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        List<Task> listOfTasks = repository.findAll();

        if (status != null) {
            listOfTasks = listOfTasks.stream()
                    .filter(task -> status.equals(task.status()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case DUE:
                    listOfTasks.sort(Comparator.comparing(
                            task -> task.dueDate().orElse(LocalDate.MAX)));
                    break;
                case PRIORITY:
                    Collections.sort(listOfTasks, (p1, p2) -> p1.priority().compareTo(p2.priority()));
                    break;
            }
        }

        for (Task task : listOfTasks) {
            System.out.printf("[%s] %s (%s) due: %s [%s] %n",
                    task.id(), task.title(), task.priority(),
                    task.dueDate().map(LocalDate::toString).orElse("none"), task.status());
        }
    }
}