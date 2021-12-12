package nl.tudelft.sem.template.ta.services;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MicroserviceCommunicationHelper {
    private transient HttpServletRequest request;

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
}
