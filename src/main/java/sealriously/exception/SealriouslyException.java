package sealriously.exception;

/**
 * Represents a user facing exception thrown by Sealriously when an error occurs.
 */
public class SealriouslyException extends Exception {
    /**
     * Constructs a {@code SealriouslyException} with the given message.
     *
     * @param message Error message shown to user.
     */
    public SealriouslyException(String message) {
        super(message);
    }
}
