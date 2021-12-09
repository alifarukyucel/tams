package nl.tudelft.sem.template.course.controllers;

import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.models.CourseModel;
import nl.tudelft.sem.template.course.security.AuthManager;
import nl.tudelft.sem.template.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An API endpoint manager for functionality related to the Course object.
 * It connects requests made from the client (redirected through the API Gateway)
 * to the server services, specifically to CourseService.
 *
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 01/12/2021, 14:15
 */
@RestController
@RequestMapping("course")
public class CourseController {

    private final transient AuthManager authManager;

    @Autowired
    private final CourseService courseService;

    Date date = new Date();

    /**
     * Instantiates a new Course controller.
     *
     * @param authManager   Spring Security component used to authenticate and authorize the user
     * @param courseService the course service
     */
    public CourseController(AuthManager authManager, CourseService courseService) {
        this.authManager = authManager;
        this.courseService = courseService;
    }

    // ------------------------------ Getters -----------------------------------

    /**
     * Gets course by id.
     *
     * @param id            id of course
     * @return the course found in the database with the given id
     */
    @GetMapping("{id}") // course/id
    public Course getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    /**
     * Gets whether a user is a responsible lecturer for the given course.
     *
     * @param netId             id of user
     * @param courseId          id of course
     * @return the course found in the database with the given id
     */
    @GetMapping("lecturer/{netId}/{courseId}") // course/lecturer/{netId}/{courseId}
    public boolean isResponsibleLecturer(@PathVariable String netId, @PathVariable String courseId) {
        return courseService.isResponsibleLecturer(netId, courseId);
    }


    // ------------------------------ Setters -----------------------------------

    /**
     * POST endpoint that saves the given course to the database. The CourseModel object is sent
     * through a POST request body in a JSON format.
     * Throws 409 Conflict upon already existing id
     *
     * @param courseModel   the course to be created
     * @return the course returned from the database (with a manually-assigned id)
     */
    @PostMapping(value = "create", consumes = "application/json") // course/create
    Course createCourse(@RequestBody CourseModel courseModel) {
        Course course = new Course(courseModel.getId(), courseModel.getStartDate(), courseModel.getName(),
                courseModel.getDescription(), courseModel.getNumberOfStudents(),
                new ArrayList<>(List.of(authManager.getNetid())));
        return courseService.createCourse(course);
    }

    /**
     * Updates the description of the Course object that is sent through the body of the PUT request.
     * The Course is found in the database by the given course id.
     *
     * @param id the course id
     */
    @PutMapping("update-description/{id}") // course/update-description/id
    void updateContentById(@PathVariable int id,
                           @RequestBody String description) {
        courseService.updateDescriptionById(id, description);
    }

    /**
     * Updates the name of the Course object that is sent through the body of the PUT request.
     * The Course is found in the database by the given course id.
     *
     * @param id       the course id
     * @param name the course to be updated
     */
    @PutMapping("update-name/{id}") // course/update-name/id
    void updateAuthorById(@PathVariable int id,
                          @RequestBody String name) {
        courseService.updateNameById(id, name);
    }


    // ---------------------------------- Deletions -------------------------------

    /**
     * Deletes Course found in database by course id.
     *
     * @param id the course id
     */
    @DeleteMapping("delete/{id}") // course/delete/id
    void deleteById(@PathVariable String id) {
        courseService.deleteById(id);
    }
}
