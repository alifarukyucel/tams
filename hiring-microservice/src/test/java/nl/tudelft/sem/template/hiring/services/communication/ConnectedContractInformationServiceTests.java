package nl.tudelft.sem.template.hiring.services.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String createContractPath = "/contracts/create";

    private static final String getTaRatingsPath = "/contracts/ratings";

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

    @Test
    public void getTaRatings_withNoException() throws Exception {
        //Arrange
        List<String> netIds = List.of("asmith", "bsmith");
        String extendedUrl = testUrl +  getTaRatingsPath + "?netIds=" + netIds.get(0) + "," + netIds.get(1);

        Map<String, Double> expectedMap = new HashMap<>() {{
                put("asmith", 9.0d);
                put("bsmith", -1.0d);
            }
        };

        when(mockMicroserviceCommunicationHelper.get(extendedUrl, Map.class)).thenReturn(ResponseEntity.ok(expectedMap));

        //Act
        Map<String, Double> actual = connectedContractInformationService.getTaRatings(netIds);

        //Assert
        assertThat(actual).isEqualTo(expectedMap);
        verify(mockMicroserviceCommunicationHelper).get(extendedUrl, Map.class);
    }

    @Test
    public void getTaRatings_withException() throws Exception {
        //Arrange
        List<String> netIds = List.of("asmith", "bsmith");
        String extendedUrl = testUrl +  getTaRatingsPath + "?netIds=" + netIds.get(0) + "," + netIds.get(1);

        when(mockMicroserviceCommunicationHelper.get(extendedUrl, Map.class)).thenThrow(Exception.class);

        //Act
        Map<String, Double> actual = connectedContractInformationService.getTaRatings(netIds);

        //Assert
        assertThat(actual).isEqualTo(new HashMap<>());
        verify(mockMicroserviceCommunicationHelper).get(extendedUrl, Map.class);
    }
}
