package nl.tudelft.sem.tams.hiring.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.tams.hiring.models.RetrieveTeachingAssistantApplicationStatusModel;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationAcceptRequestModel;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationRequestModel;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.services.HiringService;
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
 * The HiringController.
 */
@RestController
public class HiringController {
    private final transient AuthManager authManager;

    private final transient HiringService taApplicationService;
    private final transient CourseInformation courseInformation;

    /**
     * Instantiates a new HiringController.
     *
     * @param authManager        the auth manager
     * @param taApplicationService the application service
     * @param courseInformation  the course information
     */
    public HiringController(AuthManager authManager,
                            HiringService taApplicationService,
                            CourseInformation courseInformation) {
        this.authManager = authManager;
        this.taApplicationService = taApplicationService;
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
    public ResponseEntity<String> apply(@RequestBody TeachingAssistantApplicationRequestModel request) {
        TeachingAssistantApplication teachingAssistantApplication = TeachingAssistantApplication.createPendingApplication(
                request, authManager.getNetid());

        try {
            taApplicationService.checkAndSave(teachingAssistantApplication);
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
    public ResponseEntity<RetrieveTeachingAssistantApplicationStatusModel> getStatusByCourse(@PathVariable String course) {
        try {
            TeachingAssistantApplication teachingAssistantApplication = taApplicationService
                    .get(course, authManager.getNetid());
            RetrieveTeachingAssistantApplicationStatusModel status = RetrieveTeachingAssistantApplicationStatusModel
                    .fromApplication(teachingAssistantApplication);

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
    public ResponseEntity<String> withdraw(@RequestBody TeachingAssistantApplicationKey model) {

        try {
            if (taApplicationService.checkAndWithdraw(model.getCourseId(), model.getNetId())) {
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
    public ResponseEntity<String> reject(@RequestBody TeachingAssistantApplicationKey model) {

        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), model.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        try {
            this.taApplicationService.reject(model.getCourseId(), model.getNetId());
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
    public ResponseEntity<String> accept(@RequestBody TeachingAssistantApplicationAcceptRequestModel model) {

        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), model.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        try {
            this.taApplicationService.accept(model.getCourseId(), model.getNetId(), model.getDuties(),
                    model.getMaxHours());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * API Endpoint for retrieving all applications that are still pending as a JSON.
     * These applications also contain their average rating as a TA, retreived from the TA-microservice.
     *
     * @param courseId The courseId as String.
     * @return The list of pending applications (extended with rating) for that course.
     * @throws ResponseStatusException 403 if the user is not a responsible lecturer for the course
     */
    @GetMapping("/applications/{courseId}/pending")
    public ResponseEntity<List<PendingTeachingAssistantApplicationResponseModel>> getPendingApplications(
            @PathVariable String courseId) {
        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), courseId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TeachingAssistantApplication> teachingAssistantApplications = taApplicationService
                .findAllByCourseAndStatus(courseId, ApplicationStatus.PENDING);
        var extendedApplications = taApplicationService
                .extendWithRating(teachingAssistantApplications);

        return ResponseEntity.ok(extendedApplications);
    }

    /**
     * API Endpoint for retrieving the "best" X pending, recommended TA-candidates for a given course.
     * Definition of "best" is determined and explained in the PendingApplicationResponseModel class
     *
     * @param courseId  The courseId as a String
     * @param amount    The amount of candidates to fetch
     * @return  A list of X pending applications (extended with rating) sorted by recommendation
     * @throws ResponseStatusException 403 if the user is not a responsible lecturer for the course
     */
    @GetMapping("/applications/{courseId}/recommended/{amount}")
    public ResponseEntity<List<PendingTeachingAssistantApplicationResponseModel>> getRecommendedApplications(
            @PathVariable String courseId, @PathVariable int amount) {
        if (!courseInformation.isResponsibleLecturer(authManager.getNetid(), courseId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (amount <= 0) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<TeachingAssistantApplication> applications = taApplicationService.findAllByCourseAndStatus(
                courseId, ApplicationStatus.PENDING);
        var extendedApplications = taApplicationService.extendWithRating(applications);
        Collections.sort(extendedApplications);

        if (amount > extendedApplications.size()) {
            amount = extendedApplications.size();
        }
        return ResponseEntity.ok(extendedApplications.subList(0, amount));
    }
}
