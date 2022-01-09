package nl.tudelft.sem.tams.hiring.profiles;

import nl.tudelft.sem.tams.hiring.interfaces.ContractInformation;
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
     * Mocks the ContractInformation.
     *
     * @return A mocked ContractInformation.
     */
    @Bean
    @Primary
    public ContractInformation getMockContractInformation() {
        return Mockito.mock(ContractInformation.class);
    }
}
