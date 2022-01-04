package nl.tudelft.sem.template.hiring.services.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles({"test", "mockMicroserviceCommunicationHelper"})
@TestPropertySource(properties = {"microservice.course.base_url=" + ConnectedCourseInformationServiceTests.testUrl})
public class ConnectedCourseInformationServiceTests {
    static final String testUrl = "testUrl";

    public static final String isResponsibleLecturerPath = "/{courseId}/lecturer/{netId}";
    public static final String getCourseByIdPath = "/{id}";

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
                Boolean.class, courseId, netId))
                .thenReturn(ResponseEntity.ok(true));

        // Act
        boolean actual = connectedCourseInformationService.isResponsibleLecturer(netId, courseId);

        // Assert
        assertThat(actual).isTrue();
        verify(mockMicroserviceCommunicationHelper).get(testUrl + isResponsibleLecturerPath,
                Boolean.class, courseId, netId);
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
                Boolean.class, courseId, netId);
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
                Boolean.class, courseId, netId);
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

    @Test
    public void testStartDateReturnsCorrectDate() throws Exception {
        // Arrange
        String courseId = "CSE1110";

        var date = LocalDateTime.now().plusDays(23);  // random amount to make sure method doesn't just return now
        CourseInformationResponseModel expected = new CourseInformationResponseModel();
        expected.setId(courseId);
        expected.setStartDate(date);

        when(mockMicroserviceCommunicationHelper.get(testUrl + getCourseByIdPath,
            CourseInformationResponseModel.class, courseId))
            .thenReturn(ResponseEntity.ok(expected));

        // Act
        LocalDateTime actual = connectedCourseInformationService.startDate(courseId);

        // Assert
        assertThat(actual).isSameAs(date);
        verify(mockMicroserviceCommunicationHelper).get(testUrl + getCourseByIdPath,
            CourseInformationResponseModel.class, courseId);
    }


    @Test
    public void testStartDateThrowsWhenNull() throws Exception {
        // Arrange
        String courseId = "CSE1110";

        var date = LocalDateTime.now().plusDays(23);  // random amount to make sure method doesn't just return now
        CourseInformationResponseModel expected = new CourseInformationResponseModel();
        expected.setId(courseId);
        expected.setStartDate(date);

        when(mockMicroserviceCommunicationHelper.get(testUrl + getCourseByIdPath,
            CourseInformationResponseModel.class, courseId))
            .thenReturn(null);

        // Act
        ThrowableAssert.ThrowingCallable action = () ->
            connectedCourseInformationService.startDate(courseId);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
        verify(mockMicroserviceCommunicationHelper).get(testUrl + getCourseByIdPath,
            CourseInformationResponseModel.class, courseId);
    }

    @Test
    public void getCourseById_withValidCourse_returnsCorrectCourse() throws Exception {
        // Arrange
        String courseId = "CSE1110";

        CourseInformationResponseModel expected = new CourseInformationResponseModel();
        expected.setId(courseId);

        when(mockMicroserviceCommunicationHelper.get(testUrl + getCourseByIdPath,
                CourseInformationResponseModel.class, courseId))
                .thenReturn(ResponseEntity.ok(expected));

        // Act
        CourseInformationResponseModel actual = connectedCourseInformationService.getCourseById(courseId);

        // Assert
        assertThat(actual).isSameAs(expected);
        verify(mockMicroserviceCommunicationHelper).get(testUrl + getCourseByIdPath,
                CourseInformationResponseModel.class, courseId);
    }

    @Test
    public void getCourseById_withException_returnsNull() throws Exception {
        // Arrange
        String courseId = "CSE1110";

        when(mockMicroserviceCommunicationHelper.get(testUrl + getCourseByIdPath,
                CourseInformationResponseModel.class, courseId))
                .thenThrow(new Exception());

        // Act
        CourseInformationResponseModel actual = connectedCourseInformationService.getCourseById(courseId);

        // Assert
        assertThat(actual).isNull();
        verify(mockMicroserviceCommunicationHelper).get(testUrl + getCourseByIdPath,
                CourseInformationResponseModel.class, courseId);
    }

    @Test
    public void getCourseById_withNullId_returnsNull() throws Exception {
        // Arrange
        String courseId = null;

        // Act
        CourseInformationResponseModel actual = connectedCourseInformationService.getCourseById(courseId);

        // Assert
        assertThat(actual).isNull();
        verify(mockMicroserviceCommunicationHelper, times(0)).get(any(), any(), any());
    }
}
