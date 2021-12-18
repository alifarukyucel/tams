package nl.tudelft.sem.template.hiring.profiles;

import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock ApplicationService.
 */
@Profile("mockApplicationService")
@Configuration
public class MockApplicationServiceProfile {

    /**
     * Mocks the ApplicationService.
     *
     * @return A mocked TokenGenerator.
     */
    @Bean
    @Primary
    public ApplicationService getMockApplicationService()  {
        return Mockito.mock(ApplicationService.class);
    }
}
