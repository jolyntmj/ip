package duke.exception;
/**
 * Represents a user facing exception thrown by Duke when an error occurs.
 */
public class DukeException extends Exception {
    /**
     * Constructs a {@code DukeException} with the given message.
     *
     * @param message Error message shown to user.
     */
    public DukeException(String message) {
        super(message);
    }
}
