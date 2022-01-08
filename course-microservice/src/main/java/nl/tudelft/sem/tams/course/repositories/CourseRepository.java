package nl.tudelft.sem.tams.course.repositories;

import nl.tudelft.sem.tams.course.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CourseRepository utilizes JPA persistence library to contact the H2 database.
 *
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 30/11/2021, 17:16
 */
public interface CourseRepository extends JpaRepository<Course, String> {
    Course getById(String id);  // returns null when no value is present,
    // instead of an Optional<Course> as findById does
}
