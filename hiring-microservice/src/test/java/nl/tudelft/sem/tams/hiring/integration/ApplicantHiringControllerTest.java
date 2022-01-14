package nl.tudelft.sem.tams.hiring.integration;

import static nl.tudelft.sem.tams.hiring.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationRequestModel;
import nl.tudelft.sem.tams.hiring.services.communication.models.CourseInformationResponseModel;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


public class ApplicantHiringControllerTest extends BaseHiringControllerTest {

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
    public void applyTooLate() throws Exception {
        //Arrange
        TeachingAssistantApplicationRequestModel validModel = new TeachingAssistantApplicationRequestModel(
                "CSE1200", 6.0f, "I want to");

        TeachingAssistantApplicationKey validKey = new TeachingAssistantApplicationKey(
                validModel.getCourseId(), exampleNetId);

        // the deadline has passed (boundary test)
        when(mockCourseInformation.getCourseById("CSE1200")).thenReturn(new CourseInformationResponseModel(
                "CSE1200",
                assumedCurrentTime.plusWeeks(3),
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
}
