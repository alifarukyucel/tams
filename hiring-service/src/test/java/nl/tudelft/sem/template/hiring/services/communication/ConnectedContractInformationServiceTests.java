package nl.tudelft.sem.template.hiring.services.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles({"test", "mockMicroserviceCommunicationHelper"})
@TestPropertySource(properties = {"microservice.ta.base_url=" + ConnectedContractInformationServiceTests.testUrl})
public class ConnectedContractInformationServiceTests {
    static final String testUrl = "testUrl";

    public static final String createContractPath = "/contracts/create";

    @Autowired
    private transient ConnectedContractInformationService connectedContractInformationService;

    @Autowired
    private transient MicroserviceCommunicationHelper mockMicroserviceCommunicationHelper;

    @BeforeEach
    public void resetMock() {
        reset(mockMicroserviceCommunicationHelper);
    }

    @Test
    public void createContract_withNoException_returnsTrue() throws Exception {
        // Arrange
        String netId = "martin";
        String courseId = "CSE1110";

        CreateContractRequestModel model = CreateContractRequestModel.builder()
                .withCourseId(courseId)
                .withNetId(netId)
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .withTaContactEmail("martin@tudelft.nl")
                .build();

        when(mockMicroserviceCommunicationHelper.post(testUrl + createContractPath,
                null, model))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        boolean actual = connectedContractInformationService.createContract(model);

        // Assert
        assertThat(actual).isTrue();
        verify(mockMicroserviceCommunicationHelper).post(testUrl + createContractPath,
                null, model);
    }

    @Test
    public void createContract_withException_returnsFalse() throws Exception {
        // Arrange
        String netId = "martin";
        String courseId = "CSE1110";

        CreateContractRequestModel model = CreateContractRequestModel.builder()
                .withCourseId(courseId)
                .withNetId(netId)
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .withTaContactEmail("martin@tudelft.nl")
                .build();

        when(mockMicroserviceCommunicationHelper.post(testUrl + createContractPath,
                null, model))
                .thenThrow(Exception.class);

        // Act
        boolean actual = connectedContractInformationService.createContract(model);

        // Assert
        assertThat(actual).isFalse();
        verify(mockMicroserviceCommunicationHelper).post(testUrl + createContractPath,
                null, model);
    }
}
