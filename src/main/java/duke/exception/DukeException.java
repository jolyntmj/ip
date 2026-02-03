package duke.exception;
<<<<<<< HEAD
=======
/**
 * Represents a user facing exception thrown by Duke when an error occurs.
 */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
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
