package domain;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.cli.taskmanager.domain.Priority;
import com.example.cli.taskmanager.domain.Status;
import com.example.cli.taskmanager.domain.Task;
import com.example.cli.taskmanager.storage.InMemoryTaskRepository;
import com.example.cli.taskmanager.storage.JsonFileTaskRepository;
import java.nio.file.Files;


import static org.assertj.core.api.Assertions.assertThat;

class TaskRepositoryTest {

    @Test
    void save_and_find_by_id() {
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task task = new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN);

        repo.save(task);

        Optional<Task> found = repo.findById("1");

        assertThat(found).isPresent();
        assertThat(found.get().title()).isEqualTo("Buy milk");

    }

    @Test
    void find_by_id_unknown_id_returns_empty() {

        InMemoryTaskRepository repo = new InMemoryTaskRepository();

        Optional<Task> found = repo.findById("999");
        assertThat(found).isEmpty();

    }

    @Test
    void find_all_returns_all_saved_tasks() {
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task task = new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN);
        Task task2 = new Task("2", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN);

        repo.save(task);
        repo.save(task2);

        List<Task> found = repo.findAll();

        assertThat(found).hasSize(2);

    }

    @Test
    void delete_removes_task() {
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task task = new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN);

        repo.save(task);

        repo.delete("1");

        assertThat(repo.findAll()).isEmpty();
    }
@Test
void save_persists_to_disk() throws Exception {
    Path tempFile = Files.createTempFile("tasks-test", ".json");
    
    JsonFileTaskRepository repo = new JsonFileTaskRepository(tempFile); // ← needs custom path
    repo.save(new Task("1", "Buy milk", Optional.empty(), Priority.HIGH, Status.OPEN));

    JsonFileTaskRepository repo2 = new JsonFileTaskRepository(tempFile);
    assertThat(repo2.findAll()).hasSize(1);
}




}
