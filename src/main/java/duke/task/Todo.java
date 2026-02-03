package duke.task;

/**
 * Represents a to-do task that only contains a description.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} with the given description.
     *
     * @param description Description of the to-do.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of this to-do.
     *
     * @return Formatted to-do string.
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + super.toString();
    }

    /**
     * Returns the task type marker for a to-do.
     *
     * @return {@code [T]}.
     */
    @Override
    public String getType() {
        return "[T]";
    }

    /**
     * Returns the string representation of this to-do for saving to storage.
     *
     * @return Save-friendly string for a to-do.
     */
    @Override
    public String toSaveString() {
        return "todo " + description;
    }
}
