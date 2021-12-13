package nl.tudelft.sem.template.ta.controllers;

import java.util.NoSuchElementException;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.models.AcceptHoursRequestModel;
import nl.tudelft.sem.template.ta.models.SubmitHoursRequestModel;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.HourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    /**
     * Endpoint where worked hours can be submitted.
     *
     * @param request the submit hours request model containing all necessary information to
     *                process the request.
     * @return 200 OK UUID of the submitted hours for future reference.
     *         409 if a conflict arose due to the request (declaration exceeded parameters)
     *         404 if the associated contract could not be found.
     */
    @PostMapping("/submit")
    public ResponseEntity<UUID> submit(@RequestBody SubmitHoursRequestModel request) {
        try {
            HourDeclaration hourDeclaration =
                hourService.createAndSaveDeclaration(authManager.getNetid(), request);

            return ResponseEntity.ok(hourDeclaration.getId());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
