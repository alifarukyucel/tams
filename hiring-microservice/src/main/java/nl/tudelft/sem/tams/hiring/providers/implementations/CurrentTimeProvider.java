
package nl.tudelft.sem.tams.hiring.providers.implementations;

import java.time.LocalDateTime;
import nl.tudelft.sem.tams.hiring.providers.TimeProvider;
import org.springframework.stereotype.Component;

/**
 * An abstract time provider to make services testable.
 * The TimeProvider interface can be mocked in order to provide a predetermined current time and
 * make tests independent of the actual current time.
 */
@Component
public class CurrentTimeProvider implements TimeProvider {
    /**
     * Gets current time as a localDateTime.
     *
     * @return The current time
     */
    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
}