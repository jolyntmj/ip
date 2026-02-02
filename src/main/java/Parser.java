import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    

        String[] parts = remainder.split("/by", 2);

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

    public Event parseEvent(String input) throws DukeException {
        String remainder = input.substring(5).trim();

        if (remainder.isEmpty()) {
            throw new DukeException("The description, /from and /to of an event cannot be empty!");
        }

        if (remainder.startsWith("/from")) {
            if (!remainder.contains("/to")) {
                throw new DukeException("The description and /to of an event cannot be empty!");
            }
        }

        if (remainder.startsWith("/to")) {
            throw new DukeException("The description and /from of an event cannot be empty!");
        }

        String[] parts = remainder.split("/from", 2);

        if (parts.length < 2) {
            if (remainder.contains("/to")) {
                throw new DukeException("The /from of an event cannot be empty!");
            } else {
                throw new DukeException("The /from and /to of an event cannot be empty!");
            }
        }

        String description = parts[0].trim();
        String firstSplit = parts[1].trim();

        if (description.isEmpty()) {
            throw new DukeException("The description of an event cannot be empty!");
        }

        String[] secondParting = firstSplit.split("/to", 2);

        if (secondParting.length < 2) {
            throw new DukeException("The /to of an event cannot be empty!");
        }

        String start = secondParting[0].trim();
        String end = secondParting[1].trim();

        if (start.isEmpty()) {
            throw new DukeException("The /from of an event cannot be empty!");
        }
    
        if (end.isEmpty()) {
            throw new DukeException("The /to of an event cannot be empty!");
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
}
