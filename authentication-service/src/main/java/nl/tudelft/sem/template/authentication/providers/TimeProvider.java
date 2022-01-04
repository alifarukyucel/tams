package nl.tudelft.sem.template.authentication.providers;

import java.time.Instant;

/**
 * An abstract time provider to make services testable.
 */
public interface TimeProvider {
    /**
     * Retrieves the current time.
     *
     * @return The current time
     */
    Instant getCurrentTime();
}
