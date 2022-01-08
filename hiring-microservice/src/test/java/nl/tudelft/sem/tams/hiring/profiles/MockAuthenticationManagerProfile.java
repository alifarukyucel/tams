package nl.tudelft.sem.tams.hiring.profiles;

import nl.tudelft.sem.tams.hiring.security.AuthManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock AuthenticationManager.
 */
@Profile("mockAuthenticationManager")
@Configuration
public class MockAuthenticationManagerProfile {

    /**
     * Mocks the AuthenticationManager.
     *
     * @return A mocked AuthenticationManager.
     */
    @Bean
    @Primary
    public AuthManager getMockAuthManager() {
        return Mockito.mock(AuthManager.class);
    }
}

