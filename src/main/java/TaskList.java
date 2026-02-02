import java.util.ArrayList;
import java.util.List;

public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int index) throws DukeException {
        validateIndex(index);
        return tasks.remove(index);
    
    }

    public Task mark(int index) throws DukeException {
        validateIndex(index);
        Task t = get(index);
        t.done();
        return t;
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    private void validateIndex(int index) throws DukeException {
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("That task number does not exist.");
        }
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }    
    
}
