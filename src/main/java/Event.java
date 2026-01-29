import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {

    protected LocalDateTime start;
    protected LocalDateTime end;

    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "[E][" + getStatusIcon() + "]" + super.toString() + " (start: " + start.format(out) + " due: " + end.format(out) + ")";
    }

    @Override
    public String getType() {
        return "[E]";
    }

    @Override
    public String toSaveString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return "event " + description + " START: " + this.start.format(out) + " DUE: " + this.end.format(out);
    }
}
