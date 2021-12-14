package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationAcceptRequestModel;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.security.TokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier", "mockCourseInformation",
        "mockContractInformation"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ApplicationControllerTest {
    private static String exampleNetId = "johndoe";

    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    @Autowired
    private transient ContractInformation mockContractInformation;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient TokenVerifier mockTokenVerifier;

    /**
     * Setup mocking before tests run.
     */
    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getNetid()).thenReturn(exampleNetId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(exampleNetId);
    }

    @Test
    public void validApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("cse1200", (float) 6.0,
                "I want to");

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), exampleNetId);

        //Act
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(validModel))
                .header("Authorization", "Bearer Joe"));
        //assert
        validResults.andExpect(status().isOk());
        assertThat(applicationRepository.findById(validKey)).isNotEmpty();

    }

    @Test
    public void invalidApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("cse1300", (float) 5.9,
                "I want to");

        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

        //Act
        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        invalidResults.andExpect(status().isBadRequest());
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void rejectValidApplication() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationKey lookup = ApplicationKey.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isOk());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void rejectValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationKey lookup = ApplicationKey.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isForbidden());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    public void rejectNonexistentApplication() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationKey lookup = ApplicationKey.builder()
                .courseId(application.getCourseId())
                .netId("invalidNetid")
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isNotFound());

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
    public void rejectNonPendingApplication(String status) throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        applicationRepository.save(application);

        ApplicationKey lookup = ApplicationKey.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
    }

    @Test
    public void acceptValidApplication() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .duties("Be a good TA")
                .maxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isOk());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(application.getCourseId()) &&
                        contract.getNetId().equals(application.getNetId()) &&
                        contract.getDuties().equals(model.getDuties()) &&
                        contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationButContractCreationFails() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .duties("Be a good TA")
                .maxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(application.getCourseId()) &&
                        contract.getNetId().equals(application.getNetId()) &&
                        contract.getDuties().equals(model.getDuties()) &&
                        contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .duties("Be a good TA")
                .maxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(false);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isForbidden());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation, times(0)).createContract(any());
    }

    @Test
    public void acceptNonexistentApplication() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .courseId(application.getCourseId())
                .netId("invalidNetid")
                .duties("Be a good TA")
                .maxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isNotFound());

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
    public void acceptNonPendingApplication(String status) throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .courseId(application.getCourseId())
                .netId(application.getNetId())
                .duties("Be a good TA")
                .maxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, application.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        Application actual = applicationRepository
                .findById(new ApplicationKey(application.getCourseId(), application.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
        verify(mockContractInformation, times(0)).createContract(any());
    }
}
