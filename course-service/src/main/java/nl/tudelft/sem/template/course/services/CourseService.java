package nl.tudelft.sem.template.course.services;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
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
        if (courseRepository.getById(id) == null) {
            throw new NoSuchElementException("The course you're looking for doesn't exist.");
        }
        return courseRepository.getById(id);
    }

    // --------------------- Setters -------------------------

    /**
     * Saves the given course to the repository.
     *
     * @param course the saved course
     * @return the saved course
     */
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    // -------------------- Deletions ------------------------

}
