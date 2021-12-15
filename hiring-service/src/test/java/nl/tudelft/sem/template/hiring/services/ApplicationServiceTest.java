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
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles({"test", "mockCourseInformation", "mockContractInformation"})
public class ApplicationServiceTest {
    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient ApplicationService applicationService;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    @Autowired
    private transient ContractInformation mockContractInformation;

    @Test
    public void validCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application validApplication = new Application("CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
        assertThat(validApplication.meetsRequirements()).isTrue();

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.of(2024, Month.SEPTEMBER, 1, 9, 0, 0),
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        applicationService.checkAndSave(validApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1200", "johndoe")))
                .isNotEmpty();
    }

    @Test
    public void invalidGradeCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application invalidApplication = new Application("CSE1300", "jsmith", (float) 5.9,
                motivation, ApplicationStatus.PENDING);
        assertThat(invalidApplication.meetsRequirements()).isFalse();

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.of(2024, Month.SEPTEMBER, 1, 9, 0, 0),
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        applicationService.checkAndSave(invalidApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void checkAndWithdrawOnTimeTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        applicationRepository.save(application);
        when(mockCourseInformation.startDate(application.getCourseId())).thenReturn(LocalDateTime.MAX);

        //Act
        boolean result = applicationService.checkAndWithdraw(application.getCourseId(), application.getNetId());

        //Assert
        assertThat(result).isTrue();
    }

    @Test
    public void checkAndWithdrawTooLateTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        applicationRepository.save(application);
        when(mockCourseInformation.startDate(application.getCourseId())).thenReturn(LocalDateTime.now());

        //Act
        boolean result = applicationService.checkAndWithdraw(application.getCourseId(), application.getNetId());

        //Assert
        assertThat(result).isFalse();
    }

    @Test
    public void checkAndWithdrawJustTooLateTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        applicationRepository.save(application);
        when(mockCourseInformation.startDate(application.getCourseId())).thenReturn(LocalDateTime.now().plusWeeks(3));

        //Act
        boolean result = applicationService.checkAndWithdraw(application.getCourseId(), application.getNetId());

        //Assert
        assertThat(result).isFalse();
    }

    @Test
    public void checkAndWithdrawJustOnTimeTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                motivation, ApplicationStatus.PENDING);
        applicationRepository.save(application);
        when(mockCourseInformation.startDate(application.getCourseId())).thenReturn(
                LocalDateTime.now().plusWeeks(3).plusDays(1));

        //Act
        boolean result = applicationService.checkAndWithdraw(application.getCourseId(), application.getNetId());

        //Assert
        assertThat(result).isTrue();
    }


    @Test
    public void invalidDateCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application invalidApplication = new Application("CSE1300", "jsmith", (float) 5.9,
                motivation, ApplicationStatus.PENDING);
        assertThat(invalidApplication.meetsRequirements()).isFalse();

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.of(2022, Month.JANUARY, 1, 9, 0, 0),
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        applicationService.checkAndSave(invalidApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void getExisting() {
        // Arrange
        Application expected = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        applicationRepository.save(expected);

        // Act
        Application actual = applicationService.get(expected.getCourseId(), expected.getNetId());

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getWithInvalidCourseId() {
        // Arrange
        Application expected = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        String invalidCourseId = "CSE1305";
        applicationRepository.save(expected);

        // Act
        ThrowingCallable c = () -> applicationService.get(invalidCourseId, expected.getNetId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void getWithInvalidNetid() {
        // Arrange
        Application expected = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        String invalidNetid = "sjmith";
        applicationRepository.save(expected);

        // Act
        ThrowingCallable c = () -> applicationService.get(expected.getCourseId(), invalidNetid);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(c);
    }

    @Test
    public void rejectValidApplication() {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        // Act
        applicationService.reject(application.getCourseId(), application.getNetId());

        // Assert
        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void rejectNonexistentApplication() {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        // Act
        ThrowingCallable c = () -> applicationService.reject("incorrect", application.getNetId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(c);

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
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
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        applicationRepository.save(application);

        // Act
        ThrowingCallable c = () -> applicationService.reject(application.getCourseId(), application.getNetId());

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
    }

    @Test
    public void extendEmptyListWithRatingTest() {
        //Arrange
        List<Application> emptyList = new ArrayList<>();
        List<PendingApplicationResponseModel> expectedResList = new ArrayList<>();

        //Act
        List<PendingApplicationResponseModel> resList = applicationService.extendWithRating(emptyList);

        //Assert
        assertThat(resList).isEqualTo(expectedResList);
    }

    @Test
    public void extendWithRatingTest() {
        //Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application2 = new Application("CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);

        String[] netIds = new String[]{"jsmith", "wsmith"};
        Map<String, Float> expectedMap = new HashMap<>() {{
                put("jsmith", 8.0f);
                put("wsmith", 9.0f);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        var resultList = applicationService.extendWithRating(List.of(application, application2));

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
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        applicationService.accept(application.getCourseId(), application.getNetId(), expectedDuties, expectedMaxHours);

        // Assert
        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(application.getCourseId())
                        && contract.getNetId().equals(application.getNetId())
                        && contract.getDuties().equals(expectedDuties)
                        && contract.getMaxHours() == expectedMaxHours
        ));
    }

    @Test
    public void acceptNonexistentApplication() {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        // Act
        ThrowingCallable c = () -> applicationService.accept("incorrect", application.getNetId(),
                "be a good TA", 45);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(c);

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
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
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        applicationRepository.save(application);

        // Act
        ThrowingCallable c = () -> applicationService.accept(application.getCourseId(), application.getNetId(),
                "be a good TA", 45);

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
        verify(mockContractInformation, times(0)).createContract(any());
    }

    @Test
    public void acceptValidApplicationButCreatingContractThrowsException() {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        when(mockContractInformation.createContract(any())).thenReturn(false);

        String expectedDuties = "Do TA stuff";
        int expectedMaxHours = 42;

        // Act
        ThrowingCallable c = () -> applicationService.accept(application.getCourseId(), application.getNetId(),
                expectedDuties, expectedMaxHours);

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(application.getCourseId())
                        && contract.getNetId().equals(application.getNetId())
                        && contract.getDuties().equals(expectedDuties)
                        && contract.getMaxHours() == expectedMaxHours
        ));
    }
}
