package nl.tudelft.sem.template.ta.controllers;

import nl.tudelft.sem.template.ta.security.AuthManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractController {
    private final transient AuthManager authManager;

    public ContractController(AuthManager authManager) {
        this.authManager = authManager;
    }


    @GetMapping("/test")
    public ResponseEntity<String> createToken() {
        return ResponseEntity.ok("Success! You are logged in with netid: "
                + authManager.getNetid());
    }
}
