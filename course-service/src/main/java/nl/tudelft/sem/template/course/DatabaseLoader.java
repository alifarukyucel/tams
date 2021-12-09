package nl.tudelft.sem.template.course;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;


import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.repositories.CourseRepository;
import org.springframework.stereotype.Service;

/**
 * Loads stuff into the database (only for development).
 *
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 06/12/2021 17:25
 */
@Service
public class DatabaseLoader {
    Date date = new Date();

    public DatabaseLoader(CourseRepository courseRepository) {
        Course sem = new Course("CSE2115-2021", LocalDateTime.now(), "sem", "teaches you swe methods", 99999, new ArrayList<>());

        courseRepository.save(sem);
    }
}
