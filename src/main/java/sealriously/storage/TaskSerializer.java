package sealriously.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import sealriously.exception.SealriouslyException;
import sealriously.task.Deadline;
import sealriously.task.Event;
import sealriously.task.Task;
import sealriously.task.Todo;

public class TaskSerializer {
    private static final String DELIM_REGEX = "\\s*\\|\\s*";
    private static final String STATUS_DONE = "[X]";
    private static final String STATUS_NOT_DONE = "[ ]";

    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public Task fromStorageString(String line) throws SealriouslyException {
        String[] parts = splitAndValidate(line);

        String type = parts[0];
        boolean isDone = parseDoneFlag(parts[1], line);
        String description = requireNonEmpty(parts[2], "Missing description in save line: " + line);

        Task task = parseTask(type, description, parts, line);
        if (isDone) {
            task.done();
        }
        applyTagsIfPresent(task, type, parts);
        return task;
    }

    public String toStorageString(Task task) throws SealriouslyException {
        if (task == null) {
            throw new SealriouslyException("Cannot save a null task.");
        }
        String base = task.toSaveString();

        String tagsField = task.tagsToStorageField();
        if (tagsField == null || tagsField.trim().isEmpty()) {
            return base;
        }

        return base + " | " + tagsField.trim();
    }

    /**
     * Parses and applies tags from the last save-field (if present).
     */
    private static void applyTagsIfPresent(Task task, String type, String[] parts) {
        String tagsField = extractTagsField(type, parts);
        if (tagsField == null) {
            return;
        }

        String trimmed = tagsField.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        Arrays.stream(trimmed.split("\\s+"))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .forEach(task::addTag);
    }

    /**
     * Returns the tags field if present; otherwise null.
     * Tag field is always the last field (if present).
     *
     * Save formats:
     * Todo:     T | [ ] | desc                 OR  T | [ ] | desc | #fun #school
     * Deadline: D | [ ] | desc | yyyy-MM-dd HHmm      OR  ... | yyyy-MM-dd HHmm | #fun
     * Event:    E | [ ] | desc | start | end         OR  ... | start | end | #fun
     */
    private static String extractTagsField(String type, String[] parts) {
        return switch (type) {
        case TYPE_TODO -> (parts.length == 4 ? parts[3] : null);
        case TYPE_DEADLINE -> (parts.length == 5 ? parts[4] : null);
        case TYPE_EVENT -> (parts.length == 6 ? parts[5] : null);
        default -> null;
        };
    }

    private static String[] splitAndValidate(String line) throws SealriouslyException {
        if (line == null || line.trim().isEmpty()) {
            throw new SealriouslyException("Empty line in save file.");
        }

        String[] parts = line.split(DELIM_REGEX);
        if (parts.length < 3) {
            throw new SealriouslyException("Invalid save line: " + line);
        }
        return parts;
    }

    private static boolean parseDoneFlag(String status, String originalLine) throws SealriouslyException {
        if (STATUS_NOT_DONE.equals(status)) {
            return false;
        }
        if (STATUS_DONE.equals(status)) {
            return true;
        }
        throw new SealriouslyException("Invalid status in save line: " + originalLine);
    }

    private static Task parseTask(String type, String description, String[] parts, String originalLine)
            throws SealriouslyException {

        return switch (type) {
        case TYPE_TODO -> parseTodo(description, parts, originalLine);
        case TYPE_DEADLINE -> parseDeadline(description, parts, originalLine);
        case TYPE_EVENT -> parseEvent(description, parts, originalLine);
        default -> throw new SealriouslyException("Unknown task type in save line: " + originalLine);
        };
    }

    private static Task parseTodo(String description, String[] parts, String originalLine) throws SealriouslyException {
        if (parts.length != 3) {
            throw new SealriouslyException("Invalid Todo save line: " + originalLine);
        }
        return new Todo(description);
    }

    private static Task parseDeadline(String description, String[] parts, String originalLine) throws SealriouslyException {
        if (parts.length != 4) {
            throw new SealriouslyException("Invalid Deadline save line: " + originalLine);
        }
        return new Deadline(description, parseDateTime(parts[3], originalLine));
    }

    private static Task parseEvent(String description, String[] parts, String originalLine) throws SealriouslyException {
        if (parts.length != 5) {
            throw new SealriouslyException("Invalid Event save line: " + originalLine);
        }
        LocalDateTime start = parseDateTime(parts[3], originalLine);
        LocalDateTime end = parseDateTime(parts[4], originalLine);
        return new Event(description, start, end);
    }

    private static LocalDateTime parseDateTime(String raw, String originalLine) throws SealriouslyException {
        try {
            return LocalDateTime.parse(raw, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SealriouslyException("Invalid date/time in save line: " + originalLine);
        }
    }

    private static String requireNonEmpty(String text, String errorMessage) throws SealriouslyException {
        if (text == null || text.trim().isEmpty()) {
            throw new SealriouslyException(errorMessage);
        }
        return text.trim();
    }
}
