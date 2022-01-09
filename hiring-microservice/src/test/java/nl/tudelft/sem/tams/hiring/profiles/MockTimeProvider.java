package nl.tudelft.sem.tams.hiring.profiles;

import nl.tudelft.sem.tams.hiring.providers.TimeProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 *  A configuration profile to allow injection of a mock TimeProvider.
 */
@Profile("mockTimeProvider")
@Configuration
public class MockTimeProvider {
    /**
     * Mocks the TimeProvider.
     *
     * @return A mocked TimeProvider.
     */
    @Bean
    @Primary
    public TimeProvider getMockTimeProvider() {
        return Mockito.mock(TimeProvider.class);
    }
}
