package nl.tudelft.sem.template.ta.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.template.ta.models.ContractResponseModel;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<ContractResponseModel>> fetch()
            throws ResponseStatusException {
        String netId = ensureLoggedIn();

        try {
            List<Contract> contracts = contractService.getContractsOfNetID(netId);
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
