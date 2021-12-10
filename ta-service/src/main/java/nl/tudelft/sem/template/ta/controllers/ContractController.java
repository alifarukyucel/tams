package nl.tudelft.sem.template.ta.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.template.ta.models.ContractResponseModel;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<ContractResponseModel>>
        getSignedInUserContractByCourse(@PathVariable String course)
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
    public ResponseEntity<List<ContractResponseModel>>
        getUserContracts(@PathVariable String course, @PathVariable String netId)
        throws ResponseStatusException {

        boolean authorized = courseInformation
                            .isResponsibleLecturer(authManager.getNetid(), course);

        if (!authManager.getNetid().equals(netId) && !authorized) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return findContractBy(netId, course);
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
    private ResponseEntity<List<ContractResponseModel>>
        findContractBy(String netId, String courseId) throws ResponseStatusException  {
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
    private ResponseEntity<List<ContractResponseModel>>
        findContractBy(String netId) throws ResponseStatusException {
        return findContractBy(netId, null);
    }

}
