package duke.task;
<<<<<<< HEAD
public class Todo extends Task {

=======

/**
 * Represents a todo task that only contains a description.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} with the given description.
     *
     * @param description Description of the todo.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    public Todo(String description) {
        super(description);
    }

<<<<<<< HEAD
=======
    /**
     * Returns the string representation of this todo.
     *
     * @return Formatted todo string.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + super.toString();
    }

<<<<<<< HEAD
=======
    /**
     * Returns the task type marker for a todo.
     *
     * @return {@code [T]}.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    @Override
    public String getType() {
        return "[T]";
    }

<<<<<<< HEAD
=======
    /**
     * Returns the string representation of this todo for saving to storage.
     *
     * @return Save friendly string for a todo.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    @Override
    public String toSaveString() {
        return "todo " + description;
    }
}
