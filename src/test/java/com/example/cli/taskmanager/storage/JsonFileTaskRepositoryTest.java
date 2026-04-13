package com.example.cli.taskmanager.storage;

import com.example.cli.taskmanager.domain.Priority;
import com.example.cli.taskmanager.domain.Status;
import com.example.cli.taskmanager.domain.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JsonFileTaskRepositoryTest {

    @Test
    void load_from_nonexistent_file_returns_empty(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("nonexistent.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(nonExistent);

        assertThat(repo.findAll()).isEmpty();
    }

    @Test
    void save_multiple_tasks_all_persisted(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Walk dog", Optional.empty(), Priority.LOW, Status.DONE));

        JsonFileTaskRepository reloaded = new JsonFileTaskRepository(file);
        assertThat(reloaded.findAll()).hasSize(2);
    }

    @Test
    void delete_persists_removal_to_disk(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));
        repo.save(new Task("2", "Walk dog", Optional.empty(), Priority.LOW, Status.DONE));

        repo.delete("1");

        JsonFileTaskRepository reloaded = new JsonFileTaskRepository(file);
        assertThat(reloaded.findAll()).hasSize(1);
        assertThat(reloaded.findById("2")).isPresent();
        assertThat(reloaded.findById("1")).isEmpty();
    }

    @Test
    void findById_returns_correct_task(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);
        repo.save(new Task("42", "Special task", Optional.empty(), Priority.MEDIUM, Status.OPEN));

        Optional<Task> found = repo.findById("42");

        assertThat(found).isPresent();
        assertThat(found.get().title()).isEqualTo("Special task");
        assertThat(found.get().priority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    void findById_unknown_id_returns_empty(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);

        assertThat(repo.findById("999")).isEmpty();
    }

    @Test
    void save_task_with_due_date_round_trips_correctly(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);
        LocalDate dueDate = LocalDate.of(2026, 6, 15);
        repo.save(new Task("1", "Task with date", Optional.of(dueDate), Priority.MEDIUM, Status.OPEN));

        JsonFileTaskRepository reloaded = new JsonFileTaskRepository(file);
        Task loaded = reloaded.findById("1").get();
        assertThat(loaded.dueDate()).isPresent();
        assertThat(loaded.dueDate().get()).isEqualTo(dueDate);
    }

    @Test
    void save_overwrites_existing_task_with_same_id(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);
        repo.save(new Task("1", "Original title", Optional.empty(), Priority.LOW, Status.OPEN));
        repo.save(new Task("1", "Updated title", Optional.empty(), Priority.HIGH, Status.DONE));

        assertThat(repo.findAll()).hasSize(1);
        assertThat(repo.findById("1").get().title()).isEqualTo("Updated title");
        assertThat(repo.findById("1").get().status()).isEqualTo(Status.DONE);
    }

    @Test
    void delete_nonexistent_id_leaves_repository_unchanged(@TempDir Path tempDir) {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repo = new JsonFileTaskRepository(file);
        repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

        repo.delete("999");

        assertThat(repo.findAll()).hasSize(1);
    }
}
