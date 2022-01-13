package nl.tudelft.sem.tams.hiring.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.tams.hiring.providers.TimeProvider;
import nl.tudelft.sem.tams.hiring.repositories.TeachingAssistantApplicationRepository;
import nl.tudelft.sem.tams.hiring.services.communication.models.CourseInformationResponseModel;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles({"test", "mockCourseInformation", "mockContractInformation", "mockTimeProvider"})
public class HiringServiceTest {
    //This is the assumed current time for testing.
    //Because LocalDateTime.now() can't be used to test properly, we use this time as the current time
    private static final transient LocalDateTime assumedCurrentTime = LocalDateTime.of(2022, 1, 1, 0, 0);

    @Autowired
    private transient TimeProvider timeProvider;

    @Autowired
    private transient TeachingAssistantApplicationRepository taApplicationRepository;

    @Autowired
    private transient HiringService taApplicationService;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    @Autowired
    private transient ContractInformation mockContractInformation;

    /**
     * Setup mocking before tests run.
     */
    @BeforeEach
    public void setup() {
        when(timeProvider.getCurrentLocalDateTime()).thenReturn(assumedCurrentTime);
    }

    @Test
    public void gradeBelowOneCheck() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidGradeTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 0.9f,
                motivation, ApplicationStatus.PENDING);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));
        // Act

        ThrowingCallable c = () ->  taApplicationService.checkAndSave(invalidGradeTeachingAssistantApplication);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void gradeOnOne() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication validUnsufficientGradeTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 1.0f,
                motivation, ApplicationStatus.PENDING);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));
        // Act

        ThrowingCallable c = () ->  taApplicationService.checkAndSave(validUnsufficientGradeTeachingAssistantApplication);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void gradeAboveTenCheck() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidGradeTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 10.1f,
                motivation, ApplicationStatus.PENDING);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));
        // Act

        ThrowingCallable c = () ->  taApplicationService.checkAndSave(invalidGradeTeachingAssistantApplication);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void gradeOnTen() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication validTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 10.0f,
                motivation, ApplicationStatus.PENDING);
        assertThat(validTeachingAssistantApplication.meetsRequirements()).isTrue();

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        taApplicationService.checkAndSave(validTeachingAssistantApplication);

        //Assert
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isNotEmpty();
    }

    @Test
    public void validCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication validTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
        assertThat(validTeachingAssistantApplication.meetsRequirements()).isTrue();

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        taApplicationService.checkAndSave(validTeachingAssistantApplication);

        //Assert
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1200", "johndoe")))
                .isNotEmpty();
    }

    @Test
    public void nonExistingCourseCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 6.0f,
                motivation, ApplicationStatus.PENDING);

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(null);

        //Act
        ThrowingCallable c = () -> taApplicationService.checkAndSave(invalidTeachingAssistantApplication);

        //Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void invalidGradeCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 5.9f,
                motivation, ApplicationStatus.PENDING);
        assertThat(invalidTeachingAssistantApplication.meetsRequirements()).isFalse();

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ThrowingCallable c = () -> taApplicationService.checkAndSave(invalidTeachingAssistantApplication);

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    /**
     * Boundary test off point for date checking.
     */
    @Test
    public void invalidDateCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", (float) 6.9,
                motivation, ApplicationStatus.PENDING);

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                assumedCurrentTime.plusWeeks(3),
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ThrowingCallable c = () -> taApplicationService.checkAndSave(invalidTeachingAssistantApplication);

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    /**
     * Boundary test on point for date checking.
     */
    @Test
    public void validDateCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidApplication = new TeachingAssistantApplication("CSE1300", "jsmith", (float) 6.9,
            motivation, ApplicationStatus.PENDING);

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
            "CSE1300",
            assumedCurrentTime.plusWeeks(3).plusDays(1),
            "CourseName",
            "CourseDescription",
            100,
            new ArrayList<>()));

        //Act
        taApplicationService.checkAndSave(invalidApplication);

        //Assert
        assertThat(taApplicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
            .isPresent();
    }

    @Test
    public void getWithInvalidCourseId() {
        // Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        String invalidCourseId = "CSE1305";
        taApplicationRepository.save(expected);

        // Act
        ThrowingCallable c = () -> taApplicationService.get(invalidCourseId, expected.getNetId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void getStatusWithInvalidNetid() {
        // Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        String invalidNetid = "sjmith";
        taApplicationRepository.save(expected);

        // Act
        ThrowingCallable c = () -> taApplicationService.get(expected.getCourseId(), invalidNetid);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void retrieveStatusRejectedApplication() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.REJECTED);

        taApplicationRepository.save(expected);

        //Act
        var result = taApplicationService.retrieveStatus(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(ApplicationStatus.class);
        assertThat(result).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void retrieveStatusAcceptedApplication() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.ACCEPTED);

        taApplicationRepository.save(expected);

        //Act
        var result = taApplicationService.retrieveStatus(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(ApplicationStatus.class);
        assertThat(result).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    /**
     * Boundary test.
     * Off-point for reaching maximum amount of applications
     * 2 pending applications
     */
    @Test
    public void getApplicationsAndTwoApplicationsTest() {
        //Arrange
        String motivation = "I am motivated";
        TeachingAssistantApplication firstApplication = new TeachingAssistantApplication("CSE1200", "johndoe", 7.0f,
                motivation, ApplicationStatus.ACCEPTED);
        TeachingAssistantApplication secondApplication = new TeachingAssistantApplication("CSE1300", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);
        TeachingAssistantApplication thirdApplication = new TeachingAssistantApplication("CSE1400", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);

        //Act
        taApplicationRepository.save(firstApplication);
        taApplicationRepository.save(secondApplication);
        taApplicationRepository.save(thirdApplication);

        //Assert
        assertThat(taApplicationService.getApplicationFromStudent("johndoe")).size().isEqualTo(3);
        assertThat(taApplicationService.hasReachedMaxApplication("johndoe")).isFalse();

    }

    /**
     * Boundary test.
     * On-point for reaching maximum amount of applications
     * 3 pending applications
     */
    @Test
    public void getApplicationsAndExactlyThreeApplicationsTest() {
        //Arrange
        String motivation = "I am motivated";
        TeachingAssistantApplication firstTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1200", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);
        TeachingAssistantApplication secondTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);
        TeachingAssistantApplication thirdTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1400", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);

        //Act
        taApplicationRepository.save(firstTeachingAssistantApplication);
        taApplicationRepository.save(secondTeachingAssistantApplication);
        taApplicationRepository.save(thirdTeachingAssistantApplication);

        //Assert
        assertThat(taApplicationService.getApplicationFromStudent("johndoe")).size().isEqualTo(3);
        assertThat(taApplicationService.hasReachedMaxApplication("johndoe")).isTrue();
    }

    /**
     * Boundary test.
     * Reached Max Applications off point
     */
    @Test
    public void maxApplicationsTestOffPoint() {
        //Arrange
        String motivation = "I am motivated";
        TeachingAssistantApplication firstApplication = new TeachingAssistantApplication("CSE1200", "johndoe", 7.0f,
            motivation, ApplicationStatus.PENDING);
        TeachingAssistantApplication secondApplication = new TeachingAssistantApplication("CSE1300", "johndoe", 7.0f,
            motivation, ApplicationStatus.PENDING);

        //Act
        taApplicationRepository.save(firstApplication);
        taApplicationRepository.save(secondApplication);

        //Assert
        assertThat(taApplicationService.getApplicationFromStudent("johndoe")).size().isEqualTo(2);
        assertThat(taApplicationService.hasReachedMaxApplication("johndoe")).isFalse();
    }

    @Test
    public void retrieveStatusPendingApplication() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.PENDING);

        taApplicationRepository.save(expected);

        //Act
        var result = taApplicationService.retrieveStatus(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(ApplicationStatus.class);
        assertThat(result).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    public void retrieveStatusEmptyApplication() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I want to become a TA", ApplicationStatus.PENDING);
        String notCourse = "Not a courseId";
        taApplicationRepository.save(expected);

        //Act
        ThrowingCallable c = () -> taApplicationService.retrieveStatus(notCourse, expected.getNetId());

        //Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void retrieveInvalidStatusTest() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.PENDING);
        String noApplicationCourseId = "CSE1200";
        taApplicationRepository.save(expected);

        //Act
        ThrowingCallable c = () -> taApplicationService.get(noApplicationCourseId, expected.getNetId());

        //Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void retrievePendingStatusTest() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.PENDING);

        taApplicationRepository.save(expected);

        //Act
        var result = taApplicationService.get(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(TeachingAssistantApplication.class);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.PENDING);

    }

    @Test
    public void retrieveAcceptedStatusTest() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.ACCEPTED);

        taApplicationRepository.save(expected);

        //Act
        var result = taApplicationService.get(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(TeachingAssistantApplication.class);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    public void retrieveRejectedStatusTest() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.REJECTED);

        taApplicationRepository.save(expected);

        //Act
        var result = taApplicationService.get(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(TeachingAssistantApplication.class);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }



    @Test
    public void checkAndWithdrawOnTimeTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById(teachingAssistantApplication.getCourseId())).thenReturn(
                new CourseInformationResponseModel(
                        "CSE1200",
                        LocalDateTime.MAX,
                        "CourseName",
                        "CourseDescription",
                        100,
                        new ArrayList<>())
        );

        //Act
        boolean result = taApplicationService.checkAndWithdraw(
                teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        //Assert
        assertThat(result).isTrue();
    }

    @Test
    public void checkAndWithdrawTooLateTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        //When the startTime is the current time, the deadline has passed already
        when(mockCourseInformation.getCourseById(teachingAssistantApplication.getCourseId()))
                .thenReturn(new CourseInformationResponseModel(
                        "CSE1200",
                        assumedCurrentTime,
                        "CourseName",
                        "CourseDescription",
                        100,
                        new ArrayList<>())
            );

        //Act
        boolean result = taApplicationService.checkAndWithdraw(
                teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        //Assert
        assertThat(result).isFalse();
    }

    /**
     * Boundary test withdrawing off point.
     */
    @Test
    public void checkAndWithdrawJustTooLateTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);
        when(mockCourseInformation.getCourseById(teachingAssistantApplication.getCourseId())).thenReturn(
                new CourseInformationResponseModel(
                        "CSE1200",
                        assumedCurrentTime.plusWeeks(3),
                        "CourseName",
                        "CourseDescription",
                        100,
                        new ArrayList<>())
        );

        //Act
        boolean result = taApplicationService.checkAndWithdraw(
                teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        //Assert
        assertThat(result).isFalse();
    }

    /**
     * Boundary test withdrawing on point.
     */
    @Test
    public void checkAndWithdrawJustOnTimeTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);
        when(mockCourseInformation.getCourseById(teachingAssistantApplication.getCourseId())).thenReturn(
                new CourseInformationResponseModel(
                        "CSE1200",
                        assumedCurrentTime.plusWeeks(3).plusNanos(1),
                        "CourseName",
                        "CourseDescription",
                        100,
                        new ArrayList<>())
        );

        //Act
        boolean result = taApplicationService.checkAndWithdraw(
                teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        //Assert
        assertThat(result).isTrue();
    }

    @Test
    public void getExisting() {
        // Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        taApplicationRepository.save(expected);

        // Act
        TeachingAssistantApplication actual = taApplicationService.get(expected.getCourseId(), expected.getNetId());

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void rejectValidApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        // Act
        taApplicationService.reject(teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        // Assert
        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void rejectNonexistentApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> taApplicationService.reject("incorrect", teachingAssistantApplication.getNetId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(c);

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    /**
     * Test for rejecting an application in a non-pending state.
     *
     * @param status the test status (non-pending)
     */
    @ParameterizedTest
    @CsvSource({"ACCEPTED", "REJECTED"})
    public void rejectNonPendingApplication(String status) {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        taApplicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> taApplicationService.reject(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId());

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
    }

    @Test
    public void extendEmptyListWithRatingTest() {
        //Arrange
        List<TeachingAssistantApplication> emptyList = new ArrayList<>();
        List<PendingTeachingAssistantApplicationResponseModel> expectedResList = new ArrayList<>();

        //Act
        List<PendingTeachingAssistantApplicationResponseModel> resList = taApplicationService.extendWithRating(emptyList);

        //Assert
        assertThat(resList).isEqualTo(expectedResList);
    }

    @Test
    public void extendWithRatingTest() {
        //Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication teachingAssistantApplication2 = new TeachingAssistantApplication(
                "CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);

        String[] netIds = new String[]{"jsmith", "wsmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("jsmith", 8.0d);
                put("wsmith", 9.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        var resultList = taApplicationService.extendWithRating(
                List.of(teachingAssistantApplication, teachingAssistantApplication2));

        var resultModel = new PendingTeachingAssistantApplicationResponseModel("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", 8.0d);
        var resultModel2 = new PendingTeachingAssistantApplicationResponseModel("CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", 9.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedList = List.of(resultModel, resultModel2);

        assertThat(resultList).isEqualTo(expectedList);

    }

    @Test
    public void acceptValidApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be cool!", ApplicationStatus.PENDING, "test@email.com");
        taApplicationRepository.save(teachingAssistantApplication);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        taApplicationService.accept(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId(), expectedDuties, expectedMaxHours);

        // Assert
        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(
                        teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(teachingAssistantApplication.getCourseId())
                        && contract.getNetId().equals(teachingAssistantApplication.getNetId())
                        && contract.getDuties().equals(expectedDuties)
                        && contract.getMaxHours() == expectedMaxHours
                        && contract.getTaContactEmail().equals(teachingAssistantApplication.getContactEmail())
        ));
    }

    @Test
    public void acceptValidApplicationWithoutContactEmail() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        taApplicationService.accept(teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId(),
                expectedDuties, expectedMaxHours);

        // Assert
        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(teachingAssistantApplication.getCourseId())
                        && contract.getNetId().equals(teachingAssistantApplication.getNetId())
                        && contract.getDuties().equals(expectedDuties)
                        && contract.getMaxHours() == expectedMaxHours
                        && contract.getTaContactEmail() == null
        ));
    }

    @Test
    public void acceptNonexistentApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> taApplicationService.accept("incorrect", teachingAssistantApplication.getNetId(),
                "be a good TA", 45);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(c);

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation, times(0)).createContract(any());
    }

    /**
     * Test for accepting an application in a non-pending state.
     *
     * @param status the test status (non-pending)
     */
    @ParameterizedTest
    @CsvSource({"ACCEPTED", "REJECTED"})
    public void acceptNonPendingApplication(String status) {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        taApplicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> taApplicationService.accept(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId(),
                "be a good TA", 45);

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
        verify(mockContractInformation, times(0)).createContract(any());
    }

    @Test
    public void acceptValidApplicationButCreatingContractThrowsException() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(teachingAssistantApplication);

        when(mockContractInformation.createContract(any())).thenReturn(false);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        ThrowingCallable c = () -> taApplicationService.accept(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId(),
                expectedDuties, expectedMaxHours);

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(teachingAssistantApplication.getCourseId())
                        && contract.getNetId().equals(teachingAssistantApplication.getNetId())
                        && contract.getDuties().equals(expectedDuties)
                        && contract.getMaxHours() == expectedMaxHours
        ));
    }
}
