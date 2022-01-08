package nl.tudelft.sem.tams.course.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";

    private final transient TokenVerifier tokenVerifier;

    public AuthFilter(TokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith(TOKEN_PREFIX)) {
            String token = tokenHeader.substring(TOKEN_PREFIX.length());

            try {
                if (tokenVerifier.validate(token)) {
                    String netid = tokenVerifier.parseNetid(token);
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            netid,
                            null, List.of() // no credentials and no authorities
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            } catch (ExpiredJwtException e) {
                System.err.println("Token has expired.");
            } catch (IllegalArgumentException | JwtException e) {
                System.err.println("Unable to parse JWT Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}