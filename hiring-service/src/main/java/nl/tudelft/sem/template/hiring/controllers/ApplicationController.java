package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.entities.Application.createPendingApplication;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class ApplicationController {
    private final transient AuthManager authManager;
    private final transient CourseInformation courseInformation;
    private transient ApplicationService applicationService;

    /**
     * Instantiates a new Application controller.
     *
     * @param authManager        the auth manager
     * @param applicationService the application service
     * @param courseInformation  the course information
     */
    public ApplicationController(AuthManager authManager, CourseInformation courseInformation,
                                 ApplicationService applicationService) {
        this.authManager = authManager;
        this.courseInformation = courseInformation;
        this.applicationService = applicationService;
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
     * API Endpoint for withdrawing an already existing application.
     *
     * @param model applicationKey for specific application
     * @return String informing if the application is withdrawn.
     */
    @DeleteMapping ("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody ApplicationKey model) {

        boolean success = applicationService.checkAndWithdraw(model.getCourseId(), model.getNetId());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Withdrawing isn't possible at this moment");
        }
    }
}
