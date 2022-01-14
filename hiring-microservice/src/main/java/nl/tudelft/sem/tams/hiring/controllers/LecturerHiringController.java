package nl.tudelft.sem.tams.hiring.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationAcceptRequestModel;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.services.HiringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * A controller that contains application endpoints for lecturers.
 */
@RestController
public class LecturerHiringController extends BaseHiringController {

    private final transient HiringService taApplicationService;

    /**
     * Instantiates a new LecturerHiringController.
     *
     * @param authManager        the auth manager
     * @param taApplicationService the application service
     * @param courseInformation  the course information
     */
    public LecturerHiringController(AuthManager authManager,
                                    HiringService taApplicationService,
                                    CourseInformation courseInformation) {
        super(authManager, courseInformation);
        this.taApplicationService = taApplicationService;
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
        checkIsResponsibleLecturer(model.getCourseId());       //Throws ResponseStatusException 403

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
        checkIsResponsibleLecturer(model.getCourseId());       //Throws ResponseStatusException 403

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
        checkIsResponsibleLecturer(courseId);       //Throws ResponseStatusException 403

        List<PendingTeachingAssistantApplicationResponseModel> extendedApplications =
                taApplicationService.getExtendedPendingApplications(courseId, false, null);

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
        checkIsResponsibleLecturer(courseId);       //Throws ResponseStatusException 403

        if (amount <= 0) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<PendingTeachingAssistantApplicationResponseModel> extendedApplications =
                taApplicationService.getExtendedPendingApplications(courseId, true, amount);

        return ResponseEntity.ok(extendedApplications);
    }

}
