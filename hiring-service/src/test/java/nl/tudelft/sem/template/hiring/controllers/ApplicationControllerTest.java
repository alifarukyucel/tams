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
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationAcceptRequestModel;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.models.PendingApplicationResponseModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.security.TokenVerifier;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
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
public class ApplicationControllerTest {
    private static final String exampleNetId = "johndoe";

    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient ApplicationService applicationService;

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

        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

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

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), exampleNetId);

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

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), exampleNetId);

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

        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

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

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), exampleNetId);
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

        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

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

        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

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
        Application application1 = new Application("CSE1300", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application1);

        Application application2 = new Application("CSE1400", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application2);

        Application application3 = new Application("CSE1100", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application3);

        ApplicationRequestModel fourthApplicationModel = new ApplicationRequestModel("CSE1200", 6.0f,
                "I want to");

        ApplicationKey validKey = new ApplicationKey(fourthApplicationModel.getCourseId(), exampleNetId);

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
        Application acceptedApplication = new Application("CSE1000", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.ACCEPTED);
        applicationRepository.save(acceptedApplication);

        Application rejectedApplication = new Application("CSE1100", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.REJECTED);
        applicationRepository.save(rejectedApplication);

        Application application1 = new Application("CSE1300", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application1);

        Application application2 = new Application("CSE1400", exampleNetId, 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application2);

        ApplicationRequestModel thirdApplicationModel = new ApplicationRequestModel("CSE1200", 6.0f,
                "I want to");

        ApplicationKey validKey = new ApplicationKey(thirdApplicationModel.getCourseId(), exampleNetId);

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
        Application onTime = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(onTime);

        ApplicationKey key = ApplicationKey.builder()
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
        Application application = Application.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.PENDING)
                .build();

        applicationRepository.save(application);
        String invalidCourseId = "CSE1300";
        ApplicationKey key = new ApplicationKey(invalidCourseId, application.getNetId());

        //act
        ResultActions wrongCourseId = mockMvc.perform(get("/status/" + invalidCourseId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = wrongCourseId
                .andExpect(status().isNotFound())
                .andReturn();
        assertThat(application.getCourseId()).isNotEqualTo(invalidCourseId);
    }

    @Test
    void pendingStatusTest() throws Exception {
        //arrange
        Application application = Application.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);
        ApplicationKey key = new ApplicationKey(application.getCourseId(), application.getNetId());

        //act
        ResultActions pendingApplication = mockMvc.perform(get("/status/" + application.getCourseId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = pendingApplication
                .andExpect(status().isOk())
                .andReturn();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void acceptedStatusTest() throws Exception {
        //arrange
        Application application = Application.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.ACCEPTED)
                .build();
        applicationRepository.save(application);
        ApplicationKey key = new ApplicationKey(application.getCourseId(), application.getNetId());

        //act
        ResultActions pendingApplication = mockMvc.perform(get("/status/" + application.getCourseId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = pendingApplication
                .andExpect(status().isOk())
                .andReturn();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    void rejectedStatusTest() throws Exception {
        //arrange
        Application application = Application.builder()
                .netId(exampleNetId)
                .courseId("CSE1200")
                .grade(9.0f)
                .motivation("I like TAs")
                .status(ApplicationStatus.REJECTED)
                .build();
        applicationRepository.save(application);
        ApplicationKey key = new ApplicationKey(application.getCourseId(), application.getNetId());

        //act
        ResultActions pendingApplication = mockMvc.perform(get("/status/" + application.getCourseId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));

        //assert
        MvcResult result = pendingApplication
                .andExpect(status().isOk())
                .andReturn();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void withdrawTooLate() throws Exception {
        // arrange
        Application tooLate = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(tooLate);

        ApplicationKey key = ApplicationKey.builder()
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
    public void getPendingApplicationsTest() throws Exception {
        //Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application2 = new Application("CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application3 = new Application("CSE1300", "nsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);
        applicationRepository.save(application2);
        applicationRepository.save(application3);
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
        List<PendingApplicationResponseModel> res = parsePendingApplicationsResult(result);

        PendingApplicationResponseModel model = new PendingApplicationResponseModel(application, 8.0d);
        PendingApplicationResponseModel model2 = new PendingApplicationResponseModel(application2, 9.0d);
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
        List<PendingApplicationResponseModel> expected = new ArrayList<>();
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
        List<PendingApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expected);
    }
    
    @Test
    public void getRecommendedApplicationsIndexTooHigh() throws Exception {
        //Arrange
        Application application = new Application("CSE1300", "asmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"asmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("asmith", 8.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        PendingApplicationResponseModel model = new PendingApplicationResponseModel(application, 8.0d);
        List<PendingApplicationResponseModel> expectedResult = List.of(model);


        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void getRecommendedApplications() throws Exception {
        //Arrange
        Application application = new Application("CSE1300", "asmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application2 = new Application("CSE1300", "bsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application3 = new Application("CSE1300", "csmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application4 = new Application("CSE1300", "dsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application5 = new Application("CSE1300", "esmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        Application application6 = new Application("CSE1300", "fsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);
        applicationRepository.save(application2);
        applicationRepository.save(application3);
        applicationRepository.save(application4);
        applicationRepository.save(application5);
        applicationRepository.save(application6);

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
        PendingApplicationResponseModel model2 = new PendingApplicationResponseModel(application2, 9.0d);
        PendingApplicationResponseModel model = new PendingApplicationResponseModel(application, 8.0d);
        PendingApplicationResponseModel model5 = new PendingApplicationResponseModel(application5, -1.0d);
        PendingApplicationResponseModel model3 = new PendingApplicationResponseModel(application3, 3.0d);
        List<PendingApplicationResponseModel> expectedResult = List.of(model2, model, model5, model3);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/4")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void acceptValidApplication() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .withCourseId(application.getCourseId())
                .withNetId(application.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
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
                contract.getCourseId().equals(application.getCourseId())
                        && contract.getNetId().equals(application.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationButContractCreationFails() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .withCourseId(application.getCourseId())
                .withNetId(application.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
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
                contract.getCourseId().equals(application.getCourseId())
                        && contract.getNetId().equals(application.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(application);

        ApplicationAcceptRequestModel model = ApplicationAcceptRequestModel.builder()
                .withCourseId(application.getCourseId())
                .withNetId(application.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
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
                .withCourseId(application.getCourseId())
                .withNetId("invalidNetid")
                .withDuties("Be a good TA")
                .withMaxHours(42)
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
                .withCourseId(application.getCourseId())
                .withNetId(application.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
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
