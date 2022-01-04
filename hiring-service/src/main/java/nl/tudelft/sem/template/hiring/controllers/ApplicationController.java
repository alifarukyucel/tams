package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication.createPendingApplication;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationAcceptRequestModel;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.models.PendingApplicationResponseModel;
import nl.tudelft.sem.template.hiring.models.RetrieveStatusModel;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @throws ResponseStatusException 403 when the application does not meet the requirements
     * @throws ResponseStatusException 404 when the course cannot be found
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplicationRequestModel request) {
        if (applicationService.hasReachedMaxApplication(authManager.getNetid())) {
            // It is not allowed to have more than 3 applications
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Maximum number of applications has been reached!");
        }
        TeachingAssistantApplication teachingAssistantApplication = createPendingApplication(
                request.getCourseId(),
                authManager.getNetid(),
                request.getGrade(),
                request.getMotivation());

        try {
            applicationService.checkAndSave(teachingAssistantApplication);
            return ResponseEntity.ok("Applied successfully");
        } catch (NoSuchElementException e) {
            //Thrown when the course is not found.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Endpoint for fetching the status of a specific course for a signed in user.
     *
     * @param course the course to get the status from
     * @return the status of that course
     */

    @GetMapping("/status/{course}")
    public ResponseEntity<RetrieveStatusModel> getStatusByCourse(@PathVariable String course) {
        try {
            TeachingAssistantApplication teachingAssistantApplication = applicationService
                    .get(course, authManager.getNetid());
            RetrieveStatusModel status = RetrieveStatusModel.fromApplication(teachingAssistantApplication);

            return ResponseEntity.ok(status);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    /**
     * API Endpoint for withdrawing an already existing application.
     *
     * @param model applicationKey for specific application
     * @return String informing if the application is withdrawn.
     */
    @DeleteMapping ("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody ApplicationKey model) {

        try {
            if (applicationService.checkAndWithdraw(model.getCourseId(), model.getNetId())) {
                return ResponseEntity.ok().build();
            }
            throw new ResponseStatusException((HttpStatus.FORBIDDEN), "Withdrawing isn't possible at this moment");

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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

    /**
     * API Endpoint for accepting an application.
     *
     * @param model The course id and the application id
     * @return 200 OK if the request is successful
     * @throws ResponseStatusException 403 if the user is not the responsible lecturer of the course
     * @throws ResponseStatusException 404 if the application does not exist
     * @throws ResponseStatusException 409 if the application is not pending or contract creation fails
     */
    @PostMapping("/accept")
    public ResponseEntity<String> accept(@RequestBody ApplicationAcceptRequestModel model) {

        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), model.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        try {
            this.applicationService.accept(model.getCourseId(), model.getNetId(), model.getDuties(),
                    model.getMaxHours());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * API Endpoint for retreiving all applications that are still pending as a JSON.
     * These applications also contain their average rating as a TA, retreived from the TA-service.
     *
     * @param courseId The courseId as String.
     * @return The list of pending applications (extended with rating) for that course.
     */
    @GetMapping("/applications/{courseId}/pending")
    public ResponseEntity<List<PendingApplicationResponseModel>> getPendingApplications(@PathVariable String courseId) {
        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), courseId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TeachingAssistantApplication> teachingAssistantApplications = applicationService
                .findAllByCourseAndStatus(courseId, ApplicationStatus.PENDING);
        var extendedApplications = applicationService
                .extendWithRating(teachingAssistantApplications);

        return ResponseEntity.ok(extendedApplications);
    }
}
