package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.entities.Application.createPendingApplication;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


/**
 * The type Application controller.
 */
@RestController
public class ApplicationController {
    private final transient AuthManager authManager;

    private final transient ApplicationService applicationService;
    private final transient CourseInformation courseInformation;

    /**
     * Instantiates a new Application controller.
     *
     * @param authManager        the auth manager
     * @param applicationService the application service
     * @param courseInformation  the course information
     */
    public ApplicationController(AuthManager authManager, ApplicationService applicationService,
                                 CourseInformation courseInformation) {
        this.authManager = authManager;
        this.applicationService = applicationService;
        this.courseInformation = courseInformation;
    }

    /**
     * API Endpoint for registering a new application.
     *
     * @param request request to apply to become a TA ( courseId, the grade, and motivation)
     * @return String informing if the application is being considered.
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplicationRequestModel request) {
        Application application = createPendingApplication(
                request.getCourseId(),
                authManager.getNetid(),
                request.getGrade(),
                request.getMotivation());
        boolean success = applicationService.checkAndSave(application);


        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API Endpoint for rejecting an application.
     *
     * @param model The course id and the application id
     * @return 200 OK if the request is successful
     * @throws ResponseStatusException 403 if the user is not the responsible lecturer of the course
     * @throws ResponseStatusException 404 if the application does not exist
     * @throws ResponseStatusException 409 if the application is not pending
     */
    @PostMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody ApplicationKey model) {

        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), model.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        try {
            this.applicationService.reject(model.getCourseId(), model.getNetId());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().build();
    }
}
