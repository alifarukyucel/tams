package nl.tudelft.sem.template.hiring.profiles;

import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock ContractInformation.
 */
@Profile("mockContractInformation")
@Configuration
public class MockContractInformationProfile {

    /**
     * Mocks the TokenGenerator.
     *
     * @return A mocked TokenGenerator.
     */
    @Bean
    @Primary
    public ContractInformation getMockContractInformation() {
        return Mockito.mock(ContractInformation.class);
    }
}
