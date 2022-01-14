package nl.tudelft.sem.tams.hiring.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.models.RetrieveTeachingAssistantApplicationStatusModel;
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
 * A controller that contains application endpoints for students.
 */
@RestController
public class ApplicantHiringController extends BaseHiringController {

    /**
     * Instantiates a new ApplicantHiringController.
     *
     * @param authManager        the auth manager
     * @param hiringService the application service
     * @param courseInformation  the course information
     */
    public ApplicantHiringController(AuthManager authManager,
                                     HiringService hiringService,
                                     CourseInformation courseInformation) {
        super(authManager, courseInformation, hiringService);
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
            hiringService.checkAndSave(teachingAssistantApplication);
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
            TeachingAssistantApplication teachingAssistantApplication = hiringService
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
            if (hiringService.checkAndWithdraw(model.getCourseId(), model.getNetId())) {
                return ResponseEntity.ok().build();
            }
            throw new ResponseStatusException((HttpStatus.FORBIDDEN), "Withdrawing isn't possible at this moment");

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
