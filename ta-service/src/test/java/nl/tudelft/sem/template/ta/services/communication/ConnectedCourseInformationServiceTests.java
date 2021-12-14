package nl.tudelft.sem.template.ta.services.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test", "mockMicroserviceCommunicationHelper"})
@TestPropertySource(properties = {"microservice.course.base_url=" + ConnectedCourseInformationServiceTests.testUrl})
public class ConnectedCourseInformationServiceTests {
    static final String testUrl = "testUrl";

    public static final String isResponsibleLecturerPath = "/lecturer/{netId}/{courseId}";

    @Autowired
    private transient ConnectedCourseInformationService connectedCourseInformationService;

    @Autowired
    private transient MicroserviceCommunicationHelper mockMicroserviceCommunicationHelper;

    @BeforeEach
    public void resetMock() {
        reset(mockMicroserviceCommunicationHelper);
    }

    @Test
    public void isResponsibleLecturer_withTrueResponse_returnsTrue() throws Exception {
        // Arrange
        String netId = "martin";
        String courseId = "CSE1110";

        when(mockMicroserviceCommunicationHelper.get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId))
                .thenReturn(ResponseEntity.ok(true));

        // Act
        boolean actual = connectedCourseInformationService.isResponsibleLecturer(netId, courseId);

        // Assert
        assertThat(actual).isTrue();
        verify(mockMicroserviceCommunicationHelper).get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId);
    }

    @Test
    public void isResponsibleLecturer_withFalseResponse_returnsFalse() throws Exception {
        // Arrange
        String netId = "martin";
        String courseId = "CSE1110";

        when(mockMicroserviceCommunicationHelper.get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId))
                .thenReturn(ResponseEntity.ok(false));

        // Act
        boolean actual = connectedCourseInformationService.isResponsibleLecturer(netId, courseId);

        // Assert
        assertThat(actual).isFalse();
        verify(mockMicroserviceCommunicationHelper).get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId);
    }

    @Test
    public void isResponsibleLecturer_withException_returnsFalse() throws Exception {
        // Arrange
        String netId = "martin";
        String courseId = "CSE1110";

        when(mockMicroserviceCommunicationHelper.get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId))
                .thenThrow(new Exception());

        // Act
        boolean actual = connectedCourseInformationService.isResponsibleLecturer(netId, courseId);

        // Assert
        assertThat(actual).isFalse();
        verify(mockMicroserviceCommunicationHelper).get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId);
    }

    @Test
    public void isResponsibleLecturer_withNullNetid_returnsFalse() throws Exception {
        // Arrange
        String netId = null;
        String courseId = "CSE1110";

        when(mockMicroserviceCommunicationHelper.get(testUrl + isResponsibleLecturerPath,
                Boolean.class, netId, courseId))
                .thenReturn(ResponseEntity.ok(true));

        // Act
        boolean actual = connectedCourseInformationService.isResponsibleLecturer(netId, courseId);

        // Assert
        assertThat(actual).isFalse();
        verify(mockMicroserviceCommunicationHelper, times(0)).get(any(), any(), any());
    }

    @Test
    public void isResponsibleLecturer_withNullCourseId_returnsFalse() throws Exception {
        // Arrange
        String netId = "martin";
        String courseId = null;

        // Act
        boolean actual = connectedCourseInformationService.isResponsibleLecturer(netId, courseId);

        // Assert
        assertThat(actual).isFalse();
        verify(mockMicroserviceCommunicationHelper, times(0)).get(any(), any(), any());
    }

}
