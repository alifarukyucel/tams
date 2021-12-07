package nl.tudelft.sem.template.ta.controllers;

import nl.tudelft.sem.template.ta.security.AuthManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hours")
public class HourController {
    private final transient AuthManager authManager;

    public HourController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @GetMapping("/test")
    public ResponseEntity<String> createToken() {
        return ResponseEntity.ok("Success! You are logged in with netid: "
                + authManager.getNetid());
    }
}
