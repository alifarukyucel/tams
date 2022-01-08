package nl.tudelft.sem.tams.hiring.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthManager {
    public String getNetid() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
