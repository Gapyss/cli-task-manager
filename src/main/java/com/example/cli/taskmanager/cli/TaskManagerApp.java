package com.example.cli.taskmanager.cli;

import com.example.cli.taskmanager.storage.JsonFileTaskRepository;
import com.example.cli.taskmanager.storage.TaskRepository;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "tasks")
public class TaskManagerApp implements Runnable {

    public static void main(String[] args) {
        TaskRepository repository = new JsonFileTaskRepository();
        AddCommand addCommand = new AddCommand(repository);
        ListCommand listCommand = new ListCommand(repository);
        CompleteCommand completeCommand = new CompleteCommand(repository);
        DeleteCommand deleteCommand = new DeleteCommand(repository);

        new CommandLine(new TaskManagerApp())
                .addSubcommand("add", addCommand)
                .addSubcommand("list", listCommand)
                .addSubcommand("complete", completeCommand)
                .addSubcommand("delete", deleteCommand)
                .execute(args);

    }

    @Override
    public void run() {
        // no subcommand given — print help
        new CommandLine(this).usage(System.out);
    }
}