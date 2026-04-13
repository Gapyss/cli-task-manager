package com.example.cli.taskmanager.cli;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.example.cli.taskmanager.domain.Priority;
import com.example.cli.taskmanager.domain.Status;
import com.example.cli.taskmanager.domain.Task;
import com.example.cli.taskmanager.storage.TaskRepository;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "add", description = "Add a new task")
class AddCommand implements Runnable {
    private final TaskRepository repository;

    AddCommand(TaskRepository repository) {
        this.repository = repository;
    }

    @Parameters(index = "0", description = "Task")
    private String title;

    @Option(names = "--dueDate")
    private LocalDate dueDate;
    @Option(names = "--priority")
    private Priority priority;

    @Override
    public void run() {
        Task task = new Task(UUID.randomUUID().toString(), title, Optional.ofNullable(dueDate),
                priority == null ? Priority.MEDIUM : priority, Status.OPEN);
        repository.save(task);
        System.out.println("Task added: [" + task.id() + "] " + task.title());
    }

}
