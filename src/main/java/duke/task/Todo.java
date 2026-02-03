package duke.task;
public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + super.getDescription();
    }

    @Override
    public String getType() {
        return "[T]";
    }

    @Override
    public String toSaveString() {
        return "todo " + description;
    }
}
