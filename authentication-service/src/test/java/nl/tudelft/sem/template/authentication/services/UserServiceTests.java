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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTests {

    @Autowired
    private transient UserService userService;

    @Autowired
    private transient PasswordEncoder mockPasswordEncoder;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void createUser_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";
        final String testPasswordHash = "hashedTestPassword";
        when(mockPasswordEncoder.encode(testPassword)).thenReturn(testPasswordHash);

        // Act
        userService.createUser(testUser, testPassword);

        // Assert
        AppUser savedUser = userRepository.findById(testUser).orElseThrow();

        assertThat(savedUser.getNetid()).isEqualTo(testUser);
        assertThat(savedUser.getPasswordHash()).isEqualTo(testPasswordHash);
    }
}

