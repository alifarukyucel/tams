package nl.tudelft.sem.tams.hiring.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import javax.servlet.http.HttpServletRequest;

/**
 * A configuration profile to allow injection of a mock HttpServletRequest.
 */
@Profile("mockHttpServletRequest")
@Configuration
public class MockHttpServletRequest {

    /**
     * Mocks the HttpServletRequest.
     *
     * @return A mocked HttpServletRequest.
     */
    @Bean
    @Primary

    public HttpServletRequest getHttpServletRequest() {
        return Mockito.mock(HttpServletRequest.class);
    }
}
