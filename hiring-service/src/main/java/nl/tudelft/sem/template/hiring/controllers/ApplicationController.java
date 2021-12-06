package nl.tudelft.sem.template.hiring.controllers;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * API Endpoint for registering a new application from a student.
     */
    @PostMapping("apply")
    public ResponseEntity<String> apply(@RequestParam String courseId, @RequestParam int netId, @RequestParam float grade,
                                        @RequestBody String motivation, HttpServletRequest req) {

        Application application = new Application(courseId, netId, grade, motivation);
        applicationRepository.save(application);

        return ResponseEntity.ok("Thanks for your application!");
    }
}
