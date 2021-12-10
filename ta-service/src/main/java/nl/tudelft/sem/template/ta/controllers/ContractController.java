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
        try {
            Contract contract = contractService.getContract(authManager.getNetid(), request.getCourse());

            contract.setSigned(!contract.getSigned() || !request.isAccept());  // keep value true.
            contractService.save(contract);
            return ResponseEntity.ok().build();

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
     * Endpoint for fetching a contract of a signed-in user with a given course
     *
     * @return a singleton list containing a contract that belongs to the signed-in user with the requested course code
     * @throws ResponseStatusException if user is not signed-in or no contracts can be found.
     */
    @GetMapping("/{course}/mine")
    public ResponseEntity<List<ContractResponseModel>> getSignedInUserContractByCourse(@PathVariable String course)
            throws ResponseStatusException {
        return findContractBy(authManager.getNetid(), course);
    }

    /**
     * Endpoint for fetching all the contracts of a certain user.
     * Needs a netId of the requested contract and the courseId.
     *
     * @return a singleton list containing a contract that belongs to the requested user with the requested course code
     * @throws ResponseStatusException if netId is not given or when no contracts can not be found.
     */
    @PostMapping("/get")
    public ResponseEntity<List<ContractResponseModel>> getUserContracts(@RequestBody ContractRequestModel request)
            throws ResponseStatusException {

        // TODO: Implement authentication checking in next sprint.
        // TODO: Not everyone should be allowed to make this request. Only responsible lecturers should be allowed to fetch everyone's contracts.
        // String userNetId = ensureLoggedIn();
        // authManager.isResponsibleLecturer(userNetId) .. or something like that.

        return findContractBy(request.getNetId(), request.getCourse());
    }

    /**
     * Helper method that will handle requests that want to fetch a contract with a certain netId or courseId.
     *
     * @param netId the netId of the returned contracts (required)
     * @param courseId the courseId of the returned contracts (may be null)
     * @return a list of contracts
     * @throws ResponseStatusException if no contracts have been found.
     */
    private ResponseEntity<List<ContractResponseModel>> findContractBy(String netId, String courseId) throws ResponseStatusException  {
        try {
            List<Contract> contracts = contractService.getContractsBy(netId, courseId);
            List<ContractResponseModel> response = new ArrayList<ContractResponseModel>();
            for (var contract : contracts) {
                response.add(ContractResponseModel.fromContract(contract));
            }

            return ResponseEntity.ok(response);
        } catch(NoSuchElementException e) {
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
    private ResponseEntity<List<ContractResponseModel>> findContractBy(String netId) throws ResponseStatusException {
        return findContractBy(netId, null);
    }

}
