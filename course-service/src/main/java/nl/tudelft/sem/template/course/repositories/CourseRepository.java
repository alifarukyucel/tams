package nl.tudelft.sem.template.course.repositories;

import nl.tudelft.sem.template.course.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 30/11/2021, 17:16
 *
 * CourseRepository utilizes JPA persistence library to contact the H2 database.
 */
public interface CourseRepository extends JpaRepository<Course, Integer> {
    // Getters
    Course getById(int id);  // table is small enough that overhead is insignificant. We can change it so that
                             // it'll return only the properties/columns we request the most.

    // Setters
    Course save(Course course);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.description = ?2 WHERE c.id = ?1")
    void updateDescriptionById(int id, String description);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.name = ?2 WHERE c.id = ?1")
    void updateNameById(int id, String name);

    // Deletions
    void deleteById(int id);
}
