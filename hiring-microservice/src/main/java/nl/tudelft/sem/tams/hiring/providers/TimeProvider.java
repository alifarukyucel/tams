package nl.tudelft.sem.tams.hiring.providers;

import java.time.LocalDateTime;

/**
 * An abstract time provider to make services testable.
 * This interface can be mocked in order to provide a predetermined current time and
 * make tests independent of the actual current time.
 */
public interface TimeProvider {
    /**
     * Retrieves the current time.
     *
     * @return The current time
     */
    LocalDateTime getCurrentLocalDateTime();
}
