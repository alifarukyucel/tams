package nl.tudelft.sem.template.ta.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.models.AcceptHoursRequestModel;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.HourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("hours")
public class HourController {
    private final transient AuthManager authManager;
    private final transient HourService hourService;
    private final transient CourseInformation courseInformation;

    /**
     * Instantiates a new HourController.
     *
     * @param authManager the authentication manager
     * @param hourService the hour service
     * @param courseInformation the course information service
     */
    public HourController(AuthManager authManager,
                          HourService hourService,
                          CourseInformation courseInformation) {
        this.authManager = authManager;
        this.hourService = hourService;
        this.courseInformation = courseInformation;
    }

    /**
     * Set a declaration's status to approved.
     *
     * @param request The request containing what declaration to update.
     * @return 200 OK if successful
     */
    @PutMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody AcceptHoursRequestModel request) {
        try {
            Contract contract = hourService.getAssociatedContract(request.getId());

            if (!courseInformation.isResponsibleLecturer(authManager.getNetid(),
                contract.getCourseId())) {

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            hourService.approveHours(request.getId(), request.getAccept());

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
