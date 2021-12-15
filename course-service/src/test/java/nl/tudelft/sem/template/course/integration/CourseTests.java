package nl.tudelft.sem.template.course.integration;

import static nl.tudelft.sem.template.course.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.models.CourseCreationRequestModel;
import nl.tudelft.sem.template.course.models.CourseResponseModel;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import nl.tudelft.sem.template.course.security.AuthManager;
import nl.tudelft.sem.template.course.security.TokenVerifier;
import nl.tudelft.sem.template.course.services.exceptions.ConflictException;
import nl.tudelft.sem.template.course.utils.JsonUtil;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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


/**
 * Integration tests mainly for courseController.
 *
 * @created 09 /12/2021, 20:58
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CourseTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient CourseRepository courseRepository;

    @Autowired
    private transient TokenVerifier mockTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    final String testCourseId = "CSE2115";
    final LocalDateTime testStartDate = LocalDateTime.of(2021, 12, 1, 0, 0);
    final String testCourseName = "SEM";
    final String testDescription = "swe methods";
    final int testNumberOfStudents = 300;
    final String responsibleLecturer = "fmulder";
    ArrayList<String> responsibleLecturers = new ArrayList<>();

    /**
     * Sets up the environment before each test.
     */
    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        ArrayList<String> responsibleLecturers = new ArrayList<String>();
        when(mockAuthenticationManager.getNetid()).thenReturn(responsibleLecturer);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(responsibleLecturer);
    }

    // Mock authentication to show that we are signed in as a certain user.
    void mockAuthentication(String netId, boolean isResponsibleLecturer) {
        when(mockAuthenticationManager.getNetid()).thenReturn(netId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(netId);
    }

    void mockAuthentication(String netId) {
        mockAuthentication(netId, false);
    }

    @Test
    public void getExistingCourse() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions result = mockMvc.perform(get("/CSE2115")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer SomeHackingProdigy"));

        Course expected = courseRepository.getById(course.getId());

        // Assert
        result.andExpect(status().isOk());
        assertThat(expected.getId()).isNotNull();
        assertThat(expected).isEqualTo(course);
    }

    @Test
    public void getNonExistingCourse() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/CSE9999")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Andy"));

        Course expected = courseRepository.getById(course.getId());
        
        // Assert
        resultActions.andExpect(status().isNotFound());
        assertThat(expected.getId()).isNotNull();
        assertThat(expected).isEqualTo(course);
    }

    @Test
    void createCourse_NoExistingCourseInDatabase() throws Exception {
        // Arrange
        mockAuthentication("Andy", true);

        CourseCreationRequestModel courseModel = new CourseCreationRequestModel(testCourseId, testStartDate,
                testCourseName, testDescription, testNumberOfStudents);

        // Act
        ResultActions action = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(courseModel))
                .header("Authorization", "Bearer Andy"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        CourseResponseModel response =
                JsonUtil.deserialize(result.getResponse().getContentAsString(), CourseResponseModel.class);

        assertThat(response).isNotNull();
        assertThat(CourseResponseModel.fromCourse(courseRepository.getById(testCourseId)))
                .isEqualTo(response);
    }

    @Test
    void createCourse_ExistingCourseInDatabase_throwsConflictException() throws Exception {
        // Arrange
        mockAuthentication(responsibleLecturer, true);

        Course existingCourse = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(existingCourse);

        CourseCreationRequestModel courseModel = new CourseCreationRequestModel(testCourseId, testStartDate,
                testCourseName, testDescription, testNumberOfStudents);

        // Act
        ResultActions action = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(courseModel))
                .header("Authorization", "Bearer Andy"));

        // Assert
        MvcResult result = action
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void isResponsibleLecturer_withValidData_returnsTrue() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/course/CSE2115/lecturer/fmulder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        assertThat(Boolean.valueOf(resultActions.andReturn().getResponse().getContentAsString())).isTrue();
    }

    @Test
    public void isResponsibleLecturer_withValidDataMultipleLecturers_returnsTrue() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        responsibleLecturers.add("anniballePanichella");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/course/CSE2115/lecturer/fmulder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        assertThat(Boolean.valueOf(resultActions.andReturn().getResponse().getContentAsString())).isTrue();
    }

    @Test
    public void isResponsibleLecturer_invalidLecturer_returnsFalse() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/course/CSE2115/lecturer/fForRespect")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        assertThat(Boolean.valueOf(resultActions.andReturn().getResponse().getContentAsString())).isFalse();
    }

    @Test
    public void isResponsibleLecturer_courseDoesNotExist_throws404() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/course/randomCourse/lecturer/fmulder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        resultActions.andExpect(status().isNotFound());

    }
}
