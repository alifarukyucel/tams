package nl.tudelft.sem.template.course.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Conflict exception.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    public static final long serialVersionUID = 4328743;

    /**
     * Instantiates a new Conflict exception.
     */
    public ConflictException() {
    }

    /**
     * Instantiates a new Conflict exception.
     *
     * @param message The message.
     */
    public ConflictException(String message) {
        super(message);
    }
}
