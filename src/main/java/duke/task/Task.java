package duke.task;

/**
 * Represents an abstract task in Duke.
 * A task has a description and a completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a {@code Task} with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of this task.
     *
     * @return {@code "X"} if the task is done, otherwise a single space {@code " "}.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks this task as done.
     */
    public void done() {
        this.isDone = true;
    }

    /**
     * Returns a string representation of this task.
     *
     * @return Task description.
     */
    public String toString() {
        return this.description;
    }

    /**
     * Returns the task type marker (e.g., {@code [T]}, {@code [D]}, {@code [E]}).
     *
     * @return Type marker of the task.
     */
    public abstract String getType();

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if done, {@code false} otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the string representation of this task for saving to storage.
     *
     * @return Save-friendly string for this task.
     */
    public abstract String toSaveString();
}
