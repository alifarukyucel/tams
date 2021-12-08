package nl.tudelft.sem.template.ta.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.template.ta.models.ContractRequestModel;
import nl.tudelft.sem.template.ta.models.ContractResponseModel;
import nl.tudelft.sem.template.ta.models.CourseRequestModel;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("contracts")
public class ContractController {
    private final transient AuthManager authManager;
    private final transient ContractService contractService;

    /**
     * Instantiates a new ContractController.
     *
     * @param authManager the authentication manager
     * @param contractService the contract service
     */
    public ContractController(AuthManager authManager, ContractService contractService) {
        this.authManager = authManager;
        this.contractService = contractService;
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
        String netId = ensureLoggedIn();

        try {
            Contract contract = contractService.getContract(
                netId, request.getCourse());

            contract.setSigned(!contract.getSigned() || !request.isAccept());  // keep value true.
            contractService.save(contract);
            return ResponseEntity.ok().build();

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Endpoint for fetching all the contracts of a signed in user.
     *
     * @return a list of contracts that belong to the signed in user.
     * @throws ResponseStatusException if user is not signed in or no contracts can be found.
     */
    @GetMapping("/mine")
    public ResponseEntity<List<ContractResponseModel>> getSignedInUserContracts()
            throws ResponseStatusException {
        String netId = ensureLoggedIn();
        return findContractBy(netId);
    }

    /**
     * Endpoint for fetching a contract with a given course of a signed in user.
     *
     * @return a singleton list containing a contract with the requested course code that belong to the signed in user.
     * @throws ResponseStatusException if user is not signed in or no contracts can be found.
     */
    @PostMapping("/mine")
    public ResponseEntity<List<ContractResponseModel>> getSignedInUserContractByCourse(@RequestBody CourseRequestModel request)
            throws ResponseStatusException {

        String netId = ensureLoggedIn();
        return findContractBy(netId, request.getCourse());
    }

    /**
     * Endpoint for fetching all the contracts of a certain user.
     * Needs a netId of the requested contract and
     *
     * @return a list of contracts that belong to the signed in user.
     * @throws ResponseStatusException if netId is not given or when no contracts can not be found.
     */
    @PostMapping("/get")
    public ResponseEntity<List<ContractResponseModel>> getUserContracts(@RequestBody ContractRequestModel request)
            throws ResponseStatusException {

        // TODO: Implement authentication checking in next sprint.
        // TODO: Not everyone should be allowed to make this request. Only responsible lecturers should be allowed to fetch everyones contracts.
        // String userNetId = ensureLoggedIn();
        // authManager.isResponsibleLecturer(userNetId) .. or something like that.

        return findContractBy(request.getNetId(), request.getCourse());
    }

    /**
     * Helper method that will handle requests that want to fetch a contract with a certain netId or courseId.
     *
     * @param netId the netId of the returned contracts
     * @param courseId the courseId of the returned contracts
     * @return a list of contracts
     * @throws ResponseStatusException if no contracts have been found.
     */
    private ResponseEntity<List<ContractResponseModel>> findContractBy(String netId, String courseId) throws ResponseStatusException  {

        try {
            List<Contract> contracts = contractService.getContractsBy(netId, courseId);
            List<ContractResponseModel> response = new ArrayList<ContractResponseModel>();
            for (var contract : contracts) {
                response.add(contract.toResponseModel());
            }

            return ResponseEntity.ok(response);
        } catch(NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Helper method that will handle requests that want to fetch a contract with a certain netId.
     * @param netId the netId of the returned contracts
     * @return a list of contracts
     * @throws ResponseStatusException if no contracts have been found.
     */
    private ResponseEntity<List<ContractResponseModel>> findContractBy(String netId) throws ResponseStatusException {
        return findContractBy(netId, null);
    }

    /**
     * We should ensure that the user is logged in when doing some request.
     *
     * @return the netId of the logged in user.
     * @throws ResponseStatusException if the user is not logged in.
     */
    private String ensureLoggedIn() throws ResponseStatusException {
        String netId = authManager.getNetid();
        if (netId == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to sign in before making this request.");
        return netId;
    }

}
