package nl.tudelft.sem.template.ta.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.models.AcceptContractRequestModel;
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
        Contract contract;
        try {
            contract = contractService.getContract(authManager.getNetid(), request.getCourse());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        contract.setSigned(request.isAccept());

        // Um lol we also need a function to save >_<

        return ResponseEntity.ok().build();

    }


    @GetMapping("/test")
    public ResponseEntity<String> createToken() {
        return ResponseEntity.ok("Success! You are logged in with netId: "
                + authManager.getNetid());
    }
}
