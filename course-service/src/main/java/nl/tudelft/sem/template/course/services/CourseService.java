package nl.tudelft.sem.template.course.services;

import java.util.NoSuchElementException;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import nl.tudelft.sem.template.course.services.exceptions.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * CourseService is called by CourseController and performs functionality that
 * implements business logic.
 *
 * @author Ali Faruk Yücel
 * @version 1.0
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
     * @throws NoSuchElementException the no such element exception
     */
    public Course getCourseById(String id) throws NoSuchElementException {
        if (courseRepository.getById(id) == null) {
            throw new NoSuchElementException("The course you're looking for doesn't exist.");
        }
        return courseRepository.getById(id);
    }

    /**
     * Checks if a user is the responsible lecturer for a given course.
     *
     * @param netId    the net id
     * @param courseId the course id
     * @return true if netId is a responsible lecturer of the given course
     */
    public boolean isResponsibleLecturer(String netId, String courseId) {
        Course course = courseRepository.getById(courseId);
        if (course == null) {
            throw new NoSuchElementException("The course you're looking for doesn't exist.");
        } else if (!course.getResponsibleLecturers().contains(netId)) {
            throw new NoSuchElementException("The user is not a lecturer for the given course.");
        }
        return course.getResponsibleLecturers().contains(netId);
    }

    // ------------------------- Setters ------------------------
    /**
     * Create a course.
     *
     * @param course the course
     * @throws ConflictException the conflict exception
     */
    @Transactional
    public void createCourse(Course course) throws ConflictException {
        String courseId = course.getId();
        if (courseRepository.getById(courseId) != null) {
            throw new ConflictException("A course already exists with that id.");
        }
        courseRepository.save(course);
    }

    /**
     * Update description by id.
     *
     * @param id          the id
     * @param description the description
     */
    public void updateDescriptionById(int id, String description) {
        courseRepository.updateDescriptionById(id, description);
    }

    /**
     * Update name by id.
     *
     * @param id   the id
     * @param name the name
     */
    public void updateNameById(int id, String name) {
        courseRepository.updateNameById(id, name);
    }

    // -------------------- Deletions ----------------
    /**
     * Delete course by id.
     *
     * @param id the id
     */
    public void deleteById(String id) {
        courseRepository.deleteById(id);
    }

}
