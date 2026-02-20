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

/**
 * Converts Task objects to and from their storage string representations.
 *
 * This class handles:
 * - Parsing tasks from lines in the save file.
 * - Serializing tasks into a consistent storage format.
 */
public class TaskSerializer {

    private static final String DELIM_REGEX = "\\s*\\|\\s*";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final DateTimeFormatter STORAGE_DT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");


    /**
     * Parses a line from the save file into a Task object.
     *
     * @param line Raw storage line.
     * @return Parsed Task.
     * @throws SealriouslyException If the line format is invalid.
     */
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

    /**
     * Serializes a Task into a single-line storage representation.
     *
     * @param task Task to serialize.
     * @return Storage string.
     * @throws SealriouslyException If task cannot be serialized.
     */
    public String toStorageString(Task task) throws SealriouslyException {
        if (task == null) {
            throw new SealriouslyException("Cannot save a null task.");
        }
    
        String core = toCoreStorageString(task);
        String tagsField = toTagsField(task);
    
        if (tagsField.isEmpty()) {
            return core;
        }
        return core + " | " + tagsField;
    }

    /**
     * Builds the core storage string without tags.
     *
     * @param task Task to serialize.
     * @return Core storage string.
     * @throws SealriouslyException If task type is unknown.
     */
    private static String toCoreStorageString(Task task) throws SealriouslyException {
        String status = task.isDone() ? "[X]" : "[ ]";
    
        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription();
        }
        if (task instanceof Deadline) {
            return toDeadlineStorageString((Deadline) task, status);
        }
        if (task instanceof Event) {
            return toEventStorageString((Event) task, status);
        }
    
        throw new SealriouslyException("Unknown task type: " + task.getClass().getSimpleName());
    }

    /**
     * Serializes a Deadline task into storage format.
     *
     * @param d Deadline task.
     * @param status Completion status token.
     * @return Storage string.
     */
    private static String toDeadlineStorageString(Deadline d, String status) {
        return "D | " + status + " | " + d.getDescription()
                + " | " + d.getBy().format(STORAGE_DT_FORMAT);
    }

    /**
     * Serializes an Event task into storage format.
     *
     * @param e Event task.
     * @param status Completion status token.
     * @return Storage string.
     */
    private static String toEventStorageString(Event e, String status) {
        return "E | " + status + " | " + e.getDescription()
                + " | " + e.getStart().format(STORAGE_DT_FORMAT)
                + " | " + e.getEnd().format(STORAGE_DT_FORMAT);
    }

    /**
     * Serializes the tags of a task into storage format.
     *
     * @param task Task containing tags.
     * @return Tags string (empty if none).
     */
    private static String toTagsField(Task task) {
        // adjust based on your Task API:
        // If you already have tagsToStorageField(), use it.
        String tags = task.tagsToStorageField();
        return tags == null ? "" : tags.trim();
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

    /**
     * Splits a storage line into parts and validates basic structure.
     *
     * @param line Raw storage line.
     * @return Parsed parts.
     * @throws SealriouslyException If the line is invalid.
     */
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

    /**
     * Parses the done flag from storage.
     *
     * @param status       Status token from storage.
     * @param originalLine Original line (for error reporting).
     * @return true if marked as done, false otherwise.
     * @throws SealriouslyException If the flag is not recognized.
     */
    private static boolean parseDoneFlag(String status, String originalLine) throws SealriouslyException {
        String s = status == null ? "" : status.trim();
    
        if (s.equals("[X]") || s.equals("X")) {
            return true;
        }
        if (s.equals("[ ]") || s.isEmpty()) {
            return false;
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

    /**
     * Parses a todo task from storage parts.
     *
     * @param description  Task description.
     * @param parts        Split storage parts.
     * @param originalLine Original line (for error reporting).
     * @return Parsed Task.
     * @throws SealriouslyException If required fields are missing/invalid.
     */
    private static Task parseTodo(String description, String[] parts, String originalLine) throws SealriouslyException {
        if (parts.length != 3 && parts.length != 4) {
            throw new SealriouslyException("Invalid Todo save line: " + originalLine);
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline task from storage parts.
     *
     * @param description  Task description.
     * @param parts        Split storage parts.
     * @param originalLine Original line (for error reporting).
     * @return Parsed Task.
     * @throws SealriouslyException If required fields are missing/invalid.
     */
    private static Task parseDeadline(String description, String[] parts, String originalLine)
        throws SealriouslyException {

        if (parts.length != 4 && parts.length != 5) {
            throw new SealriouslyException("Invalid Deadline save line: " + originalLine);
        }
        return new Deadline(description, parseDateTime(parts[3], originalLine));
    }

    /**
     * Parses an event task from storage parts.
     *
     * @param description  Task description.
     * @param parts        Split storage parts.
     * @param originalLine Original line (for error reporting).
     * @return Parsed Task.
     * @throws SealriouslyException If required fields are missing/invalid.
    */
    private static Task parseEvent(String description, String[] parts, String originalLine)
        throws SealriouslyException {

        if (parts.length != 5 && parts.length != 6) {
            throw new SealriouslyException("Invalid Event save line: " + originalLine);
        }
        LocalDateTime start = parseDateTime(parts[3], originalLine);
        LocalDateTime end = parseDateTime(parts[4], originalLine);
        return new Event(description, start, end);
    }

    /**
     * Parses a LocalDateTime from storage format.
     *
     * @param raw          Raw date-time string.
     * @param originalLine Original line (for error reporting).
     * @return Parsed LocalDateTime.
     * @throws SealriouslyException If parsing fails.
     */
    private static LocalDateTime parseDateTime(String raw, String originalLine)
        throws SealriouslyException {

        String trimmed = raw == null ? "" : raw.trim();

        try {
            // Canonical storage format
            return LocalDateTime.parse(trimmed, STORAGE_DT_FORMAT);
        } catch (DateTimeParseException ignored) {
            // Fallback: legacy ISO format (e.g. 2026-01-01T00:00)
            try {
                return LocalDateTime.parse(trimmed);
            } catch (DateTimeParseException e) {
                throw new SealriouslyException(
                        "Invalid date/time in save line: " + originalLine);
            }
        }
    }

    /**
     * Ensures a string is non-null and non-blank.
     *
     * @param text         Text to validate.
     * @param errorMessage Message to throw if invalid.
     * @return Trimmed non-empty text.
     * @throws SealriouslyException If invalid.
     */
    private static String requireNonEmpty(String text, String errorMessage) throws SealriouslyException {
        if (text == null || text.trim().isEmpty()) {
            throw new SealriouslyException(errorMessage);
        }
        return text.trim();
    }
}
