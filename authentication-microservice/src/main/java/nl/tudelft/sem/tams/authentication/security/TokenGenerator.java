package nl.tudelft.sem.tams.authentication.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import nl.tudelft.sem.tams.authentication.providers.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JWT Token generator.
 */
@Component
public class TokenGenerator {
    @Value("${jwt.secret}")
    private transient String jwtSecret;

    private final transient TimeProvider timeProvider;

    public TokenGenerator(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * Generate a JWT token for the provided user.
     * The token is valid for 24 hours.
     *
     * @param userDetails The user details
     * @return the JWT token
     */
    public String generateJwtToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(timeProvider.getCurrentTime().toEpochMilli()))
                .setExpiration(new Date(timeProvider.getCurrentTime().toEpochMilli() + 24 * 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }
}
