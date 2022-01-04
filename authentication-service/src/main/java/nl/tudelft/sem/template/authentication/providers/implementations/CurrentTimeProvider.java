package nl.tudelft.sem.template.authentication.providers.implementations;

import java.time.Instant;
import nl.tudelft.sem.template.authentication.providers.TimeProvider;
import org.springframework.stereotype.Component;

/**
 * An abstract time provider to make services testable.
 */
@Component
public class CurrentTimeProvider implements TimeProvider {
    /**
     * Gets current time.
     *
     * @return The current time
     */
    public Instant getCurrentTime() {
        return Instant.now();
    }
}
