package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.utils.JsonUtil.serialize;
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
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.template.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationAcceptRequestModel;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.models.PendingApplicationResponseModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.security.TokenVerifier;
import nl.tudelft.sem.template.hiring.services.TeachingAssistantApplicationService;
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import nl.tudelft.sem.template.hiring.utils.JsonUtil;
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
                    "mockCourseInformation", "mockContractInformation"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TeachingAssistantApplicationControllerTest {
    private static final String exampleNetId = "johndoe";

    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient TeachingAssistantApplicationService taApplicationService;

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
    public void gradeBelowMin() throws Exception {
        //Arrange
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("CSE1200", 0.9f,
                "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

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
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void gradeLowestTest() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("CSE1200", 1.0f,
                "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);

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
        assertThat(applicationRepository.findById(validKey)).isEmpty();
    }


    @Test
    public void gradeOnPoint() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("CSE1200", 10.0f,
                "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);

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
        assertThat(applicationRepository.findById(validKey)).isNotEmpty();
    }

    @Test
    public void gradeAboveMaxTest() throws Exception {
        //Arrange
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("CSE1200", 10.1f,
                "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

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
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void validApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("CSE1200", 6.0f,
                "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);
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
        assertThat(applicationRepository.findById(validKey)).isNotEmpty();

    }


    @Test
    public void insufficientGradeApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("CSE1200", 5.9f,
                "I want to");

        TeachingAssistantApplicationKey invalidKey = new TeachingAssistantApplicationKey(
                invalidModel.getCourseId(), exampleNetId);

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
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void invalidCourseIdApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("CSE1200", 6.0f,
                "I want to");

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
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();
    }

    @Test
    public void tooManyApplicationsTest() throws Exception {
        //Arrange
        TeachingAssistantApplication taApplication1 = new TeachingAssistantApplication("CSE1300", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication1);

        TeachingAssistantApplication taApplication2 = new TeachingAssistantApplication("CSE1400", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication2);

        TeachingAssistantApplication taApplication3 = new TeachingAssistantApplication("CSE1100", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication3);

        ApplicationRequestModel fourthApplicationModel = new ApplicationRequestModel("CSE1200", 6.0f,
                "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                fourthApplicationModel.getCourseId(), exampleNetId);

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.of(2024, Month.SEPTEMBER, 1, 9, 0, 0),
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
        assertThat(applicationRepository.findById(validKey)).isEmpty();
    }

    @Test
    public void oneMoreApplicationPossibleTest() throws Exception {
        //Arrange
        TeachingAssistantApplication taApplication1 = new TeachingAssistantApplication("CSE1300", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication1);

        TeachingAssistantApplication taApplication2 = new TeachingAssistantApplication("CSE1400", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication2);

        ApplicationRequestModel thirdApplicationModel = new ApplicationRequestModel("CSE1200", 6.0f,
                "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                thirdApplicationModel.getCourseId(), exampleNetId);

        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                LocalDateTime.of(2024, Month.SEPTEMBER, 1, 9, 0, 0),
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
        assertThat(applicationRepository.findById(validKey)).isNotEmpty();
    }


    @Test
    void withdrawOnTime() throws Exception {
        // arrange
        TeachingAssistantApplication onTime = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(onTime);

        TeachingAssistantApplicationKey key = TeachingAssistantApplicationKey.builder()
                .courseId(onTime.getCourseId())
                .netId(onTime.getNetId())
                .build();

        when(mockCourseInformation.startDate(onTime.getCourseId()))
                .thenReturn(LocalDateTime.MAX);

        // act
        ResultActions onTimeResult  = mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(key))
                .header("Authorization", "Bearer Joe"));

        // assert
        assertThat(applicationRepository.findById(key)).isEmpty();
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

        applicationRepository.save(taApplication);
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
        applicationRepository.save(taApplication);
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
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.PENDING);
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
        applicationRepository.save(taApplication);
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
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
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
        applicationRepository.save(taApplication);
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
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void withdrawTooLate() throws Exception {
        // arrange
        TeachingAssistantApplication tooLate = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(tooLate);

        TeachingAssistantApplicationKey key = TeachingAssistantApplicationKey.builder()
                .courseId(tooLate.getCourseId())
                .netId(tooLate.getNetId())
                .build();

        when(mockCourseInformation.startDate(tooLate.getCourseId()))
                .thenReturn(LocalDateTime.now());

        // act
        ResultActions onTimeResult  = mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(key))
                .header("Authorization", "Bearer Joe"));

        // assert
        assertThat(applicationRepository.findById(key)).isNotEmpty();
        onTimeResult.andExpect(status().isForbidden());

    }

    @Test
    public void rejectValidApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication);

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

        TeachingAssistantApplication actual = applicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void rejectValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication);

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

        TeachingAssistantApplication actual = applicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    public void rejectNonexistentApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication);

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

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(taApplication);

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

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(taApplication);
        applicationRepository.save(taApplication2);
        applicationRepository.save(taApplication3);
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"jsmith", "wsmith"};
        Map<String, Float> expectedMap = new HashMap<>() {{
                put("jsmith", 8.0f);
                put("wsmith", 9.0f);
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
        List<PendingApplicationResponseModel> res = parsePendingApplicationsResult(result);

        PendingApplicationResponseModel model = new PendingApplicationResponseModel(taApplication, 8.0f);
        PendingApplicationResponseModel model2 = new PendingApplicationResponseModel(taApplication2, 9.0f);
        List<PendingApplicationResponseModel> expectedResult = new ArrayList<>() {{
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

    private List<PendingApplicationResponseModel> parsePendingApplicationsResult(MvcResult result) throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        var res = new ArrayList<PendingApplicationResponseModel>();
        List<Map<String, Object>> parsed = JsonUtil.deserialize(jsonString, res.getClass());

        for (Map<String, Object> map : parsed) {
            res.add(new PendingApplicationResponseModel(
                    (String) map.get("courseId"),
                    (String) map.get("netId"),
                    ((Double) map.get("grade")).floatValue(),
                    (String) map.get("motivation"),
                    ((Double) map.get("taRating")).floatValue())
            );
        }
        return res;
    }

    @Test
    public void acceptValidApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
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

        TeachingAssistantApplication actual = applicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(taApplication.getCourseId())
                        && contract.getNetId().equals(taApplication.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationButContractCreationFails() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(taApplication);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
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

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(taApplication);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
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

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(taApplication);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
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

        TeachingAssistantApplication actual = applicationRepository
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
        applicationRepository.save(taApplication);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
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

        TeachingAssistantApplication actual = applicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
        verify(mockContractInformation, times(0)).createContract(any());
    }
}
