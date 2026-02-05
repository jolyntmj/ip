package duke.task;
import java.util.ArrayList;
import java.util.List;

import duke.exception.DukeException;

/**
 * Represents the in memory list of tasks.
 * Provides operations to mark and delete tasks based on user commands.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Constructs a {@code TaskList} wrapping an existing list of tasks.
     *
     * @param tasks Backing list of tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Constructs an empty {@code TaskList}.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Adds the given task to the task list.
     *
     * @param task The task to be added.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task specified by the given {@code delete} command.
     *
     * @param index Specify task in the TaskList.
     * @throws DukeException If the task number is missing/invalid or out of range.
     */
    public Task delete(int index) throws DukeException {
        validateIndex(index);
        return tasks.remove(index);

    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index The index of the task to mark (0-based).
     * @return The task that was marked as done.
     * @throws DukeException If the index is invalid.
     */
    public Task mark(int index) throws DukeException {
        validateIndex(index);
        Task t = get(index);
        t.done();
        return t;
    }

    /**
     * Returns the specific task in the TaskList.
     *
     * @param index Specify task in the TaskList.
     * @return {@code task} when the index is provided.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the size of the TaskList
     *
     * @return {@code size} given the TaskList.
     */
    public int size() {
        return tasks.size();
    }


    /**
     * Check if the index is within range
     *
     * @param index Specify task in the TaskList
     * @throw DukeException If the index is not within range.
     */
    private void validateIndex(int index) throws DukeException {
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("That task number does not exist.");
        }
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}
