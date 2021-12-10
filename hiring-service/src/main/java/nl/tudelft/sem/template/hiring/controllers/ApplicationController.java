package nl.tudelft.sem.template.hiring.controllers;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.models.RetrieveStatusModel;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nl.tudelft.sem.template.hiring.entities.Application.createPendingApplication;

@RestController
public class ApplicationController {
    private final transient AuthManager authManager;

    private String status;

    @Autowired
    private transient ApplicationService applicationService;

    public ApplicationController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * API Endpoint for registering a new application
     *
     * @param request request to apply to become a TA (with the courseId, the grade and the motivation)
     * @return String informing if the application is being considered.
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplicationRequestModel request) {
        Application application = createPendingApplication(request.getCourseId(), authManager.getNetid(),
                request.getGrade(), request.getMotivation());
        boolean success = applicationService.checkAndSave(application);


        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for fetching the status of a specific course for a signed in user
     * @param status
     * @return
     */

    @GetMapping("/status")
    public ResponseEntity<String> getStatus(@RequestBody RetrieveStatusModel status) {

        return findStatus(authManager.getNetid(), courseId);
    }

}
