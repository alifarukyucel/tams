package nl.tudelft.sem.tams.course.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.course.entities.Course;
import nl.tudelft.sem.tams.course.models.CourseAddResponsibleLecturerRequestModel;
import nl.tudelft.sem.tams.course.models.CourseCreationRequestModel;
import nl.tudelft.sem.tams.course.models.CourseResponseModel;
import nl.tudelft.sem.tams.course.security.AuthManager;
import nl.tudelft.sem.tams.course.services.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


/**
 * An API endpoint manager for functionality related to the Course object.
 * It connects requests made from the client (redirected through the API Gateway)
 * to the server services, specifically to CourseService.
 *
 * @created 01/12/2021, 14:15
 */
@RestController
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
    @GetMapping("/{id}") // id
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

    /**
     * Gets whether a user is a responsible lecturer for the given course.
     *
     * @param netId             id of user
     * @param courseId          id of course
     * @return 200 OK with true if user is responsible lecturer of the course
     *         200 OK with false if not.
     */
    @GetMapping("{courseId}/lecturer/{netId}") // {courseId}/lecturer/{netId}
    public ResponseEntity<Boolean> isResponsibleLecturer(@PathVariable String netId,
                                                         @PathVariable String courseId) {
        try {
            courseService.isResponsibleLecturer(netId, courseId);
            return ResponseEntity.ok(true);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(false);
        }
    }

    // ------------------------------ Setters -----------------------------------

    /**
     * POST endpoint that saves the given course to the database. The CourseCreationRequestModel
     * object is sent through a POST request body in a JSON format.
     * Throws 409 Conflict upon already existing id
     *
     * @param courseModel   the course to be created
     * @return the course returned from the database (with a manually-assigned id)
     */
    @PostMapping(value = "/create", consumes = "application/json") // create
    public ResponseEntity<CourseResponseModel> createCourse(@RequestBody CourseCreationRequestModel courseModel)
            throws ResponseStatusException {
        Course course = new Course(courseModel.getId(),
                courseModel.getStartDate(), courseModel.getName(),
                courseModel.getDescription(), courseModel.getNumberOfStudents(),
                new ArrayList<>(List.of(authManager.getNetid())));
        courseService.createCourse(course);
        CourseResponseModel response = CourseResponseModel.fromCourse(course);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT endpoint that adds the given netId as a responsible lecturer to the given course.
     *
     * @param courseId          Id of the course to add responsible lecturers to
     * @param netId             NetId of the responsible lecturer to be added
     * @return                  403 FORBIDDEN if the requesting user is not a responsible lecturer
     *                          404 NOT FOUND if the course does not exist
     *                          200 OK if given netId is successfully added
     */
    @PutMapping(value = "{courseId}/addLecturer/{netId}")   // {courseId}/addLecturer/{netId}
    public ResponseEntity<String> addResponsibleLecturers(@PathVariable String courseId,
                                                          @PathVariable String netId) {
        try {
            courseService.isResponsibleLecturer(authManager.getNetid(), courseId);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You should be a responsible lecturer to execute this operation.");
        }
        courseService.addResponsibleLecturers(courseId, netId); // this can't throw NoSuchElementException since
        // whether a course exists or not is already checked by isResponsibleLecturer.
        return ResponseEntity.ok().build();
    }

    /**
     * PUT endpoint that adds the given netIds as responsible lecturers to the given course.
     * Effectively replaces the existing course's responsible lecturers field with the new one.
     *
     * @param courseId          Id of the course to add responsible lecturers to
     * @param model             Course object with updated responsible lecturers
     * @return                  403 FORBIDDEN if the requesting user is not a responsible lecturer
     *                          404 NOT FOUND if the course does not exist
     *                          200 OK if given netId is successfully added
     */
    @PutMapping(value = "{courseId}/addLecturer/")   // {courseId}/addLecturer/
    public ResponseEntity<String> addResponsibleLecturers(@PathVariable String courseId,
                                                          @RequestBody CourseAddResponsibleLecturerRequestModel model) {
        try {
            courseService.isResponsibleLecturer(authManager.getNetid(), courseId);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You should be a responsible lecturer to execute this operation.");
        }
        courseService.addResponsibleLecturers(courseId, model.getResponsibleLecturers()); // this can't throw
        // NoSuchElementException since whether a course exists or not is already checked by isResponsibleLecturer.
        return ResponseEntity.ok().build();
    }

    // ---------------------------------- Deletions -------------------------------

}
