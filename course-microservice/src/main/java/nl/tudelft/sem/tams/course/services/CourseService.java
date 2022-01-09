package nl.tudelft.sem.tams.course.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.transaction.Transactional;
import nl.tudelft.sem.tams.course.entities.Course;
import nl.tudelft.sem.tams.course.repositories.CourseRepository;
import nl.tudelft.sem.tams.course.services.exceptions.ConflictException;
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

    /**
     * Add netIds as responsible lecturers to the given course.
     *
     * @param courseId                      Id of the course to add responsible lecturers to
     * @param netIds                        NetId(s) of the responsible lecturers to be added
     * @throws NoSuchElementException       if course does not exist
     */
    public void addResponsibleLecturers(String courseId, List<String> netIds)
            throws NoSuchElementException {
        Course course = getCourseById(courseId); // throws NoSuchElementException if course doesn't exist

        List<String> respLecturers = course.getResponsibleLecturers();
        respLecturers.addAll(netIds);
        Set<String> noDuplicates = new HashSet<>(respLecturers);
        course.setResponsibleLecturers(new ArrayList<>(noDuplicates));

        courseRepository.deleteById(courseId);
        courseRepository.save(course);
    }

    /**
     * Overloaded addResponsibleLecturers method that accepts a single or multiple netIds as parameters.
     *
     * @param courseId                      Id of the course to add responsible lecturers to
     * @param netIds                        NetId(s) of the responsible lecturers to be added
     * @throws NoSuchElementException       if course does not exist
     */
    public void addResponsibleLecturers(String courseId, String... netIds)
            throws NoSuchElementException {
        addResponsibleLecturers(courseId, List.of(netIds));
    }

    // -------------------- Deletions ------------------------

    /**
     * Remove netIds as responsible lecturers from the given course.
     *
     * @param courseId                      Id of the course to remove responsible lecturers from
     * @param netIds                        NetId(s) of the responsible lecturers to be removed
     * @throws NoSuchElementException       if course does not exist
     */
    public void removeResponsibleLecturers(String courseId, List<String> netIds) throws NoSuchElementException {
        Course course = getCourseById(courseId); // throws NoSuchElementException if course doesn't exist

        List<String> respLecturers = course.getResponsibleLecturers();
        respLecturers.removeAll(netIds);
        Set<String> noDuplicates = new HashSet<>(respLecturers);
        course.setResponsibleLecturers(new ArrayList<>(noDuplicates));

        courseRepository.deleteById(courseId);
        courseRepository.save(course);
    }

    /**
     * Overloaded removeResponsibleLecturers method that accepts a single or multiple netIds as parameters.
     *
     * @param courseId                      Id of the course to remove responsible lecturers from
     * @param netIds                        NetId(s) of the responsible lecturers to be removed
     * @throws NoSuchElementException       if course does not exist
     */
    public void removeResponsibleLecturers(String courseId, String... netIds)
            throws NoSuchElementException {
        removeResponsibleLecturers(courseId, List.of(netIds));
    }

}
