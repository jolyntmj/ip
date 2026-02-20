package sealriously.task;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an abstract task in Sealriously.
 * A task has a description and a completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Constructs a {@code Task} with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Task description cannot be null.");
        }
        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        assert description != null : "Task description should not be null";
        assert !description.trim().isEmpty() : "Task description should not be empty";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of this task.
     *
     * @return {@code "X"} if the task is done, otherwise a single space {@code " "}.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks this task as done.
     */
    public void done() {
        this.isDone = true;
    }

    /**
     * Returns a string description of this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }

    /**
     * Returns the task type marker (e.g., {@code [T]}, {@code [D]}, {@code [E]}).
     *
     * @return Type marker of the task.
     */
    public abstract String getType();

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if done, {@code false} otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the string representation of this task for saving to storage.
     *
     * @return Save friendly string for this task.
     */
    public abstract String toSaveString();

    public void addTag(String tag) {
        tags.add(normalize(tag));
    }

    public Set<String> getTags() {
        return tags;
    }

    /**
     * Returns tags formatted for UI display.
     * Example: " [#fun #school]" or "" if no tags.
     */
    public String tagsToDisplay() {
        if (tags.isEmpty()) {
            return "";
        }
        String tagStr = tags.stream().collect(Collectors.joining(" "));
        return " [" + tagStr + "]";
    }

    /**
     * Returns tags formatted for storage (space-separated), or "" if none.
     * Example: "#fun #school"
     */
    public String tagsToStorageField() {
        if (tags.isEmpty()) {
            return "";
        }
        return tags.stream().collect(Collectors.joining(" "));
    }

    private String normalize(String tag) {
        String t = tag.trim();
        if (t.isEmpty()) {
            return t;
        }
        if (!t.startsWith("#")) {
            t = "#" + t;
        }
        return t;
    }
}
