package nl.tudelft.sem.tams.hiring.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.services.HiringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Abstract class for HiringControllers.
 */
public abstract class BaseHiringController {
    protected final transient AuthManager authManager;
    protected final transient CourseInformation courseInformation;
    protected final transient HiringService hiringService;

    /**
     * Instantiates a new BaseHiringController.
     *
     * @param authManager        the auth manager
     * @param hiringService the application service
     * @param courseInformation  the course information
     */
    protected BaseHiringController(AuthManager authManager, CourseInformation courseInformation,
                                   HiringService hiringService) {
        this.authManager = authManager;
        this.courseInformation = courseInformation;
        this.hiringService = hiringService;
    }

    protected void checkIsResponsibleLecturer(String courseId) {
        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), courseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * ExceptionHandler for NoSuchElementExceptions thrown throughout the class.
     *
     * @param ex    NoSuchElementException
     * @return      404 NOT FOUND
     */
    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // although some
        // NoSuchElementExceptions were not returning a message, there is no way to distinguish when to return a message
        // and when not to. Therefore, taking the safe route and returning a description for the issue at hand for all.
    }
}
