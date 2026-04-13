package com.example.cli.taskmanager.cli;

import java.util.Optional;

import com.example.cli.taskmanager.domain.Task;
import com.example.cli.taskmanager.storage.TaskRepository;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
@Command(name = "delete")
class DeleteCommand implements Runnable {
    private final TaskRepository repository;

    DeleteCommand(TaskRepository repository) {
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
           
            repository.delete(found.id());
            System.out.println("Task Delete complete: " + found.title());

        }
    }

}
