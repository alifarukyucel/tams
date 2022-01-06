package nl.tudelft.sem.template.authentication.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import nl.tudelft.sem.template.authentication.providers.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenGeneratorTests {
    private transient TokenGenerator tokenGenerator;
    private transient TimeProvider timeProvider;
    private transient Instant mockedTime = Instant.parse("2021-12-31T13:25:34.00Z");

    private final String secret = "testSecret123";

    private String netid = "andy";
    private UserDetails user;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        timeProvider = mock(TimeProvider.class);
        when(timeProvider.getCurrentTime()).thenReturn(mockedTime);

        tokenGenerator = new TokenGenerator(timeProvider);
        this.injectSecret(secret);

        user = new User(netid, "someHash", new ArrayList<>());
    }

    @Test
    public void generatedTokenHasCorrectIssuanceDate() {
        // Act
        String token = tokenGenerator.generateJwtToken(user);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getIssuedAt()).isEqualTo(mockedTime.toString());
    }

    @Test
    public void generatedTokenHasCorrectExpirationDate() {
        // Act
        String token = tokenGenerator.generateJwtToken(user);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getExpiration()).isEqualTo(mockedTime.plus(1, ChronoUnit.DAYS).toString());
    }

    @Test
    public void generatedTokenHasCorrectNetid() {
        // Act
        String token = tokenGenerator.generateJwtToken(user);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getSubject()).isEqualTo(netid);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setAllowedClockSkewSeconds(Integer.MAX_VALUE)
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = tokenGenerator.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(tokenGenerator, secret);
    }
}
