package nl.tudelft.sem.template.ta.services.communication;

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

    /**
     * Make a request to a URL with the request and response type headers as well as the authentication
     * header pre-configured.
     *
     * @param url          The URL to make a request to
     * @param responseType The (expected) type of the response
     * @param body         The body of the request
     * @param method       The HTTP method
     * @param variables    The variables to be inserted into the URL
     * @param <T>          The type of the response
     * @param <J>          The type of the request
     * @return The response
     * @throws Exception if the status code is not 200 OK
     */
    private <T, J> ResponseEntity<T> send(String url, Class<T> responseType, J body,
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

    /**
     * Make a GET request to a URL with the request and response type headers as well as the authentication
     * header pre-configured.
     *
     * @param url          The URL to make a request to
     * @param responseType The (expected) type of the response
     * @param variables    The variables to be inserted into the URL
     * @param <T>          The type of the response
     * @return The response
     * @throws Exception if the status code is not 200 OK
     */
    public <T> ResponseEntity<T> get(String url, Class<T> responseType, String... variables) {
        return send(url, responseType, null, HttpMethod.GET, variables);
    }

    /**
     * Make a POST to a URL with the request and response type headers as well as the authentication
     * header pre-configured.
     *
     * @param url          The URL to make a request to
     * @param responseType The (expected) type of the response
     * @param body         The body of the request
     * @param variables    The variables to be inserted into the URL
     * @param <T>          The type of the response
     * @param <J>          The type of the request
     * @return The response
     * @throws Exception if the status code is not 200 OK
     */
    public <T, J> ResponseEntity<T> post(String url, Class<T> responseType, J body, String... variables) {
        return send(url, responseType, body, HttpMethod.POST, variables);
    }

    /**
     * Make a PUT to a URL with the request and response type headers as well as the authentication
     * header pre-configured.
     *
     * @param url          The URL to make a request to
     * @param responseType The (expected) type of the response
     * @param body         The body of the request
     * @param variables    The variables to be inserted into the URL
     * @param <T>          The type of the response
     * @param <J>          The type of the request
     * @return The response
     * @throws Exception if the status code is not 200 OK
     */
    public <T, J> ResponseEntity<T> put(String url, Class<T> responseType, J body, String... variables) {
        return send(url, responseType, body, HttpMethod.PUT, variables);
    }
}
