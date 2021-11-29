package nl.tudelft.sem.template.authentication.controllers;

import nl.tudelft.sem.template.authentication.models.LoginRequestModel;
import nl.tudelft.sem.template.authentication.models.LoginResponseModel;
import nl.tudelft.sem.template.authentication.models.RegistrationRequestModel;
import nl.tudelft.sem.template.authentication.security.TokenGenerator;
import nl.tudelft.sem.template.authentication.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsersController {
    private final transient UserService userDetailsService;

    private final transient AuthenticationManager authenticationManager;

    private final transient TokenGenerator tokenGenerator;

    /**
     * Instantiates a new UsersController.
     *
     * @param userDetailsService    the user details service
     * @param authenticationManager the authentication manager
     * @param tokenGenerator        the token generator
     */
    public UsersController(UserService userDetailsService,
                           AuthenticationManager authenticationManager,
                           TokenGenerator tokenGenerator) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * Endpoint for logging in.
     *
     * @param request The login model
     * @return JWT token if the login is successful
     * @throws Exception if the user does not exist or the password is incorrect
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseModel> createToken(@RequestBody LoginRequestModel request)
            throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNetid(),
                        request.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getNetid());
        final String jwtToken = tokenGenerator.generateJwtToken(userDetails);
        return ResponseEntity.ok(new LoginResponseModel(jwtToken));
    }

    /**
     * Endpoint for registration.
     *
     * @param request The registration model
     * @return 200 OK if the registration is successful
     * @throws Exception if a user with this netid already exists
     */
    @PostMapping("/register")
    public ResponseEntity createToken(@RequestBody RegistrationRequestModel request)
            throws Exception {

        try {
            userDetailsService.createUser(request.getNetid(), request.getPassword());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}
