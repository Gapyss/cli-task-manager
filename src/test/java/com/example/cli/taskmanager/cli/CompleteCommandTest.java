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
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CompleteCommandTest {

    private InMemoryTaskRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
    }

    @Test
    void complete_existing_task_sets_status_to_done() {
        repo.save(new Task("42", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        new CommandLine(new CompleteCommand(repo)).execute("42");

        assertThat(repo.findById("42"))
                .isPresent()
                .hasValueSatisfying(t -> assertThat(t.status()).isEqualTo(Status.DONE));
    }

    @Test
    void complete_existing_task_preserves_title_and_priority() {
        repo.save(new Task("42", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        new CommandLine(new CompleteCommand(repo)).execute("42");

        Task completed = repo.findById("42").get();
        assertThat(completed.title()).isEqualTo("Buy milk");
        assertThat(completed.priority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void complete_existing_task_preserves_due_date() {
        LocalDate dueDate = LocalDate.of(2026, 6, 15);
        repo.save(new Task("42", "Buy milk", Optional.of(dueDate), Priority.MEDIUM, Status.OPEN));

        new CommandLine(new CompleteCommand(repo)).execute("42");

        Task completed = repo.findById("42").get();
        assertThat(completed.dueDate()).isPresent();
        assertThat(completed.dueDate().get()).isEqualTo(dueDate);
    }

    @Test
    void complete_task_prints_confirmation_with_title() {
        repo.save(new Task("42", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        new CommandLine(new CompleteCommand(repo)).execute("42");

        System.setOut(original);
        assertThat(out.toString()).contains("Task completed:").contains("Buy milk");
    }

    @Test
    void complete_unknown_id_prints_error_with_id() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        new CommandLine(new CompleteCommand(repo)).execute("unknown-id");

        System.setOut(original);
        assertThat(out.toString()).contains("cannot find task id").contains("unknown-id");
    }

    @Test
    void complete_unknown_id_does_not_modify_repository() {
        repo.save(new Task("1", "Existing task", Optional.empty(), Priority.LOW, Status.OPEN));

        new CommandLine(new CompleteCommand(repo)).execute("999");

        assertThat(repo.findAll()).hasSize(1);
        assertThat(repo.findById("1").get().status()).isEqualTo(Status.OPEN);
    }
}
