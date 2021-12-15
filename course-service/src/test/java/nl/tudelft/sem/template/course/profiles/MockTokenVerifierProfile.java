package nl.tudelft.sem.template.course.profiles;

import nl.tudelft.sem.template.course.security.TokenVerifier;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock TokenVerifier.
 */
@Profile("mockTokenVerifier")
@Configuration
public class MockTokenVerifierProfile {

    /**
     * Mocks the TokenVerifier.
     *
     * @return A mocked TokenVerifier.
     */
    @Bean
    @Primary
    public TokenVerifier getMockTokenVerifier() {
        return Mockito.mock(TokenVerifier.class);
    }
}
