package nl.tudelft.sem.template.ta.controllers;

import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.services.HourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hours")
public class HourController {
    private final transient AuthManager authManager;
    private final transient HourService hourService;

    /**
     * Instantiates a new HourController.
     *
     * @param authManager the authentication manager
     * @param hourService the hour service
     */
    public HourController(AuthManager authManager, HourService hourService) {
        this.authManager = authManager;
        this.hourService = hourService;
    }


    @GetMapping("/test")
    public ResponseEntity<String> createToken() {
        return ResponseEntity.ok("Success! You are logged in with netid: "
                + authManager.getNetid());
    }
}
