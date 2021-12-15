package nl.tudelft.sem.template.hiring.profiles;

import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock TokenGenerator.
 */
@Profile("mockCourseInformation")
@Configuration
public class MockCourseInformationProfile {

    /**
     * Mocks the TokenGenerator.
     *
     * @return A mocked TokenGenerator.
     */
    @Bean
    @Primary
    public CourseInformation getMockCourseInformation() {
        return Mockito.mock(CourseInformation.class);
    }
}
