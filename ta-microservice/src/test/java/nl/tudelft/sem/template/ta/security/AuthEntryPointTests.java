package nl.tudelft.sem.template.ta.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;

public class AuthEntryPointTests {
    private transient AuthEntryPoint authEntryPoint;

    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient AuthenticationException dummyAuthenticationException;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        dummyAuthenticationException = Mockito.mock(AuthenticationException.class);

        authEntryPoint = new AuthEntryPoint();
    }

    @Test
    public void commenceTest() throws ServletException, IOException {
        // Act
        authEntryPoint.commence(mockRequest, mockResponse, dummyAuthenticationException);

        // Assert
        verifyNoInteractions(mockRequest);
        verify(mockResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        verifyNoMoreInteractions(mockResponse);
    }
}
