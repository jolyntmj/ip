package duke.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task with a start and end date/time.
 */
public class Event extends Task {

    protected LocalDateTime start;
    protected LocalDateTime end;

    /**
     * Constructs an {@code Event} with the specified description, start time, and end time.
     *
     * @param description Description of the event.
     * @param start Start date/time of the event.
     * @param end End date/time of the event.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the string representation of this event.
     *
     * @return Formatted event string.
     */
    @Override
    public String toString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "[E][" + getStatusIcon() + "]" + super.toString() + " (start: " + start.format(out) + " due: " + end.format(out) + ")";
    }

    /**
     * Returns the task type marker for an event.
     *
     * @return {@code [E]}.
     */
    @Override
    public String getType() {
        return "[E]";
    }

    /**
     * Returns the string representation of this event for saving to storage.
     *
     * @return Save-friendly string for an event.
     */
    @Override
    public String toSaveString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "event " + description + " START: " + this.start.format(out) + " DUE: " + this.end.format(out);
    }
}
