package nl.tudelft.sem.template.ta.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.models.AcceptHoursRequestModel;
import nl.tudelft.sem.template.ta.models.HourResponseModel;
import nl.tudelft.sem.template.ta.models.SubmitHoursRequestModel;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.ContractService;
import nl.tudelft.sem.template.ta.services.HourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final transient ContractService contractService;

    /**
     * Instantiates a new HourController.
     *
     * @param authManager the authentication manager
     * @param hourService the hour service
     * @param courseInformation the course information service
     */
    public HourController(AuthManager authManager,
                          HourService hourService,
                          CourseInformation courseInformation,
                          ContractService contractService) {
        this.authManager = authManager;
        this.hourService = hourService;
        this.courseInformation = courseInformation;
        this.contractService = contractService;
    }

    /**
     * Endpoint for fetching the non reviewed hours of a certain course
     * Note that you need to be a responsible lecturer of the course to make this request.
     *
     * @params course
     * @return a singleton list containing non reviewed hour declaration that
     *          belongs to the requested user with the requested course code
     * @throws ResponseStatusException if netId is unauthorized.
     */
    @GetMapping("/open/{course}")
    public ResponseEntity<List<HourResponseModel>>
        getOpenHours(@PathVariable String course)
        throws ResponseStatusException {

        checkAuthorized(course);
        return findNonReviewedHoursBy(course, null);
    }

    /**
     * Endpoint for fetching the non reviewed hours of a certain user for a certain course
     * Note that you need to be a responsible lecturer of the course to request
     * non reviewed hours of other users than yourself.
     *
     * @params course
     * @params netId (not always the signed-in user)
     * @return a singleton list containing non reviewed hour declaration that
     *          belongs to the requested user with the requested course code
     * @throws ResponseStatusException if netId is unauthorized.
     */
    @GetMapping("/open/{course}/{netId}")
    public ResponseEntity<List<HourResponseModel>>
        getOpenHours(@PathVariable String course, @PathVariable String netId)
        throws ResponseStatusException {

        // Check if authorized when fetching someone elses data.
        if (!netId.equals(authManager.getNetid())) {
            checkAuthorized(course);
        }

        return findNonReviewedHoursBy(course, netId);
    }

    /**
     * Set a worked hour's status to approved.
     *
     * @param request The request containing
     * @return 200 OK if successful
     */
    @PutMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody AcceptHoursRequestModel request) {
        try {
            Contract contract = hourService.getAssociatedContract(request.getId());

            checkAuthorized(contract.getCourseId());
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
            Contract contract = contractService.getContract(
                authManager.getNetid(), request.getCourse());

            HourDeclaration hourDeclaration = HourDeclaration.builder()
                .workedTime(request.getWorkedTime())
                .reviewed(false)
                .approved(false)
                .contract(contract)
                .date(request.getDate())
                .desc(request.getDesc())
                .build();

            hourDeclaration = hourService.checkAndSave(hourDeclaration);

            return ResponseEntity.ok(hourDeclaration.getId());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Helper method that will handle requests that want to fetch
     * a non reviewed hour declaration with a certain netId or courseId.
     *
     * @param netId the netId of the returned hour declarations (may be null)
     * @param courseId the courseId of the returned hour declarations (required)
     * @return a list of hour declarations (empty if non were found)
     */
    private ResponseEntity<List<HourResponseModel>> findNonReviewedHoursBy(
        String courseId, String netId) {

        List<HourDeclaration> declarations = hourService
                                            .getNonReviewedHoursByCourseIdAndNetId(courseId, netId);
        List<HourResponseModel> response = declarations.stream().map(declaration ->
            HourResponseModel.fromHourDeclaration(declaration)
        ).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to check if the user is a responsible lecturer for a certain course.
     *
     * @param courseId the courseId the user needs to be a responsible lecturer for.
     * @throws ResponseStatusException if not a res
     */
    private void checkAuthorized(String courseId) throws ResponseStatusException {
        boolean authorized = courseInformation
            .isResponsibleLecturer(authManager.getNetid(), courseId);

        if (!authorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
