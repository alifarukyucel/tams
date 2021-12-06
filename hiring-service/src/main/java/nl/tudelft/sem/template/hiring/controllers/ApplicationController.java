package nl.tudelft.sem.template.hiring.controllers;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.repository.ApplicationRepository;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ApplicationController {
    private final transient AuthManager authManager;

    @Autowired
    private ApplicationService applicationService;

    public ApplicationController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * API Endpoint for registering a new application from a student.
     */
    @PostMapping("apply")
    public ResponseEntity<String> apply(@RequestBody ApplicationRequestModel request) {
        Application application = new Application(request.getCourseId(), authManager.getNetid(),
                request.getGrade(), request.getMotivation());
        boolean success = applicationService.save(application);

        if (success) {
            return ResponseEntity.ok("Thanks for your application!");
        } else {
            return ResponseEntity.ok("Something went wrong!");
        }
    }
}
