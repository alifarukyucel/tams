package nl.tudelft.sem.template.course.services;

import java.util.NoSuchElementException;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import nl.tudelft.sem.template.course.services.exceptions.ConflictException;
import org.springframework.stereotype.Service;


/**
 * CourseService is called by CourseController and performs functionality that
 * implements business logic.
 *
 * @created 01 /12/2021, 13:06
 */
@Service
public class CourseService {

    private final transient CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    // --------------------- Getters -------------------------

    /**
     * Gets course by id.
     *
     * @param id the id
     * @return the course by id
     * @throws NoSuchElementException if there is no such course
     */
    public Course getCourseById(String id) throws NoSuchElementException {
        Course course = courseRepository.getById(id);
        if (course == null) {
            throw new NoSuchElementException("The course you're looking for doesn't exist.");
        }
        return course;
    }

    /**
     * Checks if a user is the responsible lecturer for a given course.
     *
     * @param netId     the net id of the user to be checked
     * @param courseId  the course id of the course to be checked
     * @return true if netId is a responsible lecturer of the given course
     * @throws NoSuchElementException   Thrown if no course is found with the given id
     *     or if the user is not a lecturer for the given course.
     */
    public boolean isResponsibleLecturer(String netId, String courseId) throws NoSuchElementException {
        Course course = getCourseById(courseId);  // throws a NoSuchElementException if no course is found
        if (!course.getResponsibleLecturers().contains(netId)) {
            throw new NoSuchElementException("The user is not a lecturer for the given course.");
        }
        return true;
    }

    // --------------------- Setters -------------------------

    /**
     * Saves the given course to the repository.
     *
     * @param course                the course to be saved
     * @throws ConflictException    thrown if a course already exists with the same id
     */
    @Transactional
    public void createCourse(Course course) throws ConflictException {
        String courseId = course.getId();
        if (courseRepository.getById(courseId) != null) {
            throw new ConflictException("A course already exists with that id.");
        }
        courseRepository.save(course);
    }

    // -------------------- Deletions ------------------------

}
