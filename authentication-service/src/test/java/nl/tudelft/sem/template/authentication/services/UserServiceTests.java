package nl.tudelft.sem.template.authentication.services;

import nl.tudelft.sem.template.authentication.entities.AppUser;
import nl.tudelft.sem.template.authentication.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test", "mockPasswordEncoder"})
public class UserServiceTests {

    @Autowired
    private transient UserService userService;

    @Autowired
    private transient PasswordEncoder mockPasswordEncoder;

    @Autowired
    private transient UserRepository userRepository;


}

