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

class ListCommandTest {

    private InMemoryTaskRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
    }

    private String captureOutput(String... args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        new CommandLine(new ListCommand(repo)).execute(args);
        System.setOut(original);
        return out.toString();
    }

    @Test
    void list_all_tasks_shows_all() {
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Walk dog", Optional.empty(), Priority.LOW, Status.DONE));

        String output = captureOutput();

        assertThat(output).contains("Buy milk").contains("Walk dog");
    }

    @Test
    void list_empty_repository_produces_no_output() {
        String output = captureOutput();
        assertThat(output).isEmpty();
    }

    @Test
    void list_filter_by_open_status_excludes_done_tasks() {
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Walk dog", Optional.empty(), Priority.LOW, Status.DONE));

        String output = captureOutput("--status", "OPEN");

        assertThat(output).contains("Buy milk").doesNotContain("Walk dog");
    }

    @Test
    void list_filter_by_done_status_excludes_open_tasks() {
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Walk dog", Optional.empty(), Priority.LOW, Status.DONE));

        String output = captureOutput("--status", "DONE");

        assertThat(output).contains("Walk dog").doesNotContain("Buy milk");
    }

    @Test
    void list_sort_by_priority_orders_low_before_high() {
        repo.save(new Task("1", "High task", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Low task", Optional.empty(), Priority.LOW, Status.OPEN));
        repo.save(new Task("3", "Medium task", Optional.empty(), Priority.MEDIUM, Status.OPEN));

        String output = captureOutput("--sort", "PRIORITY");

        // enum ordinal order: LOW(0) < MEDIUM(1) < HIGH(2)
        int lowPos = output.indexOf("Low task");
        int medPos = output.indexOf("Medium task");
        int highPos = output.indexOf("High task");
        assertThat(lowPos).isLessThan(medPos);
        assertThat(medPos).isLessThan(highPos);
    }

    @Test
    void list_sort_by_due_date_orders_earliest_first() {
        repo.save(new Task("1", "Later task", Optional.of(LocalDate.of(2026, 12, 1)), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Earlier task", Optional.of(LocalDate.of(2026, 5, 1)), Priority.HIGH, Status.OPEN));

        String output = captureOutput("--sort", "DUE");

        int earlierPos = output.indexOf("Earlier task");
        int laterPos = output.indexOf("Later task");
        assertThat(earlierPos).isLessThan(laterPos);
    }

    @Test
    void list_sort_by_due_date_places_no_due_date_last() {
        repo.save(new Task("1", "No due date", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Has due date", Optional.of(LocalDate.of(2026, 5, 1)), Priority.HIGH, Status.OPEN));

        String output = captureOutput("--sort", "DUE");

        int withDatePos = output.indexOf("Has due date");
        int noDueDatePos = output.indexOf("No due date");
        assertThat(withDatePos).isLessThan(noDueDatePos);
    }

    @Test
    void list_task_without_due_date_shows_none() {
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        String output = captureOutput();

        assertThat(output).contains("due: none");
    }

    @Test
    void list_task_with_due_date_shows_formatted_date() {
        repo.save(new Task("1", "Buy milk", Optional.of(LocalDate.of(2026, 5, 1)), Priority.HIGH, Status.OPEN));

        String output = captureOutput();

        assertThat(output).contains("due: 2026-05-01");
    }

    @Test
    void list_shows_task_id_and_status() {
        repo.save(new Task("abc-123", "Buy milk", Optional.empty(), Priority.MEDIUM, Status.OPEN));

        String output = captureOutput();

        assertThat(output).contains("abc-123").contains("OPEN");
    }
}
