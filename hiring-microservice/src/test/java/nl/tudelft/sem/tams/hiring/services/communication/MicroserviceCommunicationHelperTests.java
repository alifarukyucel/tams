package nl.tudelft.sem.tams.hiring.services.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;

import java.util.Collections;


@SpringBootTest
@ActiveProfiles({"test", "mockRestTemplate", "mockHttpServletRequest"})
//@TestPropertySource(properties = {"microservice.course.base_url=" + ConnectedCourseInformationServiceTests.testUrl})
public class MicroserviceCommunicationHelperTests {

    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private transient HttpServletRequest request;

    @Autowired
    private transient MicroserviceCommunicationHelper microserviceCommunicationHelper;

    @BeforeEach
    public void resetMock() {
        reset(restTemplate);
        reset(request);
    }

    @Test
    public void testGet() throws Exception {
        // Arrange
        String url = "https://tams.com";
        String body = null;
        String[] variables = new String[] { "a", "b", "c" };

        // Mock headers
        String authToken = "somerandomtoken";
        HttpEntity<String> httpEntity =  requestEntity(body, authToken);
        when(request.getHeader("Authorization")).thenReturn(authToken);

        // Mock the actual request and response.
        String mockedResponse = "valid response!";
        when(restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class, (Object[]) variables))
                .thenReturn(ResponseEntity.ok(mockedResponse));

        // Act
        ResponseEntity<String> response = microserviceCommunicationHelper.get(url, String.class, "a", "b", "c");

        // Assert
        assertThat(response.getBody()).isEqualTo(mockedResponse);
        verify(request).getHeader("Authorization");
    }

    // Note:
    // that most of the "assertions" happen by the mockito "when" of the restTemplate.exchange.
    // Only if every parameter is as expected (HTTP method, headers, body, etc)
    // a valid response will be returned.

    @Test
    public void testPost() throws Exception {
        // Arrange
        String url = "https://tams.com";
        String body = "winstijnsmit";
        String[] variables = new String[] { "desc" };

        // Mock headers
        String authToken = "somerandomtoken";
        HttpEntity<String> httpEntity =  requestEntity(body, authToken);
        when(request.getHeader("Authorization")).thenReturn(authToken);

        // Mock the actual request and response
        String mockedResponse = "valid response!";
        when(restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class, (Object[]) variables))
                .thenReturn(ResponseEntity.ok(mockedResponse));

        // Act
        ResponseEntity<String> response = microserviceCommunicationHelper.post(url, String.class, body, "desc");

        // Assert
        assertThat(response.getBody()).isEqualTo(mockedResponse);
        verify(request).getHeader("Authorization");
    }

    @Test
    public void testPut() throws Exception {
        // Arrange
        String url = "https://tams.com";
        String body = "winstijnsmit";

        // Mock headers
        String authToken = "somerandomtoken";
        HttpEntity<String> httpEntity =  requestEntity(body, authToken);
        when(request.getHeader("Authorization")).thenReturn(authToken);

        // Mock the actual request and response
        String mockedResponse = "valid response!";
        when(restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class))
                .thenReturn(ResponseEntity.ok(mockedResponse));

        // Act
        ResponseEntity<String> response = microserviceCommunicationHelper.put(url, String.class, body);

        // Assert
        assertThat(response.getBody()).isEqualTo(mockedResponse);
        verify(request).getHeader("Authorization");
    }


    private <T> HttpEntity<T> requestEntity(T body, String authHeader){
        // Create the request entity with the body given
        // and setting the following headers.
        // Is later compared by mockito in "when()".
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authHeader);
        return new HttpEntity<T>(body, headers);
    }

}
