package nl.tudelft.sem.template.authentication.profiles;

import nl.tudelft.sem.template.authentication.security.TokenGenerator;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock TokenGenerator.
 */
@Profile("mockTokenGenerator")
@Configuration
public class MockTokenGeneratorProfile {

    /**
     * Mocks the TokenGenerator.
     *
     * @return A mocked TokenGenerator.
     */
    @Bean
    @Primary
    public TokenGenerator getMockTokenGenerator() {
        return Mockito.mock(TokenGenerator.class);
    }
}
