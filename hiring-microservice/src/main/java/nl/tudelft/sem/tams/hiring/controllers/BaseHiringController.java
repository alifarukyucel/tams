package nl.tudelft.sem.tams.hiring.controllers;

import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.services.HiringService;
import org.springframework.http.HttpStatus;
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
    protected BaseHiringController(AuthManager authManager, CourseInformation courseInformation, HiringService hiringService) {
        this.authManager = authManager;
        this.courseInformation = courseInformation;
        this.hiringService = hiringService;
    }

    protected void checkIsResponsibleLecturer(String courseId) {
        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), courseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
