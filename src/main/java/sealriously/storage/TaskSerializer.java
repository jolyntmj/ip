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
 * Converts tasks to and from the save file format.
 *
 * This class is responsible for:
 * - Reading a single line from the storage file and turning it into
 *   the correct Task object (e.g., Todo, Deadline, Event).
 * - Converting a Task object into a properly formatted string
 *   for saving back to disk.
 *
 * The storage format uses a structured, delimiter-based layout where
 * each task stores its type, completion status, description,
 * date-time information, and tags in a fixed order.
 *
 * If a line from the save file is malformed or does not follow the
 * expected format, a SealriouslyException will be thrown.
 */
public class TaskSerializer {
    private static final String DELIM_REGEX = "\\s*\\|\\s*";
    private static final String STATUS_DONE = "[X]";
    private static final String STATUS_NOT_DONE = "[ ]";

    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";

    private static final DateTimeFormatter DATE_FORMAT =
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
        if (parts.length != 3) {
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
    private static Task parseDeadline(String description, String[] parts,
        String originalLine) throws SealriouslyException {

        if (parts.length != 4) {
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
    private static Task parseEvent(String description, String[] parts,
        String originalLine) throws SealriouslyException {

        if (parts.length != 5) {
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
    private static LocalDateTime parseDateTime(String raw, String originalLine) throws SealriouslyException {
        try {
            return LocalDateTime.parse(raw, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SealriouslyException("Invalid date/time in save line: " + originalLine);
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
