package nl.tudelft.sem.tams.hiring.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * A configuration profile to allow injection of a mock RestTemplate.
 */
@Profile("mockRestTemplate")
@Configuration
public class MockRestTemplate {

    /**
     * Mocks the RestTemplate.
     *
     * @return A mocked RestTemplate.
     */
    @Bean
    @Primary
    public RestTemplate getRestTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}
