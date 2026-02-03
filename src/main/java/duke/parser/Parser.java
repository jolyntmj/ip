package duke.parser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import duke.command.CommandType;
import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;

public class Parser {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public Parser() {}

    public CommandType parseCommandType(String input) {
        String firstWord = input.trim().split("\\s+", 2)[0].toLowerCase();
    
        return switch (firstWord) {
        case "list" -> CommandType.LIST;
        case "todo" -> CommandType.TODO;
        case "deadline" -> CommandType.DEADLINE;
        case "event" -> CommandType.EVENT;
        case "mark" -> CommandType.MARK;
        case "delete" -> CommandType.DELETE;
        case "find" ->CommandType.FIND;
        case "bye" -> CommandType.BYE;
        default -> CommandType.UNKNOWN;
        };
    }
    
    public int parseIndex(String input) throws DukeException {
        String[] parts = input.split(" ", 2);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new DukeException("Please specify a task number.");
        }        

        int index;

        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a number.");
        }

        return index;

    }

    public String parseTodo(String input) throws DukeException {
        String[] parts = input.split(" ", 2);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new DukeException("The description of a todo cannot be empty!");
        }

        return parts[1];

    }

    public Deadline parseDeadline(String input) throws DukeException {
        String remainder = input.substring(8).trim();

        if (remainder.isEmpty()) {
            throw new DukeException("The description and /by of a deadline cannot be empty!");
        }
    

        String[] parts = remainder.split("DUE: ", 2);

        if (parts.length < 2) {
            throw new DukeException("The /by of a deadline cannot be empty!");
        }

        String description = parts[0];
        String byRaw = parts[1].trim();

        if (description.isEmpty()) {
            throw new DukeException("The description of a deadline cannot be empty!");
        }

        if (byRaw.isEmpty()) {
            throw new DukeException("The /by of a deadline cannot be empty!");
        }

        LocalDateTime by;
        try {
            by = LocalDateTime.parse(byRaw, FORMAT);
        } catch (Exception e) {
            throw new DukeException("Invalid date format. Use yyy-MM-dd HHmm (eg. 2019-12-02 1800)");
        }

        return new Deadline(description,by);
    }

<<<<<<< Updated upstream
=======
    /**
     * Parses an event command into an {@link Event} object.
     * Expected format: {@code "event <desc> START:  yyyy-MM-dd HHmm DUE:  yyyy-MM-dd HHmm"}.
     *
     * @param input Full user input string.
     * @return Parsed {@link Event}.
     * @throws DukeException If description, "START: ", or "DUE: " is missing, or the datetime format is invalid.
     */
>>>>>>> Stashed changes
    public Event parseEvent(String input) throws DukeException {
        String remainder = input.substring(5).trim();

        if (remainder.isEmpty()) {
            throw new DukeException("The description, START:  and DUE:  of an event cannot be empty!");
        }

        if (remainder.startsWith("START: ")) {
            if (!remainder.contains("DUE: ")) {
                throw new DukeException("The description and DUE:  of an event cannot be empty!");
            }
        }

        if (remainder.startsWith("DUE: ")) {
            throw new DukeException("The description and START:  of an event cannot be empty!");
        }

        String[] parts = remainder.split("START: ", 2);

        if (parts.length < 2) {
            if (remainder.contains("DUE: ")) {
                throw new DukeException("The START of an event cannot be empty!");
            } else {
                throw new DukeException("The START and DUE of an event cannot be empty!");
            }
        }

        String description = parts[0].trim();
        String firstSplit = parts[1].trim();

        if (description.isEmpty()) {
            throw new DukeException("The description of an event cannot be empty!");
        }

        String[] secondParting = firstSplit.split("DUE: ", 2);

        if (secondParting.length < 2) {
            throw new DukeException("The DUE: of an event cannot be empty!");
        }

        String start = secondParting[0].trim();
        String end = secondParting[1].trim();

        if (start.isEmpty()) {
            throw new DukeException("The START: of an event cannot be empty!");
        }
    
        if (end.isEmpty()) {
            throw new DukeException("The DUE: of an event cannot be empty!");
        }
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            startTime = LocalDateTime.parse(start, FORMAT);
            endTime = LocalDateTime.parse(end, FORMAT);
        } catch (Exception e) {
            throw new DukeException("Invalid date format. Use yyy-MM-dd HHmm (eg. 2019-12-02 1800)");
        }

        return new Event(description, startTime, endTime);
    }

    /**
     * Extracts the description keyword from a {@code find} command.
     *
     * @param input The full user input starting with {@code find}.
     * @return The trimmed keyword used for matching task descriptions.
     * @throws DukeException If the find command does not contain a keyword.
     */
    public String parseDescription(String input) throws DukeException {
        String[] parts = input.split("find", 2);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new DukeException("The description of a find cannot be empty!");
        }

        return parts[1].trim();
    }
}
