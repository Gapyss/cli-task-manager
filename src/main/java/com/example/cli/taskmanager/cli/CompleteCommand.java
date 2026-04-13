package com.example.cli.taskmanager.cli;

import java.util.Optional;

import com.example.cli.taskmanager.domain.Status;
import com.example.cli.taskmanager.domain.Task;
import com.example.cli.taskmanager.storage.TaskRepository;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "complete")
class CompleteCommand implements Runnable {
    private final TaskRepository repository;

    CompleteCommand(TaskRepository repository) {
        this.repository = repository;
    }

    @Parameters(index = "0", description = "id")
    private String id;

    @Override
    public void run() {

        Optional<Task> task = repository.findById(id);

        if (task.isEmpty()) {
            System.out.print("Sorry cannot find task id: " + id);
        } else {
            // correct — unwrap it first
            Task found = task.get();
            Task completed = new Task(
                    found.id(),
                    found.title(),
                    found.dueDate(),
                    found.priority(),
                    Status.DONE);

            repository.save(completed);
            System.out.println("Task completed: " + completed.title());

        }
    }

}
