package nl.tudelft.sem.template.authentication.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A configuration profile to allow injection of a mock PasswordEncoder.
 */
@Profile("mockPasswordEncoder")
@Configuration
public class MockPasswordEncoderProfile {

    /**
     * Mocks the PasswordEncoder.
     *
     * @return A mocked PasswordEncoder.
     */
    @Bean
    @Primary
    public PasswordEncoder getMockPasswordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }
}
