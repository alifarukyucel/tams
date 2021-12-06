package nl.tudelft.sem.template.course.config;

import nl.tudelft.sem.template.course.security.AuthEntryPoint;
import nl.tudelft.sem.template.course.security.AuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The type Web security config.
 */
@Configuration
public class RequestAuthenticationConfig extends WebSecurityConfigurerAdapter {
    private final transient AuthEntryPoint authEntryPoint;
    private final transient AuthFilter authFilter;

    public RequestAuthenticationConfig(AuthEntryPoint authEntryPoint, AuthFilter authFilter) {
        this.authEntryPoint = authEntryPoint;
        this.authFilter = authFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
