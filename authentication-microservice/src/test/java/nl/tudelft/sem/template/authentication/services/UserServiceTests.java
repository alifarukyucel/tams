package nl.tudelft.sem.template.authentication.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.authentication.entities.AppUser;
import nl.tudelft.sem.template.authentication.repositories.UserRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Test
    public void createUser_withExistingUser_throwsException() {
        // Arrange
        final String testUser = "SomeUser";
        final String existingTestPassword = "password123";
        final String newTestPassword = "password456";

        AppUser existingAppUser = new AppUser();
        existingAppUser.setNetid(testUser);
        existingAppUser.setPasswordHash(existingTestPassword);
        userRepository.save(existingAppUser);

        // Act
        ThrowingCallable action = () -> userService.createUser(testUser, newTestPassword);

        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);

        AppUser savedUser = userRepository.findById(testUser).orElseThrow();

        assertThat(savedUser.getNetid()).isEqualTo(testUser);
        assertThat(savedUser.getPasswordHash()).isEqualTo(existingTestPassword);
    }

    @Test
    public void loadUserByUsername_withValidUser_returnsCorrectUser() {
        // Arrange
        final String testUser = "SomeUser";
        final String testPasswordHash = "password123Hash";

        AppUser appUser = new AppUser();
        appUser.setNetid(testUser);
        appUser.setPasswordHash(testPasswordHash);
        userRepository.save(appUser);

        // Act
        UserDetails actual = userService.loadUserByUsername(testUser);

        // Assert
        assertThat(actual.getUsername()).isEqualTo(testUser);
        assertThat(actual.getPassword()).isEqualTo(testPasswordHash);
    }

    @Test
    public void loadUserByUsername_withNonexistentUser_throwsException() {
        // Arrange
        final String testNonexistentUser = "SomeUser";

        final String testUser = "AnotherUser";
        final String testPasswordHash = "password123Hash";

        AppUser appUser = new AppUser();
        appUser.setNetid(testUser);
        appUser.setPasswordHash(testPasswordHash);
        userRepository.save(appUser);

        // Act
        ThrowingCallable action = () -> userService.loadUserByUsername(testNonexistentUser);

        // Assert
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(action);
    }
}
