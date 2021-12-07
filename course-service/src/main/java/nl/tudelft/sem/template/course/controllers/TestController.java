package nl.tudelft.sem.template.course.controllers;

import nl.tudelft.sem.template.course.security.AuthManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final transient AuthManager authManager;

    public TestController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @GetMapping("/test")
    public ResponseEntity<String> createToken() {
        return ResponseEntity.ok("Success! You are logged in with netid: "
                + authManager.getNetid());
    }
}
