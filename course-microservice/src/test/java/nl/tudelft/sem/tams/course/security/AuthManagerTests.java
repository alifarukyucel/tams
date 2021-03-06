package nl.tudelft.sem.tams.course.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthManagerTests {
    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    public void getNetidTest() {
        // Arrange
        String expected = "mmladenov";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        String actual = authManager.getNetid();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
