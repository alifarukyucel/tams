package nl.tudelft.sem.template.course.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthManager {
    public String getNetid() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
