package sealriously.parser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import sealriously.command.CommandType;
import sealriously.exception.SealriouslyException;
import sealriously.task.Deadline;
import sealriously.task.Event;

/**
 * Parses user commands and determines what action Sealriously should take.
 */
public class Parser {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final String TOKEN_DUE = "DUE:";
    private static final String TOKEN_START = "START:";

    private static final int SPLIT_LIMIT_TWO_PARTS = 2;

    public static class TagArgs {
        public final int index;     // zero-based
        public final String tag;    // raw tag token, e.g. "#fun"

        public TagArgs(int index, String tag) {
            this.index = index;
            this.tag = tag;
        }
    }

    /**
     * Determines the {@link CommandType} based on the first word of the input.
     *
     * @param input Full user input string.
     * @return Parsed {@code CommandType}. Returns {@code UNKNOWN} if the command is not recognised.
     */
    public CommandType parseCommandType(String input) {
        if (input == null) {
                return CommandType.UNKNOWN;
        }
        assert input != null : "Parser.parseCommandType: input should not be null";

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return CommandType.UNKNOWN;
        }
        String firstWord = trimmed.split("\\s+", 2)[0].toLowerCase();

        return switch (firstWord) {
        case "list" -> CommandType.LIST;
        case "todo" -> CommandType.TODO;
        case "deadline" -> CommandType.DEADLINE;
        case "event" -> CommandType.EVENT;
        case "mark" -> CommandType.MARK;
        case "delete" -> CommandType.DELETE;
        case "find" -> CommandType.FIND;
        case "tag" -> CommandType.TAG;
        case "bye" -> CommandType.BYE;
        default -> CommandType.UNKNOWN;
        };
    }

    /**
     * Parses tag command arguments.
     * Format: tag INDEX #tag
     * Example: "tag 3 #fun" -> index=2, tag="#fun"
     */
    public TagArgs parseTagArgs(String input) throws SealriouslyException {
        requireInputNotNull(input, "Usage: tag TASK_NUMBER #tag");
        assert input != null : "Parser.parseTagArgs: input should not be null";

        String[] parts = input.trim().split("\\s+", 3);
        if (parts.length < 3) {
            throw new SealriouslyException("Usage: tag TASK_NUMBER #tag");
        }

        int index;
        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new SealriouslyException("Task number must be a number.");
        }

        String tag = parts[2].trim();
        if (tag.isEmpty()) {
            throw new SealriouslyException("Tag cannot be empty. Usage: tag TASK_NUMBER #tag");
        }

        return new TagArgs(index, tag);
    }

    /**
     * Parses a one based task number from the input and converts it to a zero based index.
     * Example: {@code "mark 2"} returns {@code 1}.
     *
     * @param input Full user input containing a task number.
     * @return Zero based index of the task.
     * @throws SealriouslyException If the task number is missing or not a valid integer.
     */
    public int parseIndex(String input) throws SealriouslyException {
        requireInputNotNull(input, "Please specify a task number.");
        assert input != null : "Parser.parseIndex: input should not be null";

        String[] parts = input.split(" ", 2);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new SealriouslyException("Please specify a task number.");
        }

        int index;

        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new SealriouslyException("Task number must be a number.");
        }
        return index;
    }

    /**
     * Extracts the description for a todo command.
     * Example: {@code "todo read book"} returns {@code "read book"}.
     *
     * @param input Full user input string.
     * @return Description portion of the todo command.
     * @throws SealriouslyException If the description is missing.
     */
    public String parseTodo(String input) throws SealriouslyException {
        requireInputNotNull(input, "The description of a todo cannot be empty!");
        assert input != null : "Parser.parseTodo: input should not be null";
        assert input.trim().toLowerCase().startsWith("todo") : "parseTodo called with non-todo input";

        String description = extractRemainder(input);

        if (description.isEmpty()) {
            throw new SealriouslyException("The description of a todo cannot be empty!");
        }

        return description;

    }

    /**
     * Parses a deadline command into a {@link Deadline} object.
     * Expected format: {@code "deadline <desc> DUE: yyyy-MM-dd HHmm"}.
     *
     * @param input Full user input string.
     * @return Parsed {@link Deadline}.
     * @throws SealriouslyException If description or "DUE:" is missing, or the datetime format is invalid.
     */
    public Deadline parseDeadline(String input) throws SealriouslyException {
        requireInputNotNull(input, "The description and DUE: of a deadline cannot be empty!");
        assert input != null : "Parser.parseDeadline: input should not be null";
        assert input.trim().toLowerCase().startsWith("deadline") : "parseDeadline called with non-deadline input";
    
        String remainder = requireNonEmpty(extractRemainder(input),
                "The description and DUE: of a deadline cannot be empty!");
    
        String[] parts = splitByToken(remainder, TOKEN_DUE);
    
        String description = requireNonEmpty(parts[0], "The description of a deadline cannot be empty!");
        String byRaw = requireNonEmpty(parts[1], "The DUE: of a deadline cannot be empty!");
    
        return new Deadline(description, parseDateTime(byRaw));
    }    

    /**
     * Parses an event command into an {@link Event} object.
     * Expected format: {@code "event <desc> START:  yyyy-MM-dd HHmm DUE:  yyyy-MM-dd HHmm"}.
     *
     * @param input Full user input string.
     * @return Parsed {@link Event}.
     * @throws SealriouslyException If description, "START: ", or "DUE: " is missing, or the datetime format is invalid.
     */
    public Event parseEvent(String input) throws SealriouslyException {
        requireInputNotNull(input, "The START: and DUE: of an event cannot be empty!");
        assertIsEventCommand(input);
    
        String remainder = extractAndValidateEventRemainder(input);
    
        EventParts parts = parseEventParts(remainder);
    
        LocalDateTime start = parseDateTime(parts.startRaw);
        LocalDateTime end = parseDateTime(parts.endRaw);
    
        return new Event(parts.description, start, end);
    }
    
    /**
     * Extracts the description keyword from a {@code find} command.
     *
     * @param input The full user input starting with {@code find}.
     * @return The trimmed keyword used for matching task descriptions.
     * @throws SealriouslyException If the find command does not contain a keyword.
     */
    public String parseDescription(String input) throws SealriouslyException {
        requireInputNotNull(input, "The description of a find cannot be empty!");
        assert input != null : "Parser.parseDescription: input should not be null";
        assert input.trim().toLowerCase().startsWith("find") : "parseDescription called with non-find input";

        String keyword = extractRemainder(input);

        if (keyword.isEmpty()) {
            throw new SealriouslyException("The description of a find cannot be empty!");
        }

        return keyword;
    }

    private static String extractRemainder(String input) {
        String[] parts = input.trim().split("\\s+", SPLIT_LIMIT_TWO_PARTS);
        return (parts.length < 2) ? "" : parts[1].trim();
    }
    

    private static int indexOfToken(String text, String token) {
        return text.indexOf(token);
    }

    private static String[] splitByToken(String text, String token) throws SealriouslyException {
        // Allow flexible whitespace around the token and after it.
        // e.g. "abc DUE: 2026-02-20 1800"
        String regex = "\\s*" + token + "\\s*";
        String[] parts = text.split(regex, SPLIT_LIMIT_TWO_PARTS);

        if (parts.length < 2) {
            throw new SealriouslyException("Missing " + token + " in: " + text);
        }
        return parts;
    }

    private static LocalDateTime parseDateTime(String raw) throws SealriouslyException {
        try {
            return LocalDateTime.parse(raw, FORMAT);
        } catch (DateTimeParseException e) {
            throw new SealriouslyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g., 2019-12-02 1800).");
        }
    }

    private static void validateEventTokens(String remainder, int startPos, int duePos) throws SealriouslyException {
        if (startPos < 0 && duePos < 0) {
            throw new SealriouslyException("The START: and DUE: of an event cannot be empty!");
        }
        if (startPos < 0) {
            throw new SealriouslyException("The START: of an event cannot be empty!");
        }
        if (duePos < 0) {
            throw new SealriouslyException("The DUE: of an event cannot be empty!");
        }
        if (startPos > duePos) {
            throw new SealriouslyException("START: must appear before DUE: in an event.");
        }
    }

    private static void assertIsEventCommand(String input) {
        assert input != null : "Parser.parseEvent: input should not be null";
        assert input.trim().toLowerCase().startsWith("event") : "parseEvent called with non-event input";
    }

    private String extractAndValidateEventRemainder(String input) throws SealriouslyException {
        String remainder = extractRemainder(input);
        if (remainder.isEmpty()) {
            throw new SealriouslyException("The description, START:  and DUE:  of an event cannot be empty!");
        }
        return remainder;
    }
    
    private static class EventParts {
        private final String description;
        private final String startRaw;
        private final String endRaw;
    
        private EventParts(String description, String startRaw, String endRaw) {
            this.description = description;
            this.startRaw = startRaw;
            this.endRaw = endRaw;
        }
    }
    
    private EventParts parseEventParts(String remainder) throws SealriouslyException {
        int startPos = indexOfToken(remainder, TOKEN_START);
        int duePos = indexOfToken(remainder, TOKEN_DUE);
        validateEventTokens(remainder, startPos, duePos);
    
        String[] descAndStart = splitByToken(remainder, TOKEN_START);
        String description = requireNonEmpty(descAndStart[0], "The description of an event cannot be empty!");
        String afterStart = descAndStart[1].trim();
    
        String[] startAndEnd = splitByToken(afterStart, TOKEN_DUE);
        String startRaw = requireNonEmpty(startAndEnd[0], "The START: of an event cannot be empty!");
        String endRaw = requireNonEmpty(startAndEnd[1], "The DUE: of an event cannot be empty!");
    
        return new EventParts(description, startRaw, endRaw);
    }
    
    private static String requireNonEmpty(String text, String errorMessage) throws SealriouslyException {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new SealriouslyException(errorMessage);
        }
        return trimmed;
    }

    private static void requireInputNotNull(String input, String message) throws SealriouslyException {
        if (input == null) {
            throw new SealriouslyException(message);
        }
    }
}
