package sealriously.task;
import java.util.ArrayList;
import java.util.List;

import sealriously.exception.SealriouslyException;

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
        if (tasks == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }
        assert tasks != null : "TaskList backing list should not be null";

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
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        assert task != null : "TaskList.add: task should not be null";

        tasks.add(task);
    }

    /**
     * Deletes the task specified by the given {@code delete} command.
     *
     * @param index Specify task in the TaskList.
     * @throws SealriouslyException If the task number is missing/invalid or out of range.
     */
    public Task delete(int index) throws SealriouslyException {
        validateIndex(index);
        return tasks.remove(index);

    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index The index of the task to mark (0-based).
     * @return The task that was marked as done.
     * @throws SealriouslyException If the index is invalid.
     */
    public Task mark(int index) throws SealriouslyException {
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
        assert index >= 0 && index < tasks.size() : "TaskList.get: index out of bounds: " + index;

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
     * @throw SealriouslyException If the index is not within range.
     */
    private void validateIndex(int index) throws SealriouslyException {
        if (index < 0 || index >= tasks.size()) {
            throw new SealriouslyException("That task number does not exist.");
        }
    }

    /**
     * Returns whether the list is empty.
     *
     * @return true if there are no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the backing list of tasks.
     * Caller should not modify it directly unless intended.
     *
     * @return backing list of tasks.
     */
    public List<Task> getTasks() {
        return this.tasks;
    }
}
