package nl.tudelft.sem.tams.hiring.profiles;

import nl.tudelft.sem.tams.hiring.services.communication.MicroserviceCommunicationHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock MicroserviceCommunicationHelper.
 */
@Profile("mockMicroserviceCommunicationHelper")
@Configuration
public class MockMicroserviceCommunicationHelperProfile {

    /**
     * Mocks the MicroserviceCommunicationHelper.
     *
     * @return A mocked MicroserviceCommunicationHelper.
     */
    @Bean
    @Primary
    public MicroserviceCommunicationHelper getMockMicroserviceCommunicationHelper() {
        return Mockito.mock(MicroserviceCommunicationHelper.class);
    }
}
