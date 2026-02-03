package duke.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {

    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "[D][" + getStatusIcon() + "]" + super.toString() + " (due: " + by.format(out) + ")";
    }

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
