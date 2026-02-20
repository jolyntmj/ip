package sealriously.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task with a start and end date/time.
 */
public class Event extends Task {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
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
        if (start == null || end == null) {
            throw new IllegalArgumentException("Event start/end cannot be null.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Event end cannot be before start.");
        }

        assert start != null : "Event.start should not be null";
        assert end != null : "Event.end should not be null";
        assert !end.isBefore(start) : "Event end time should not be before start time";

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
        return "[E][" + getStatusIcon() + "] " + super.getDescription()
            + " (start: " + start.format(FORMAT)
            + " due: " + end.format(FORMAT) + ")";
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
     * @return Save friendly string for an event.
     */
    @Override
    public String toSaveString() {
        return "E | " + getStatusIcon()
                + " | " + description
                + " | " + start
                + " | " + end;
    }

    /**
     * Returns the start date/time of this event.
     *
     * @return Start date/time.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Returns the end date/time of this event.
     *
     * @return End date/time.
     */
    public LocalDateTime getEnd() {
        return end;
    }

}
