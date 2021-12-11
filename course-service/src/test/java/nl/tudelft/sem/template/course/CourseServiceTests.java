package nl.tudelft.sem.template.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import nl.tudelft.sem.template.course.services.CourseService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * Unit tests for CourseService
 *
 * @created 09/12/2021, 12:20
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CourseServiceTests {

    @Autowired
    private transient CourseService courseService;

    @Autowired
    private transient CourseRepository courseRepository;

    private final CourseRepository mockCourseRepository = Mockito.mock(CourseRepository.class);

    final static String testCourseID = "CSE2115";
    final static LocalDateTime testStartDate = LocalDateTime.of(2021, 12, 1, 0, 0);
    final static String testCourseName = "SEM";
    final static String testDescription = "swe methods";
    final static int testNumberOfStudents = 300;
    final static String responsibleLecturer = "fmulder";
    ArrayList<String> responsibleLecturers;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        responsibleLecturers = new ArrayList<>();
    }

    @Test
    public void createCourse_withValidData_worksCorrectly() {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course newCourse = new Course(testCourseID, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);

        // Act
        courseService.createCourse(newCourse);

        // Assert
        Course savedCourse = courseRepository.getById("CSE2115");
        assertThat(savedCourse.getId()).isEqualTo(testCourseID);
        assertThat(savedCourse.getName()).isEqualTo(testCourseName);
        assertThat(savedCourse.getDescription()).isEqualTo(testDescription);
        assertThat(savedCourse.getNumberOfStudents()).isEqualTo(testNumberOfStudents);
        assertThat(savedCourse.getResponsibleLecturers()).containsExactly(responsibleLecturer);
    }

    @Test
    public void createCourse_withExistingCourse_throwsConflictException() throws Exception {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course existingCourse = new Course(testCourseID,
                testStartDate, testCourseName, testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(existingCourse);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> courseService.createCourse(existingCourse);

        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);
    }

    @Test
    public void isLecturer_withValidData_worksCorrectly() {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);
        Course course = new Course(testCourseID, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Assert
        assertThat(courseService.isResponsibleLecturer(responsibleLecturer, course.getId())).isTrue();
    }

    @Test
    public void isLecturer_withDifferentLecturer_returnsFalse() {
        // Arrange
        responsibleLecturers.add("someOtherGuy");
        Course course = new Course(testCourseID, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        boolean isResponsibleLecturer = courseService.isResponsibleLecturer(responsibleLecturer, course.getId());

        // Assert
        assertThat(isResponsibleLecturer).isFalse();
    }

    @Test
    public void isLecturer_withDifferentCourse_returnsFalse() {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);

        ArrayList<String> FalseResponsiblelecturers = new ArrayList<>();
        FalseResponsiblelecturers.add("someOtherGuy");

        Course courseWithCorrectLecturer = new Course(testCourseID, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);

        Course course = new Course("CourseWithWrongTeacher", testStartDate, testCourseName, testDescription,
                testNumberOfStudents,FalseResponsiblelecturers);

        courseRepository.save(course);
        courseRepository.save(courseWithCorrectLecturer);

        // Act
        boolean isResponsibleLecturer = courseService.isResponsibleLecturer(responsibleLecturer, course.getId());

        // Assert
        assertThat(isResponsibleLecturer).isFalse();
    }

    @Test
    public void isLecturer_courseDoesNotExists_returnsFalse() {
        // Arrange
        responsibleLecturers.add(responsibleLecturer);

        // Act
        ThrowableAssert.ThrowingCallable action = () ->
                courseService.isResponsibleLecturer(responsibleLecturer, testCourseID);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(action);
    }
}
