package com.example.cli.taskmanager.cli;

import com.example.cli.taskmanager.domain.Priority;
import com.example.cli.taskmanager.domain.Status;
import com.example.cli.taskmanager.domain.Task;
import com.example.cli.taskmanager.storage.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteCommandTest {

    private InMemoryTaskRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
    }

    @Test
    void delete_existing_task_removes_it_from_repository() {
        repo.save(new Task("42", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        new CommandLine(new DeleteCommand(repo)).execute("42");

        assertThat(repo.findAll()).isEmpty();
        assertThat(repo.findById("42")).isEmpty();
    }

    @Test
    void delete_existing_task_prints_confirmation_with_title() {
        repo.save(new Task("42", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        new CommandLine(new DeleteCommand(repo)).execute("42");

        System.setOut(original);
        assertThat(out.toString()).contains("Task Delete complete").contains("Buy milk");
    }

    @Test
    void delete_unknown_id_prints_error_with_id() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        new CommandLine(new DeleteCommand(repo)).execute("unknown-id");

        System.setOut(original);
        assertThat(out.toString()).contains("cannot find task id").contains("unknown-id");
    }

    @Test
    void delete_unknown_id_does_not_affect_existing_tasks() {
        repo.save(new Task("1", "Task 1", Optional.empty(), Priority.HIGH, Status.OPEN));

        new CommandLine(new DeleteCommand(repo)).execute("999");

        assertThat(repo.findAll()).hasSize(1);
    }

    @Test
    void delete_only_removes_the_specified_task() {
        repo.save(new Task("1", "Task 1", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Task 2", Optional.empty(), Priority.HIGH, Status.OPEN));

        new CommandLine(new DeleteCommand(repo)).execute("1");

        assertThat(repo.findAll()).hasSize(1);
        assertThat(repo.findById("2")).isPresent();
        assertThat(repo.findById("1")).isEmpty();
    }
}
