package nl.tudelft.sem.template.course.integration;

import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.models.CourseModel;
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

import java.time.LocalDateTime;

import static nl.tudelft.sem.template.course.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @created 09/12/2021, 20:58
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


    final static String testCourseID = "CSE2115";
    final static LocalDateTime testStartDate = LocalDateTime.of(2021, 12, 1, 0, 0);
    final static String testCourseName = "SEM";
    final static String testDescription = "swe methods";
    final static int testNumberOfStudents = 300;
    final static String responsibleLecturer = "fmulder";

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        // Save a basic course in db.
        when(mockAuthenticationManager.getNetid()).thenReturn(responsibleLecturer);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(responsibleLecturer);
    }

    @Test
    public void createCourse_withValidData_worksCorrectly() throws Exception {
        // Arrange
        CourseModel newCourse = new CourseModel(testCourseID, testStartDate, testCourseName, testDescription,
                testNumberOfStudents);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(newCourse)));

        // Assert
        resultActions.andExpect(status().isOk());

        Course savedCourse = courseRepository.getById(testCourseID);

        assertThat(savedCourse.getId()).isEqualTo(testCourseID);
        assertThat(savedCourse.getName()).isEqualTo(testCourseName);
    }
}
