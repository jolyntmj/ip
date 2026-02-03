package duke.task;
import java.util.ArrayList;
import java.util.List;

import duke.exception.DukeException;

<<<<<<< HEAD
=======
/**
 * Represents the in memory list of tasks.
 * Provides operations to mark and delete tasks based on user commands.
 */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
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

<<<<<<< HEAD
=======
    /**
     * Returns the size of the TaskList
     *
     * @return {@code size} given the TaskList.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
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
