package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.entities.Application.createPendingApplication;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.models.ExtendedApplicationRequestModel;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class ApplicationController {
    private final transient AuthManager authManager;

    @Autowired
    private transient ApplicationService applicationService;

    public ApplicationController(AuthManager authManager) {
        this.authManager = authManager;
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
     * API Endpoint for retreiving all applications that are still pending as a JSON
     * These applications also contain their average rating as a TA, retreived from the TA-service
     *
     * @param courseId The courseId as String
     * @return The list of pending applications (extended with rating) for that course.
     */
    @GetMapping("/getPendingApplications/{courseId}")
    public ResponseEntity<List<ExtendedApplicationRequestModel>> getPendingApplications(@PathVariable String courseId) {
        List<Application> applications = applicationService.findAllByCourseAndStatus(courseId, ApplicationStatus.PENDING);
        var extendedApplications = applicationService.extendWithRating(applications);

        return ResponseEntity.ok(extendedApplications);
    }
}
