package nl.tudelft.sem.tams.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.course.entities.Course;
import nl.tudelft.sem.tams.course.repositories.CourseRepository;
import nl.tudelft.sem.tams.course.services.CourseService;
import nl.tudelft.sem.tams.course.services.exceptions.ConflictException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * Unit tests for CourseService.
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

    final String testCourseId = "CSE2115";
    final LocalDateTime testStartDate = LocalDateTime.of(2021, 12, 1, 0, 0);
    final String testCourseName = "SEM";
    final String testDescription = "swe methods";
    final int testNumberOfStudents = 300;
    final String expectedResponsibleLecturer = "fmulder";
    ArrayList<String> responsibleLecturers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        responsibleLecturers = new ArrayList<>();
    }

    @Test
    public void getExistingCourse() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        Course expected = courseService.getCourseById(testCourseId);

        // Assert
        assertThat(course.getId()).isNotNull();
        assertThat(course).isEqualTo(expected);
    }

    @Test
    public void getNonExistingCourse() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);

        // act
        ThrowableAssert.ThrowingCallable actionNull = () -> courseService.getCourseById(testCourseId);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionNull);
    }

    @Test
    public void createCourse_NoExistingCourseInDatabase() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);

        // Act
        courseService.createCourse(course);

        // Assert
        Course expected = courseRepository.getById(course.getId());
        assertThat(course.getId()).isNotNull();
        assertThat(course).isEqualTo(expected);
    }

    @Test
    public void createCourse_ExistingCourseInDatabase_throwsConflictException() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ThrowableAssert.ThrowingCallable actionConflict = () -> courseService.createCourse(course);

        // Assert
        assertThatExceptionOfType(ConflictException.class).isThrownBy(actionConflict);
    }

    @Test
    public void isResponsibleLecturer() {
        // Arrange
        responsibleLecturers.add(expectedResponsibleLecturer);
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        boolean isResponsible = courseService.isResponsibleLecturer(expectedResponsibleLecturer, course.getId());

        // Assert
        assertThat(isResponsible).isTrue();
    }

    @Test
    public void isResponsibleLecturer_multipleLecturers() {
        // Arrange
        responsibleLecturers.add(expectedResponsibleLecturer);
        responsibleLecturers.add("annibalePanichella");
        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        boolean isResponsible = courseService.isResponsibleLecturer(expectedResponsibleLecturer, course.getId());

        // Assert
        assertThat(isResponsible).isTrue();
    }

    @Test
    public void isNotResponsibleLecturer_differentLecturer_throwsNoSuchElementException() {
        // Arrange
        String wrongLecturer = "someOtherGuy";
        responsibleLecturers.add(wrongLecturer);

        Course course = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ThrowableAssert.ThrowingCallable action = () ->
                courseService.isResponsibleLecturer(expectedResponsibleLecturer, course.getId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(action);
    }

    @Test
    public void isNotResponsibleLecturer_differentCourse_throwsNoSuchElementException() {
        // Arrange
        responsibleLecturers.add(expectedResponsibleLecturer);

        ArrayList<String> falseResponsibleLecturers = new ArrayList<>();
        falseResponsibleLecturers.add("someOtherGuy");

        Course courseWithWrongTeacher = new Course("CourseWithWrongTeacher", testStartDate, testCourseName, testDescription,
                testNumberOfStudents, falseResponsibleLecturers);

        Course courseWithCorrectLecturer = new Course(testCourseId, testStartDate, testCourseName, testDescription,
                testNumberOfStudents, responsibleLecturers);

        courseRepository.save(courseWithWrongTeacher);
        courseRepository.save(courseWithCorrectLecturer);

        // Act
        ThrowableAssert.ThrowingCallable action = () ->
                courseService.isResponsibleLecturer(expectedResponsibleLecturer, courseWithWrongTeacher.getId());

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(action);
    }

    @Test
    public void isResponsibleLecturer_courseDoesNotExist_throwsNoSuchElementException() {
        // Arrange
        responsibleLecturers.add(expectedResponsibleLecturer);

        // Act
        ThrowableAssert.ThrowingCallable action = () ->
                courseService.isResponsibleLecturer(expectedResponsibleLecturer, testCourseId);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(action);
    }

    @Test
    public void addResponsibleLecturers_courseDoesNotExist_throwsNoSuchElementException() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        ThrowableAssert.ThrowingCallable action = () ->
                courseService.addResponsibleLecturers("courseThatDoesNotExist", expectedResponsibleLecturer);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(action);
    }

    @Test
    public void addResponsibleLecturers_doNotAddIfLecturerAlreadyExists() {
        // Arrange
        responsibleLecturers.add(expectedResponsibleLecturer);  // already exists
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        courseService.addResponsibleLecturers(course.getId(), expectedResponsibleLecturer);  // try to add it

        // Assert
        assertThat(courseRepository.getById(course.getId()).getResponsibleLecturers())
                .containsExactly(expectedResponsibleLecturer);  // no duplicates
    }

    @Test
    public void addResponsibleLecturers_addSingleLecturer() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        courseService.addResponsibleLecturers(course.getId(), expectedResponsibleLecturer);

        // Assert
        assertThat(courseRepository.getById(course.getId()).getResponsibleLecturers())
                .containsExactly(expectedResponsibleLecturer);
    }

    @Test
    public void addResponsibleLecturers_addMultipleLecturers_asMultipleArguments() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        courseService.addResponsibleLecturers(course.getId(), expectedResponsibleLecturer, "lecturer2");

        // Assert
        List<String> expectedResponsibleLecturers = courseRepository.getById(course.getId()).getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers)
                .containsExactlyInAnyOrder(expectedResponsibleLecturer, "lecturer2");
    }

    @Test
    public void addResponsibleLecturers_addMultipleLecturers_asList() {
        // Arrange
        Course course = new Course(testCourseId, testStartDate, testCourseName,
                testDescription, testNumberOfStudents, responsibleLecturers);
        courseRepository.save(course);

        // Act
        courseService.addResponsibleLecturers(course.getId(), List.of(expectedResponsibleLecturer, "lecturer2"));

        // Assert
        List<String> expectedResponsibleLecturers = courseRepository.getById(course.getId()).getResponsibleLecturers();
        assertThat(expectedResponsibleLecturers)
                .containsExactlyInAnyOrder(expectedResponsibleLecturer, "lecturer2");
    }

}
