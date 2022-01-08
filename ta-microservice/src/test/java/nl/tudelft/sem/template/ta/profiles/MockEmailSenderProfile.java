package nl.tudelft.sem.template.ta.profiles;

import nl.tudelft.sem.template.ta.interfaces.EmailSender;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock EmailSender.
 */
@Profile("mockEmailSender")
@Configuration
public class MockEmailSenderProfile {

    /**
     * Mocks the EmailSender.
     *
     * @return A mocked EmailSender.
     */
    @Bean
    @Primary
    public EmailSender getMockEmailSender() {
        return Mockito.mock(EmailSender.class);
    }
}
