package nl.tudelft.sem.template.course.integration;

import static nl.tudelft.sem.template.course.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import nl.tudelft.sem.template.course.security.AuthManager;
import nl.tudelft.sem.template.course.security.TokenVerifier;
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

}