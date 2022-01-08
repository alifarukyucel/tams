package nl.tudelft.sem.tams.ta.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenVerifierTests {
    private transient TokenVerifier tokenVerifier;

    private final String secret = "testSecret123";

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        tokenVerifier = new TokenVerifier();
        this.injectSecret(secret);
    }

    @Test
    public void validateNonExpiredToken() {
        // Arrange
        String token = generateToken(secret, "mmladenov", -10_000_000, 10_000_000);

        // Act
        boolean actual = tokenVerifier.validate(token);

        // Assert
        assertThat(actual).isTrue();
    }

    @Test
    public void validateExpiredToken() {
        // Arrange
        String token = generateToken(secret, "mmladenov", -10_000_000, -5_000_000);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> tokenVerifier.validate(token);

        // Assert
        assertThatExceptionOfType(ExpiredJwtException.class)
                .isThrownBy(action);
    }

    @Test
    public void validateTokenIncorrectSignature() {
        // Arrange
        String token = generateToken("incorrectSecret", "mmladenov", -10_000_000, 10_000_000);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> tokenVerifier.validate(token);

        // Assert
        assertThatExceptionOfType(SignatureException.class)
                .isThrownBy(action);
    }

    @Test
    public void validateMalformedToken() {
        // Arrange
        String token = "blahblahblahtoken";

        // Act
        ThrowableAssert.ThrowingCallable action = () -> tokenVerifier.validate(token);

        // Assert
        assertThatExceptionOfType(MalformedJwtException.class)
                .isThrownBy(action);
    }

    @Test
    public void parseNetid() {
        // Arrange
        String expected = "mmladenov";
        String token = generateToken(secret, expected, -10_000_000, 10_000_000);

        // Act
        String actual = tokenVerifier.parseNetid(token);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    private String generateToken(String jwtSecret, String netid, long issuanceOffset, long expirationOffset) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(netid)
                .setIssuedAt(new Date(System.currentTimeMillis() + issuanceOffset))
                .setExpiration(new Date(System.currentTimeMillis() + expirationOffset))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = tokenVerifier.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(tokenVerifier, secret);
    }
}
