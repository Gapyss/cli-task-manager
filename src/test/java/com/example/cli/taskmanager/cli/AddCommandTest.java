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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AddCommandTest {

    private InMemoryTaskRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTaskRepository();
    }

    @Test
    void add_task_with_title_only_uses_medium_priority_and_open_status() {
        new CommandLine(new AddCommand(repo)).execute("Buy milk");

        List<Task> tasks = repo.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).title()).isEqualTo("Buy milk");
        assertThat(tasks.get(0).priority()).isEqualTo(Priority.MEDIUM);
        assertThat(tasks.get(0).status()).isEqualTo(Status.OPEN);
        assertThat(tasks.get(0).dueDate()).isEmpty();
    }

    @Test
    void add_task_with_explicit_priority() {
        new CommandLine(new AddCommand(repo)).execute("Buy milk", "--priority", "HIGH");

        List<Task> tasks = repo.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).priority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void add_task_with_low_priority() {
        new CommandLine(new AddCommand(repo)).execute("Buy milk", "--priority", "LOW");

        assertThat(repo.findAll().get(0).priority()).isEqualTo(Priority.LOW);
    }

    @Test
    void add_task_with_due_date() {
        new CommandLine(new AddCommand(repo)).execute("Buy milk", "--dueDate", "2026-04-20");

        List<Task> tasks = repo.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).dueDate()).isPresent();
        assertThat(tasks.get(0).dueDate().get().toString()).isEqualTo("2026-04-20");
    }

    @Test
    void add_task_prints_confirmation_with_id_and_title() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        new CommandLine(new AddCommand(repo)).execute("Buy milk");

        System.setOut(original);
        assertThat(out.toString()).contains("Task added:").contains("Buy milk");
    }

    @Test
    void add_task_generates_unique_ids() {
        new CommandLine(new AddCommand(repo)).execute("Task 1");
        new CommandLine(new AddCommand(repo)).execute("Task 2");

        List<Task> tasks = repo.findAll();
        assertThat(tasks).hasSize(2);
        String id1 = tasks.get(0).id();
        String id2 = tasks.get(1).id();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void add_multiple_tasks_all_saved() {
        new CommandLine(new AddCommand(repo)).execute("Task A");
        new CommandLine(new AddCommand(repo)).execute("Task B");
        new CommandLine(new AddCommand(repo)).execute("Task C");

        assertThat(repo.findAll()).hasSize(3);
    }
}
