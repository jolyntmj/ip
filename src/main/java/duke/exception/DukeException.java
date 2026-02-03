package duke.exception;
/**
 * Represents a user facing exception thrown by Duke when an error occurs.
 */
public class DukeException extends Exception {
    public DukeException(String message) {
        super(message);
    }
}
