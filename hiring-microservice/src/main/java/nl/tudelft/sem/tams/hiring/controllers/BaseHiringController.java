package nl.tudelft.sem.tams.hiring.controllers;

import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.services.HiringService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Abstract class for HiringControllers.
 */
public class BaseHiringController {
    protected final transient AuthManager authManager;
    protected final transient CourseInformation courseInformation;
    protected final transient HiringService taApplicationService;

    protected BaseHiringController(AuthManager authManager, CourseInformation courseInformation, HiringService taApplicationService) {
        this.authManager = authManager;
        this.courseInformation = courseInformation;
        this.taApplicationService = taApplicationService;
    }

    protected void checkIsResponsibleLecturer(String courseId) {
        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), courseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
