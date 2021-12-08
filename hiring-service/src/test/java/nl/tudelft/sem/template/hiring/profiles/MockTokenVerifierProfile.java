package nl.tudelft.sem.template.hiring.profiles;

import nl.tudelft.sem.template.hiring.security.TokenVerifier;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock TokenGenerator.
 */
@Profile("mockTokenVerifier")
@Configuration
public class MockTokenVerifierProfile {

    /**
     * Mocks the TokenGenerator.
     *
     * @return A mocked TokenGenerator.
     */
    @Bean
    @Primary
    public TokenVerifier getMockTokenGenerator() {
        return Mockito.mock(TokenVerifier.class);
    }
}

