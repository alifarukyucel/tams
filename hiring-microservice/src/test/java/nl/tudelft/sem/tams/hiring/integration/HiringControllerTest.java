package nl.tudelft.sem.tams.hiring.integration;

import static nl.tudelft.sem.tams.hiring.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationAcceptRequestModel;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationRequestModel;
import nl.tudelft.sem.tams.hiring.providers.TimeProvider;
import nl.tudelft.sem.tams.hiring.repositories.TeachingAssistantApplicationRepository;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.security.TokenVerifier;
import nl.tudelft.sem.tams.hiring.services.HiringService;
import nl.tudelft.sem.tams.hiring.services.communication.models.CourseInformationResponseModel;
import nl.tudelft.sem.tams.hiring.utils.JsonUtil;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier",
                    "mockCourseInformation", "mockContractInformation", "mockTimeProvider"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class HiringControllerTest {
    private static final transient String exampleNetId = "johndoe";

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
        when(timeProvider.getCurrentLocalDateTime()).thenReturn(assumedCurrentTime);
    }

    @Test
    public void gradeBelowMin() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel invalidModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 0.9f, "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        invalidResults.andExpect(status().isForbidden());
        assertThat(taApplicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void gradeLowestTest() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel validModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 1.0f, "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(validModel))
                .header("Authorization", "Bearer Joe"));
        //assert
        validResults.andExpect(status().isForbidden());
        assertThat(taApplicationRepository.findById(validKey)).isEmpty();
    }


    @Test
    public void gradeOnPoint() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel validModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 10.0f, "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(validModel))
                .header("Authorization", "Bearer Joe"));
        //assert
        validResults.andExpect(status().isOk());
        assertThat(taApplicationRepository.findById(validKey)).isNotEmpty();
    }

    @Test
    public void gradeAboveMaxTest() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel invalidModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 10.1f, "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        invalidResults.andExpect(status().isForbidden());
        assertThat(taApplicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void validApplicationTest() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel validModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 6.0f, "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(validModel))
                .header("Authorization", "Bearer Joe"));
        //assert
        validResults.andExpect(status().isOk());
        assertThat(taApplicationRepository.findById(validKey)).isNotEmpty();

    }


    @Test
    public void insufficientGradeApplicationTest() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel invalidModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 5.9f, "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        invalidResults.andExpect(status().isForbidden());
        assertThat(taApplicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void invalidCourseIdApplicationTest() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel invalidModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 6.0f, "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(null);

        //Act
        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        invalidResults.andExpect(status().isNotFound());
        assertThat(taApplicationRepository.findById(invalidKey)).isEmpty();
    }

    /**
     * Boundary test.
     * On-point test for reaching maximum amount of applications
     * 3 pending applications
     */
    @Test
    public void tooManyApplicationsTest() throws Exception {
        //Arrange
        TeachingAssistantApplication taApplication1 = new TeachingAssistantApplication("CSE1300", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication1);

        TeachingAssistantApplication taApplication2 = new TeachingAssistantApplication("CSE1400", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication2);

        TeachingAssistantApplication taApplication3 = new TeachingAssistantApplication("CSE1100", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication3);

        TeachingAssistantApplicationRequestModel fourthApplicationModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 6.0f, "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                fourthApplicationModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions limitReached = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(fourthApplicationModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        limitReached.andExpect(status().isForbidden());
        assertThat(taApplicationRepository.findById(validKey)).isEmpty();
    }

    /**
     * Boundary test.
     * Off-point test for reaching maximum  amount of applications
     * 2 pending applications
     */
    @Test
    public void oneMoreApplicationPossibleTest() throws Exception {
        //Arrange
        TeachingAssistantApplication acceptedApplication = new TeachingAssistantApplication("CSE1000", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        taApplicationRepository.save(acceptedApplication);

        TeachingAssistantApplication rejectedApplication = new TeachingAssistantApplication("CSE1100", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.REJECTED);
        taApplicationRepository.save(rejectedApplication);

        TeachingAssistantApplication taApplication1 = new TeachingAssistantApplication("CSE1300", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication1);

        TeachingAssistantApplication taApplication2 = new TeachingAssistantApplication("CSE1400", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication2);

        TeachingAssistantApplicationRequestModel thirdApplicationModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 6.0f, "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                thirdApplicationModel.getCourseId(), exampleNetId);

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.MAX,
                "CourseName",
                "CourseDescription",
                100,
                new ArrayList<>()));

        //Act
        ResultActions oneMorePossible = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(thirdApplicationModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        oneMorePossible.andExpect(status().isOk());
        assertThat(taApplicationRepository.findById(validKey)).isNotEmpty();
    }


    @Test
    void withdrawOnTime() throws Exception {
        // arrange
        TeachingAssistantApplication onTime = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(onTime);

        TeachingAssistantApplicationKey key = TeachingAssistantApplicationKey.builder()
                .courseId(onTime.getCourseId())
                .netId(onTime.getNetId())
                .build();

        //LocalDateTime.MAX is used here to guarantee the deadline hasn't passed yet
        when(mockCourseInformation.startDate(onTime.getCourseId()))
                .thenReturn(LocalDateTime.MAX);

        // act
        ResultActions onTimeResult  = mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(key))
                .header("Authorization", "Bearer Joe"));

        // assert
        assertThat(taApplicationRepository.findById(key)).isEmpty();
        onTimeResult.andExpect(status().isOk());
    }

    @Test
    void invalidCourseGetStatusTest() throws Exception {
        //arrange
        TeachingAssistantApplication taApplication = TeachingAssistantApplication.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.PENDING)
                .build();

        taApplicationRepository.save(taApplication);
        String invalidCourseId = "CSE1300";
        TeachingAssistantApplicationKey key = new TeachingAssistantApplicationKey(
                invalidCourseId, taApplication.getNetId());

        //act
        ResultActions wrongCourseId = mockMvc.perform(get("/status/" + invalidCourseId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = wrongCourseId
                .andExpect(status().isNotFound())
                .andReturn();
        assertThat(taApplication.getCourseId()).isNotEqualTo(invalidCourseId);
    }

    @Test
    void pendingStatusTest() throws Exception {
        //arrange
        TeachingAssistantApplication taApplication = TeachingAssistantApplication.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.PENDING)
                .build();
        taApplicationRepository.save(taApplication);
        TeachingAssistantApplicationKey key = new TeachingAssistantApplicationKey(
                taApplication.getCourseId(), taApplication.getNetId());

        //act
        ResultActions pendingApplication = mockMvc.perform(get("/status/" + taApplication.getCourseId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = pendingApplication
                .andExpect(status().isOk())
                .andReturn();
        assertThat(taApplication.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(taApplicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void acceptedStatusTest() throws Exception {
        //arrange
        TeachingAssistantApplication taApplication = TeachingAssistantApplication.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.ACCEPTED)
                .build();
        taApplicationRepository.save(taApplication);
        TeachingAssistantApplicationKey key = new TeachingAssistantApplicationKey(
                taApplication.getCourseId(), taApplication.getNetId());

        //act
        ResultActions pendingApplication = mockMvc.perform(get("/status/" + taApplication.getCourseId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = pendingApplication
                .andExpect(status().isOk())
                .andReturn();
        assertThat(taApplication.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(taApplicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    void rejectedStatusTest() throws Exception {
        //arrange
        TeachingAssistantApplication taApplication = TeachingAssistantApplication.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.REJECTED)
                .build();
        taApplicationRepository.save(taApplication);
        TeachingAssistantApplicationKey key = new TeachingAssistantApplicationKey(
                taApplication.getCourseId(), taApplication.getNetId());

        //act
        ResultActions pendingApplication = mockMvc.perform(get("/status/" + taApplication.getCourseId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = pendingApplication
                .andExpect(status().isOk())
                .andReturn();
        assertThat(taApplication.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(taApplicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void withdrawTooLate() throws Exception {
        // arrange
        TeachingAssistantApplication tooLate = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(tooLate);

        TeachingAssistantApplicationKey key = TeachingAssistantApplicationKey.builder()
                .courseId(tooLate.getCourseId())
                .netId(tooLate.getNetId())
                .build();

        //AssumedCurrentTime is used here the startTime of the course to guarantee the deadline has passed
        when(mockCourseInformation.startDate(tooLate.getCourseId()))
                .thenReturn(assumedCurrentTime);

        // act
        ResultActions onTimeResult  = mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(key))
                .header("Authorization", "Bearer Joe"));

        // assert
        assertThat(taApplicationRepository.findById(key)).isNotEmpty();
        onTimeResult.andExpect(status().isForbidden());
    }

    @Test
    public void rejectValidApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId(taApplication.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isOk());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void rejectValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId(taApplication.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isForbidden());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    public void rejectNonexistentApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId("invalidNetid")
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isNotFound());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
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
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId(taApplication.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
    }

    @Test
    public void getPendingApplicationsTest() throws Exception {
        //Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication taApplication2 = new TeachingAssistantApplication("CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication taApplication3 = new TeachingAssistantApplication("CSE1300", "nsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.ACCEPTED);
        taApplicationRepository.save(taApplication);
        taApplicationRepository.save(taApplication2);
        taApplicationRepository.save(taApplication3);
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"jsmith", "wsmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("jsmith", 8.0d);
                put("wsmith", 9.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();


        //Parse json
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);

        PendingTeachingAssistantApplicationResponseModel model = new PendingTeachingAssistantApplicationResponseModel(
                taApplication, 8.0d);
        PendingTeachingAssistantApplicationResponseModel model2 = new PendingTeachingAssistantApplicationResponseModel(
                taApplication2, 9.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedResult = new ArrayList<>() {{
                add(model);
                add(model2);
            }
        };

        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void getPendingApplicationsWhileNotBeingResponsibleLecturerTest() throws Exception {
        //Arrange
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(false);

        //Act
        ResultActions result = mockMvc.perform(get("/applications/CSE1300/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        result.andExpect(status().isForbidden());
    }

    private List<PendingTeachingAssistantApplicationResponseModel> parsePendingApplicationsResult(MvcResult result)
            throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        var res = new ArrayList<PendingTeachingAssistantApplicationResponseModel>();
        List<Map<String, Object>> parsed = JsonUtil.deserialize(jsonString, res.getClass());

        for (Map<String, Object> map : parsed) {
            res.add(new PendingTeachingAssistantApplicationResponseModel(
                    (String) map.get("courseId"),
                    (String) map.get("netId"),
                    ((Double) map.get("grade")).floatValue(),
                    (String) map.get("motivation"),
                    ((Double) map.get("taRating")))
            );
        }
        return res;
    }

    @Test
    public void getRecommendedApplicationsWhileNotBeingResponsibleLecturerTest() throws Exception {
        //Arrange
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(false);

        //Act
        ResultActions result = mockMvc.perform(get("/applications/CSE1300/recommended/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        result.andExpect(status().isForbidden());
    }

    /**
     * Test for checking if the method still works when an invalid index (too low) is provided.
     * A parameterized test is used here to make sure it works for both 0 and a negative amount.
     *
     * @param amount The (invalid) amount of recommended applications to request.
     */
    @ParameterizedTest
    @CsvSource({"0", "-1"})
    public void getRecommendedApplicationsIndexTooLow(String amount) throws Exception {
        //Arrange
        List<PendingTeachingAssistantApplicationResponseModel> expected = new ArrayList<>();
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/" + amount)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expected);
    }

    @Test
    public void getRecommendedApplicationsIndexTooHigh() throws Exception {
        //Arrange
        TeachingAssistantApplication application = new TeachingAssistantApplication("CSE1300", "asmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        taApplicationRepository.save(application);

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"asmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("asmith", 8.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        var model = new PendingTeachingAssistantApplicationResponseModel(application, 8.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedResult = List.of(model);


        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void getRecommendedApplications() throws Exception {
        //Arrange
        TeachingAssistantApplication application = new TeachingAssistantApplication("CSE1300", "asmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application2 = new TeachingAssistantApplication("CSE1300", "bsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application3 = new TeachingAssistantApplication("CSE1300", "csmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application4 = new TeachingAssistantApplication("CSE1300", "dsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application5 = new TeachingAssistantApplication("CSE1300", "esmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application6 = new TeachingAssistantApplication("CSE1300", "fsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.ACCEPTED);
        taApplicationRepository.save(application);
        taApplicationRepository.save(application2);
        taApplicationRepository.save(application3);
        taApplicationRepository.save(application4);
        taApplicationRepository.save(application5);
        taApplicationRepository.save(application6);

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"asmith", "bsmith", "csmith", "dsmith", "esmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("asmith", 8.0d);
                put("bsmith", 9.0d);
                put("csmith", 3.0d);
                put("dsmith", 2.0d);
                put("esmith", -1.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        //Notice how in the following code application4 is not added because it isn't in the top-4 recommended.
        //Also notice the order of which the applications were added to the expected result, in the order of TA-rating.
        PendingTeachingAssistantApplicationResponseModel model2 = new PendingTeachingAssistantApplicationResponseModel(
                application2, 9.0d);
        PendingTeachingAssistantApplicationResponseModel model = new PendingTeachingAssistantApplicationResponseModel(
                application, 8.0d);
        PendingTeachingAssistantApplicationResponseModel model5 = new PendingTeachingAssistantApplicationResponseModel(
                application5, -1.0d);
        PendingTeachingAssistantApplicationResponseModel model3 = new PendingTeachingAssistantApplicationResponseModel(
                application3, 3.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedResult = List.of(model2, model, model5, model3);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/4")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void acceptValidApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING, "tueindhoven@utwente.nl");
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isOk());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(taApplication.getCourseId())
                        && contract.getNetId().equals(taApplication.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
                        && contract.getTaContactEmail().equals(taApplication.getContactEmail())
        ));
    }

    @Test
    public void acceptValidApplicationButContractCreationFails() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(taApplication.getCourseId())
                        && contract.getNetId().equals(taApplication.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(false);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isForbidden());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation, times(0)).createContract(any());
    }

    @Test
    public void acceptNonexistentApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId("invalidNetid")
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isNotFound());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
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
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
        verify(mockContractInformation, times(0)).createContract(any());
    }
}
