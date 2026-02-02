package duke.task;
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void done() {
        this.isDone = true;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }
    public abstract String getType();

    public boolean isDone() {
        return this.isDone;
    }

    public boolean isNotDone() {
        this.isDone = false;
        return this.isDone;
    }

    public abstract String toSaveString();
}
