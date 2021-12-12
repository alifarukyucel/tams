package nl.tudelft.sem.template.ta.services;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MicroserviceCommunicationHelper {
    private final transient HttpServletRequest request;

    public MicroserviceCommunicationHelper(HttpServletRequest request) {
        this.request = request;
    }

    public <T, J> ResponseEntity<T> send(String url, Class<T> responseType, J body,
                                         HttpMethod method, String... variables) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", request.getHeader("Authorization"));
        HttpEntity<J> request = new HttpEntity<J>(body, headers);
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                method,
                request,
                responseType,
                (Object[]) variables
        );
        return response;
    }

    public <T> ResponseEntity<T> get(String url, Class<T> responseType, String... variables) {
        return send(url, responseType, null, HttpMethod.GET, variables);
    }

    public <T, J> ResponseEntity<T> post(String url, Class<T> responseType, J body, String... variables) {
        return send(url, responseType, body, HttpMethod.POST, variables);
    }
}
