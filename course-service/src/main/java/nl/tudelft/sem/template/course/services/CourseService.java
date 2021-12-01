package nl.tudelft.sem.template.course.services;

import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
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
    public Optional<Course> getCourseById(int id) {
        return Optional.ofNullable(courseRepository.getById(id));
    }

    // Setters
    @Transactional
    public Course save(Course course) { // this method can also be used as an update method.
        return courseRepository.save(course);
    }

    public void updateDescriptionById(int id, String description) {
        courseRepository.updateDescriptionById(id, description);
    }

    public void updateNameById(int id, String name) {
        courseRepository.updateNameById(id, name);
    }

    // Deletions
    public void deleteById(int id) {
        courseRepository.deleteById(id);
    }

}
