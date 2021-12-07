package nl.tudelft.sem.template.ta.controllers;


import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/test")
    public ResponseEntity<String> createToken() {
        return ResponseEntity.ok("Success! You are logged in with netid: "
                + authManager.getNetid());
    }
}
