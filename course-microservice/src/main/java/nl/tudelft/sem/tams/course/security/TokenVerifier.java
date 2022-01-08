package nl.tudelft.sem.tams.course.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenVerifier {
    @Value("${jwt.secret}")
    private transient String jwtSecret;

    public boolean validate(String token) {
        boolean isExpired = getClaims(token).getExpiration().before(new Date());
        return !isExpired;
    }

    public String parseNetid(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}
