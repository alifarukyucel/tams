package nl.tudelft.sem.template.course.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.course.entities.Course;
import nl.tudelft.sem.template.course.models.CourseResponseModel;
import nl.tudelft.sem.template.course.security.AuthManager;
import nl.tudelft.sem.template.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


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

    private final transient CourseService courseService;
    
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
    public ResponseEntity<CourseResponseModel> getCourseById(@PathVariable String id)
            throws NoSuchElementException {
        try {
            Course course = courseService.getCourseById(id);
            CourseResponseModel response = CourseResponseModel.fromCourse(course);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ------------------------------ Setters -----------------------------------


    // ---------------------------------- Deletions -------------------------------

}
