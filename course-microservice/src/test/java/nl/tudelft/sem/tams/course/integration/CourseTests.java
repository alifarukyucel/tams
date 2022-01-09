package nl.tudelft.sem.tams.course.integration;

import static nl.tudelft.sem.tams.course.utils.JsonUtil.serialize;
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
import java.util.List;
import nl.tudelft.sem.tams.course.entities.Course;
import nl.tudelft.sem.tams.course.models.CourseAddResponsibleLecturerRequestModel;
import nl.tudelft.sem.tams.course.models.CourseCreationRequestModel;
import nl.tudelft.sem.tams.course.models.CourseRemoveResponsibleLecturerRequestModel;
import nl.tudelft.sem.tams.course.models.CourseResponseModel;
import nl.tudelft.sem.tams.course.repositories.CourseRepository;
import nl.tudelft.sem.tams.course.security.AuthManager;
import nl.tudelft.sem.tams.course.security.TokenVerifier;
import nl.tudelft.sem.tams.course.utils.JsonUtil;
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
 * Integration tests for CourseController.
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

        // Assert
        result.andExpect(status().isOk());

        CourseResponseModel response =
                JsonUtil.deserialize(result.andReturn().getResponse().getContentAsString(), CourseResponseModel.class);

        assertThat(response.getId()).isEqualTo(course.getId());
        assertThat(response.getDescription()).isEqualTo(course.getDescription());
    }

    @Test
    public void getNonExistingCourse() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(get("/CSE9999")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Andy"));
        
        // Assert
        action.andExpect(status().isNotFound());
    }

    @Test
    public void createCourse_NoExistingCourseInDatabase() throws Exception {
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
    public void createCourse_ExistingCourseInDatabase_throwsConflictException() throws Exception {
        // Arrange
        mockAuthentication(responsibleLecturer, true);

        Course existingCourse = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(existingCourse);

        CourseCreationRequestModel courseModel = new CourseCreationRequestModel(testCourseId, testStartDate.plusDays(1),
                testCourseName + " Conflict", testDescription + " Conflict", testNumberOfStudents + 1);

        // Act
        ResultActions action = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(courseModel))
                .header("Authorization", "Bearer Andy"));

        // Assert
        action.andExpect(status().isConflict());

        assertThat(courseRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void isResponsibleLecturer_correctLecturer_200OkTrue() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(get("/CSE2115/lecturer/fmulder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        Boolean actual = Boolean.valueOf(result.getResponse().getContentAsString());
        assertThat(actual).isTrue();
    }

    @Test
    public void isResponsibleLecturer_withMultipleLecturers_200OkTrue() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        responsibleLecturers.add("anniballePanichella");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(get("/CSE2115/lecturer/anniballePanichella")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        Boolean actual = Boolean.valueOf(result.getResponse().getContentAsString());
        assertThat(actual).isTrue();
    }

    @Test
    public void isResponsibleLecturer_lecturerDoesNotExist_200OkFalse() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(get("/CSE2115/lecturer/fForRespect")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        Boolean actual = Boolean.valueOf(result.getResponse().getContentAsString());
        assertThat(actual).isFalse();
    }

    @Test
    public void isResponsibleLecturer_courseDoesNotExist_200OkFalse() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(get("/randomCourse/lecturer/fmulder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        Boolean actual = Boolean.valueOf(result.getResponse().getContentAsString());
        assertThat(actual).isFalse();
    }

    @Test
    public void addResponsibleLecturers_courseDoesNotExist_403Forbidden() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);  // to be authenticated as responsible lecturer
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(put("/nonExistingCourse/addLecturer/lecturer2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isForbidden());
    }

    @Test
    public void addResponsibleLecturers_addSingleLecturer_userIsNotaLecturer() throws Exception {
        // Arrange
        // responsible lecturers is empty, hence, the requesting user is not authenticated as a lecturer
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/addLecturer/addedLecturer")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isForbidden());
    }

    @Test
    public void addResponsibleLecturers_addSingleLecturer() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer); // to be authenticated as responsible lecturer
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/addLecturer/lecturer2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        Course expectedCourse = courseRepository.getById(course.getId());
        List<String> expectedResponsibleLecturers = expectedCourse.getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers).containsExactlyInAnyOrder(responsibleLecturer, "lecturer2");
        action.andExpect(status().isOk());
    }

    @Test
    public void addResponsibleLecturers_addMultipleLecturers() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer); // to be authenticated as responsible lecturer
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        List<String> addedResponsibleLecturers = new ArrayList<>(
                List.of(responsibleLecturer, "addedLecturer", "addedLecturer2"));
        var model = new CourseAddResponsibleLecturerRequestModel(addedResponsibleLecturers);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/addLecturer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Mulder"));

        // Assert
        Course expectedCourse = courseRepository.getById(course.getId());
        List<String> expectedResponsibleLecturers = expectedCourse.getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers).containsExactlyInAnyOrder("fmulder", "addedLecturer", "addedLecturer2");
        action.andExpect(status().isOk());
    }

    @Test
    public void addResponsibleLecturers_addMultipleLecturers_userIsNotaLecturer_403Forbidden() throws Exception {
        // Arrange
        // responsible lecturers is empty, hence, the requesting user is not authenticated as a lecturer
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        List<String> addedResponsibleLecturers = new ArrayList<>(
                List.of(responsibleLecturer, "addedLecturer", "addedLecturer2"));
        var model = new CourseAddResponsibleLecturerRequestModel(addedResponsibleLecturers);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/addLecturer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isForbidden());
    }

    @Test
    public void removeResponsibleLecturers_courseDoesNotExist_403Forbidden() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);  // to be authenticated as responsible lecturer
        responsibleLecturers.add("lecturer2");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(put("/nonExistingCourse/removeLecturer/lecturer2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isForbidden());
    }

    @Test
    public void removeResponsibleLecturers_lecturerDoesNotExist() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);  // to be authenticated as responsible lecturer
        responsibleLecturers.add("lecturer");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/removeLecturer/nonExistentLecturer")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isOk());  // Remove lecturers method is idempotent, so doesn't throw an exception.
    }

    @Test
    public void removeResponsibleLecturers_removeSingleLecturer() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);  // to be authenticated as responsible lecturer
        responsibleLecturers.add("lecturer2");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/removeLecturer/lecturer2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        Course expectedCourse = courseRepository.getById(course.getId());
        List<String> expectedResponsibleLecturers = expectedCourse.getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers).containsExactlyInAnyOrder(responsibleLecturer);
        action.andExpect(status().isOk());
    }

    @Test
    public void removeResponsibleLecturers_removeMultipleLecturers_asMultipleArguments() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer); // to be authenticated as responsible lecturer
        responsibleLecturers.add("lecturer2");
        responsibleLecturers.add("lecturer3");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);


        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/removeLecturer/lecturer2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Mulder"));

        // Assert
        Course expectedCourse = courseRepository.getById(course.getId());
        List<String> expectedResponsibleLecturers = expectedCourse.getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers).containsExactlyInAnyOrder(responsibleLecturer, "lecturer3");
        action.andExpect(status().isOk());
    }

    @Test
    public void removeResponsibleLecturers_removeMultipleLecturers_asList() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer); // to be authenticated as responsible lecturer
        responsibleLecturers.add("lecturer2");
        responsibleLecturers.add("lecturer3");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        List<String> toBeRemovedResponsibleLecturers = new ArrayList<>(
                List.of("lecturer2", "lecturer3"));
        CourseRemoveResponsibleLecturerRequestModel model =
            new CourseRemoveResponsibleLecturerRequestModel(toBeRemovedResponsibleLecturers);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2115/removeLecturer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Mulder"));

        // Assert
        Course expectedCourse = courseRepository.getById(course.getId());
        List<String> expectedResponsibleLecturers = expectedCourse.getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers).containsExactlyInAnyOrder(responsibleLecturer);
        action.andExpect(status().isOk());
    }

    @Test
    public void removeResponsibleLecturers_removeMultipleLecturers_courseDoesNotExist_throws403() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer); // to be authenticated as responsible lecturer
        responsibleLecturers.add("lecturer2");
        responsibleLecturers.add("lecturer3");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
            testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        List<String> toBeRemovedResponsibleLecturers = new ArrayList<>(
            List.of("lecturer2", "lecturer3"));
        CourseRemoveResponsibleLecturerRequestModel model =
            new CourseRemoveResponsibleLecturerRequestModel(toBeRemovedResponsibleLecturers);

        // Act
        ResultActions action = mockMvc.perform(put("/nonExistentCourse/removeLecturer/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isForbidden());
    }

    @Test
    public void removeResponsibleLecturers_notAuthenticated_throws403() throws Exception {
        // Arrange
        // responsibleLecturer is not in responsibleLecturers, therefore not authenticated.
        responsibleLecturers.add("lecturer2");
        responsibleLecturers.add("lecturer3");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
            testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        List<String> toBeRemovedResponsibleLecturers = new ArrayList<>(
            List.of("lecturer2", "lecturer3"));
        CourseRemoveResponsibleLecturerRequestModel model =
            new CourseRemoveResponsibleLecturerRequestModel(toBeRemovedResponsibleLecturers);

        // Act
        ResultActions action = mockMvc.perform(put("/CSE2215/removeLecturer/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Mulder"));

        // Assert
        action.andExpect(status().isForbidden());
    }
}
