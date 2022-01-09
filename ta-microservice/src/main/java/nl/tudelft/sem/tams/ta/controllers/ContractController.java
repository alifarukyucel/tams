package nl.tudelft.sem.tams.ta.controllers;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.interfaces.CourseInformation;
import nl.tudelft.sem.tams.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.tams.ta.models.ContractResponseModel;
import nl.tudelft.sem.tams.ta.models.CreateContractRequestModel;
import nl.tudelft.sem.tams.ta.models.RateContractRequestModel;
import nl.tudelft.sem.tams.ta.security.AuthManager;
import nl.tudelft.sem.tams.ta.services.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("contracts")
public class ContractController {
    private final transient AuthManager authManager;
    private final transient ContractService contractService;
    private final transient CourseInformation courseInformation;

    /**
     * Instantiates a new ContractController.
     *
     * @param authManager the authentication manager
     * @param contractService the contract service
     */
    public ContractController(AuthManager authManager,
                              ContractService contractService,
                              CourseInformation courseInformation) {
        this.authManager = authManager;
        this.contractService = contractService;
        this.courseInformation = courseInformation;
    }

    /**
     * Endpoint for creating a new unsigned contract for a course.
     * Only a responsible lecturer from that course is allowed to make this request.
     * This request will be called from the Hiring Microservice.
     *
     * @param request a CreateContractRequestModel
     * @return 200 OK with ContractResponseModel if saving was a success.
     *         400 Bad Request if contract already exists, parameters are invalid,
     *         or no more TAs are allowed to be hired for the course.
     *         403 Forbidden if not a responsible lecturer for the course.
     */
    @PostMapping("/create")
    public ResponseEntity<ContractResponseModel> createContract(@RequestBody CreateContractRequestModel request)
        throws ResponseStatusException {

        checkAuthorized(request.getCourseId());

        try {
            Contract contract = contractService.createUnsignedContract(
                    request.getNetId(), request.getCourseId(), request.getMaxHours(), request.getDuties(),
                    request.getTaContactEmail());
            return ResponseEntity.ok(ContractResponseModel.fromContract(contract));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * Set a users contract signed status. This will allow them to begin working.
     *
     * @param request The request made to the service containing the approval of their contract.
     * @return 200 OK if operation successfully
     */
    @PutMapping("/sign")
    public ResponseEntity<String> sign(@RequestBody AcceptContractRequestModel request)
        throws ResponseStatusException {

        try {
            contractService.sign(authManager.getNetid(), request.getCourse());
            return ResponseEntity.ok().build();

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Endpoint for rating a TA's performance.
     *
     * @param request a RateContractRequestModel
     * @return 200 OK if rating was saved successfully.
     *         400 Bad Request if rating was invalid
     *         404 Not Found if contract has not been found
     *         403 Forbidden if not a responsible lecturer for the course.
     */
    @PostMapping("/rate")
    public ResponseEntity<String> rateContract(@RequestBody RateContractRequestModel request)
        throws ResponseStatusException {

        checkAuthorized(request.getCourseId());

        try {
            contractService.rate(request.getNetId(), request.getCourseId(), request.getRating());
            return ResponseEntity.ok("Successfully saved rating!");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{course}/set-hours/{hours}")
    public ResponseEntity<String> setWorkedHours(@PathVariable String course, @PathVariable int hours)
            throws ResponseStatusException {
        try {
            contractService.updateHours(authManager.getNetid(), course, hours);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalCallerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Endpoint for fetching all the contracts of a signed-in user.
     *
     * @return a list of contracts that belong to the signed-in user.
     * @throws ResponseStatusException if user is not signed-in or no contracts can be found.
     */
    @GetMapping("/mine")
    public ResponseEntity<List<ContractResponseModel>> getSignedInUserContracts()
            throws ResponseStatusException {
        return findContractBy(authManager.getNetid());
    }

    /**
     * Endpoint for fetching a contract of a signed-in user with a given course.
     *
     * @return a singleton list containing a contract that
     *          belongs to the signed-in user with the requested course code.
     * @throws ResponseStatusException if user is not signed-in or no contracts can be found.
     */
    @GetMapping("/{course}/mine")
    public ResponseEntity<List<ContractResponseModel>> getSignedInUserContractByCourse(
        @PathVariable String course)
            throws ResponseStatusException {
        return findContractBy(authManager.getNetid(), course);
    }

    /**
     * Endpoint for fetching the contract of a certain user for a certain course
     * Note that you need to be a responsible lecturer of the course
     * to request contracts other than the one you have.
     *
     * @params the course of the contract
     * @params the netId of the requested contract (not always the signed in user)
     * @return a singleton list containing a contract that
     *          belongs to the requested user with the requested course code
     * @throws ResponseStatusException if netId is not given or when no contracts can not be found.
     */
    @GetMapping("/{course}/{netId}")
    public ResponseEntity<List<ContractResponseModel>> getUserContracts(
        @PathVariable String course, @PathVariable String netId)
        throws ResponseStatusException {

        // Check if we are authorized as
        // responsible lecturer for this course.
        if (!authManager.getNetid().equals(netId)) {
            checkAuthorized(course);
        }

        return findContractBy(netId, course);
    }

    /**
     * Endpoint for fetching ratings of a list of netIds.
     * Note that this request is open to everyone that is signed in.
     *
     * @params netIds a comma seperated string containing netIds
     * @return a hashmap containing netId as key and average rating as value.
     * @throws ResponseStatusException if netIds is empty.
     */
    @GetMapping("/ratings")
    public ResponseEntity<Map<String, Double>> getRatings(@RequestParam List<String> netIds)
        throws ResponseStatusException {

        try {

            // Get the list of netIds from the request parameter.
            return ResponseEntity.ok(contractService.getAverageRatingOfNetIds(netIds));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Helper method that will handle requests that
     * want to fetch a contract with a certain netId or courseId.
     *
     * @param netId the netId of the returned contracts (required)
     * @param courseId the courseId of the returned contracts (may be null)
     * @return a list of contracts
     * @throws ResponseStatusException if no contracts have been found.
     */
    private ResponseEntity<List<ContractResponseModel>> findContractBy(
        String netId, String courseId) throws ResponseStatusException  {

        try {
            List<Contract> contracts = contractService.getContractsBy(netId, courseId);
            List<ContractResponseModel> response = contracts.stream().map(contract ->
                    ContractResponseModel.fromContract(contract)
                ).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Helper method that will handle requests that want to fetch a contract with a certain netId.
     *
     * @param netId the netId of the returned contracts
     * @return a list of contracts
     * @throws ResponseStatusException if no contracts have been found.
     */
    private ResponseEntity<List<ContractResponseModel>> findContractBy(
        String netId) throws ResponseStatusException {
        return findContractBy(netId, null);
    }


    /**
     * Helper method to check if the user is a responsible lecturer for a certain course.
     *
     * @param courseId the courseId the user needs to be a responsible lecturer for.
     * @throws ResponseStatusException if not a responsible lecturer
     */
    private void checkAuthorized(String courseId) throws ResponseStatusException {
        boolean authorized = courseInformation
            .isResponsibleLecturer(authManager.getNetid(), courseId);

        if (!authorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Only a responsible lecturer is allowed to make this request.");
        }
    }

}
