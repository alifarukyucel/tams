package nl.tudelft.sem.template.authentication.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;

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
    public AuthenticationManager getMockAuthenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}
