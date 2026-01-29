public class Event extends Task {

    protected String start;
    protected String end;

    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "]" + super.toString() + " (from: " + start + " to " + end + ")";
    }

    @Override
    public String getType() {
        return "[E]";
    }

    @Override
    public String toSaveString() {
        return "event " + description + " /from " + this.start + " /to " + this.end;
    }
}
