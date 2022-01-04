package nl.tudelft.sem.template.hiring.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.template.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.PendingApplicationResponseModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
@ActiveProfiles({"test", "mockCourseInformation", "mockContractInformation"})
public class TeachingAssistantApplicationServiceTest {
    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient ApplicationService applicationService;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    @Autowired
    private transient ContractInformation mockContractInformation;

    @Test
    public void gradeBelowOneCheck() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidGradeTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 0.9f,
                motivation, ApplicationStatus.PENDING);
        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));
        // Act

        ThrowingCallable c = () ->  applicationService.checkAndSave(invalidGradeTeachingAssistantApplication);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void gradeOnOne() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication validUnsufficientGradeTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 1.0f,
                motivation, ApplicationStatus.PENDING);

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));
        // Act

        ThrowingCallable c = () ->  applicationService.checkAndSave(validUnsufficientGradeTeachingAssistantApplication);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void gradeAboveTenCheck() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidGradeTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 10.1f,
                motivation, ApplicationStatus.PENDING);

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));
        // Act

        ThrowingCallable c = () ->  applicationService.checkAndSave(invalidGradeTeachingAssistantApplication);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
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

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.of(2024, Month.SEPTEMBER, 1, 9, 0, 0),
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        applicationService.checkAndSave(validTeachingAssistantApplication);

        //Assert
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
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

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        applicationService.checkAndSave(validTeachingAssistantApplication);

        //Assert
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1200", "johndoe")))
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
        ThrowingCallable c = () -> applicationService.checkAndSave(invalidTeachingAssistantApplication);

        //Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
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

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ThrowingCallable c = () -> applicationService.checkAndSave(invalidTeachingAssistantApplication);

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void invalidDateCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication invalidTeachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", (float) 5.9,
                motivation, ApplicationStatus.PENDING);
        assertThat(invalidTeachingAssistantApplication.meetsRequirements()).isFalse();

        when(mockCourseInformation.getCourseById("CSE1300")).thenReturn(new CourseInformationResponseModel(
                "CSE1300",
                LocalDateTime.now(),
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ThrowingCallable c = () -> applicationService.checkAndSave(invalidTeachingAssistantApplication);

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(c);
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }


    @Test
    public void getWithInvalidCourseId() {
        // Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        String invalidCourseId = "CSE1305";
        applicationRepository.save(expected);

        // Act
        ThrowingCallable c = () -> applicationService.get(invalidCourseId, expected.getNetId());

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
        applicationRepository.save(expected);

        // Act
        ThrowingCallable c = () -> applicationService.get(expected.getCourseId(), invalidNetid);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void retrieveStatusRejectedApplication() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.REJECTED);

        applicationRepository.save(expected);

        //Act
        var result = applicationService.retrieveStatus(expected.getCourseId(), expected.getNetId());

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

        applicationRepository.save(expected);

        //Act
        var result = applicationService.retrieveStatus(expected.getCourseId(), expected.getNetId());

        //Assert
        assertThat(result).isInstanceOf(ApplicationStatus.class);
        assertThat(result).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    public void getApplicationsAndMaxApplicationsTest() {
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
        applicationRepository.save(firstTeachingAssistantApplication);
        applicationRepository.save(secondTeachingAssistantApplication);
        applicationRepository.save(thirdTeachingAssistantApplication);

        //Assert
        assertThat(applicationRepository.findById(new TeachingAssistantApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
        assertThat(applicationService.getApplicationFromStudent("johndoe")).size().isEqualTo(3);
        assertThat(applicationService.hasReachedMaxApplication("johndoe")).isTrue();
    }

    @Test
    public void retrieveStatusPendingApplication() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.PENDING);

        applicationRepository.save(expected);

        //Act
        var result = applicationService.retrieveStatus(expected.getCourseId(), expected.getNetId());

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
        applicationRepository.save(expected);

        //Act
        ThrowingCallable c = () -> applicationService.retrieveStatus(notCourse, expected.getNetId());

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
        applicationRepository.save(expected);

        //Act
        ThrowingCallable c = () -> applicationService.get(noApplicationCourseId, expected.getNetId());

        //Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void retrievePendingStatusTest() {
        //Arrange
        TeachingAssistantApplication expected = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I got a 10", ApplicationStatus.PENDING);

        applicationRepository.save(expected);

        //Act
        var result = applicationService.get(expected.getCourseId(), expected.getNetId());

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

        applicationRepository.save(expected);

        //Act
        var result = applicationService.get(expected.getCourseId(), expected.getNetId());

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

        applicationRepository.save(expected);

        //Act
        var result = applicationService.get(expected.getCourseId(), expected.getNetId());

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
        applicationRepository.save(teachingAssistantApplication);
        when(mockCourseInformation.startDate(teachingAssistantApplication.getCourseId())).thenReturn(LocalDateTime.MAX);

        //Act
        boolean result = applicationService.checkAndWithdraw(
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
        applicationRepository.save(teachingAssistantApplication);
        when(mockCourseInformation.startDate(teachingAssistantApplication.getCourseId())).thenReturn(LocalDateTime.now());

        //Act
        boolean result = applicationService.checkAndWithdraw(
                teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        //Assert
        assertThat(result).isFalse();
    }

    @Test
    public void checkAndWithdrawJustTooLateTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        applicationRepository.save(teachingAssistantApplication);
        when(mockCourseInformation.startDate(teachingAssistantApplication.getCourseId()))
                .thenReturn(LocalDateTime.now().plusWeeks(3));

        //Act
        boolean result = applicationService.checkAndWithdraw(
                teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        //Assert
        assertThat(result).isFalse();
    }

    @Test
    public void checkAndWithdrawJustOnTimeTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        applicationRepository.save(teachingAssistantApplication);
        when(mockCourseInformation.startDate(teachingAssistantApplication.getCourseId())).thenReturn(
                LocalDateTime.now().plusWeeks(3).plusDays(1));

        //Act
        boolean result = applicationService.checkAndWithdraw(
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
        applicationRepository.save(expected);

        // Act
        TeachingAssistantApplication actual = applicationService.get(expected.getCourseId(), expected.getNetId());

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void rejectValidApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(teachingAssistantApplication);

        // Act
        applicationService.reject(teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId());

        // Assert
        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> applicationService.reject("incorrect", teachingAssistantApplication.getNetId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(c);

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> applicationService.reject(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId());

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        TeachingAssistantApplication actual = applicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
    }

    @Test
    public void extendEmptyListWithRatingTest() {
        //Arrange
        List<TeachingAssistantApplication> emptyList = new ArrayList<>();
        List<PendingApplicationResponseModel> expectedResList = new ArrayList<>();

        //Act
        List<PendingApplicationResponseModel> resList = applicationService.extendWithRating(emptyList);

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
        Map<String, Float> expectedMap = new HashMap<>() {{
                put("jsmith", 8.0f);
                put("wsmith", 9.0f);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        var resultList = applicationService.extendWithRating(
                List.of(teachingAssistantApplication, teachingAssistantApplication2));

        var resultModel = new PendingApplicationResponseModel("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", 8.0f);
        var resultModel2 = new PendingApplicationResponseModel("CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", 9.0f);
        List<PendingApplicationResponseModel> expectedList = List.of(resultModel, resultModel2);

        assertThat(resultList).isEqualTo(expectedList);

    }

    @Test
    public void acceptValidApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be cool!", ApplicationStatus.PENDING);
        applicationRepository.save(teachingAssistantApplication);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        applicationService.accept(teachingAssistantApplication.getCourseId(), teachingAssistantApplication.getNetId(),
                expectedDuties, expectedMaxHours);

        // Assert
        TeachingAssistantApplication actual = applicationRepository
                .findById(new TeachingAssistantApplicationKey(teachingAssistantApplication.getCourseId(),
                        teachingAssistantApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(teachingAssistantApplication.getCourseId())
                        && contract.getNetId().equals(teachingAssistantApplication.getNetId())
                        && contract.getDuties().equals(expectedDuties)
                        && contract.getMaxHours() == expectedMaxHours
        ));
    }

    @Test
    public void acceptNonexistentApplication() {
        // Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> applicationService.accept("incorrect", teachingAssistantApplication.getNetId(),
                "be a good TA", 45);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(c);

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(teachingAssistantApplication);

        // Act
        ThrowingCallable c = () -> applicationService.accept(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId(),
                "be a good TA", 45);

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(teachingAssistantApplication);

        when(mockContractInformation.createContract(any())).thenReturn(false);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        ThrowingCallable c = () -> applicationService.accept(teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId(),
                expectedDuties, expectedMaxHours);

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        TeachingAssistantApplication actual = applicationRepository
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
