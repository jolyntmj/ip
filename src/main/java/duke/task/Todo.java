package duke.task;

/**
 * Represents a todo task that only contains a description.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of this todo.
     *
     * @return Formatted todo string.
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + super.getDescription();
    }

    /**
     * Returns the task type marker for a todo.
     *
     * @return {@code [T]}.
     */
    @Override
    public String getType() {
        return "[T]";
    }

    /**
     * Returns the string representation of this todo for saving to storage.
     *
     * @return Save friendly string for a todo.
     */
    @Override
    public String toSaveString() {
        return "todo " + description;
    }
}
