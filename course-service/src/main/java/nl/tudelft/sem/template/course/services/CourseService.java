package nl.tudelft.sem.template.course.services;

import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import nl.tudelft.sem.template.course.services.exceptions.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * CourseService is called by CourseController and performs functionality that
 * implements business logic.
 *
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 01/12/2021, 13:06
 */
@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;

    // Getters
    public Course getCourseById(String id) {
        return courseRepository.getById(id);
    }

    // Setters
    @Transactional
    public Course createCourse(Course course) { // this method can also be used as an update method.
        String courseId = course.getId();
        if (getCourseById(courseId) != null) {
            throw new ConflictException("A course already exists with that id.");
        }
        return courseRepository.save(course);
    }

    public void updateDescriptionById(int id, String description) {
        courseRepository.updateDescriptionById(id, description);
    }

    public void updateNameById(int id, String name) {
        courseRepository.updateNameById(id, name);
    }

    // Deletions
    public void deleteById(String id) {
        courseRepository.deleteById(id);
    }

}
