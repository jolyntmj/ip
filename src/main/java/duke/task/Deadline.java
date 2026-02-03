package duke.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task with a due date/time.
 */
public class Deadline extends Task {

    protected LocalDateTime by;

    /**
     * Constructs a {@code Deadline} with the specified description and due date/time.
     *
     * @param description Description of the deadline.
     * @param by Due date/time of the deadline.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the string representation of this deadline.
     *
     * @return Formatted deadline string.
     */
    @Override
    public String toString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "[D][" + getStatusIcon() + "] " + super.getDescription() + " (due: " + by.format(out) + ")";
    }

    /**
     * Returns the task type marker for a deadline.
     *
     * @return {@code [D]}.
     */
    @Override
    public String getType() {
        return "[D]";
    }

    /**
     * Returns the string representation of this deadline for saving to storage.
     *
     * @return Save friendly string for a deadline.
     */
    @Override
    public String toSaveString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "deadline " + description + " DUE: " + by.format(out);
    }
}
