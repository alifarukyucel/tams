package nl.tudelft.sem.template.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;

public class SendGridEmailSenderTests {
    private transient SendGrid mockSendGrid;

    private transient SendGridEmailSender sendGridEmailSender;

    private final String from = "crewmate@tudelft.nl";
    private final String to = "impostor@tudelft.nl";
    private final String subject = "amogus";
    private final String body = "You are kinda sus!";

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        mockSendGrid = mock(SendGrid.class);
        sendGridEmailSender = new SendGridEmailSender(mockSendGrid);
        injectFromEmail(from);
    }

    @Test
    public void sendEmailSuccessful() throws IOException {
        // Act
        sendGridEmailSender.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mockSendGrid).api(captor.capture());
        verifyNoMoreInteractions(mockSendGrid);

        Request request = captor.getValue();
        assertThat(request.getMethod()).isEqualTo(Method.POST);
        assertThat(request.getEndpoint()).isEqualTo("mail/send");
        assertThat(request.getBody()).isEqualTo(
                "{\"from\":{\"email\":\"crewmate@tudelft.nl\"},\"subject\":\"amogus\"," +
                        "\"personalizations\":[{\"to\":[{\"email\":\"impostor@tudelft.nl\"}]}]," +
                        "\"content\":[{\"type\":\"text/plain\",\"value\":\"You are kinda sus!\"}]}");
    }

    @Test
    public void sendEmailException() throws IOException {
        // Arrange
        when(mockSendGrid.api(any())).thenThrow(IOException.class);

        // Act
        Executable executable = () -> sendGridEmailSender.sendEmail(to, subject, body);

        // Assert
        assertDoesNotThrow(executable);
    }

    private void injectFromEmail(String from) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = sendGridEmailSender.getClass().getDeclaredField("fromEmail");
        declaredField.setAccessible(true);
        declaredField.set(sendGridEmailSender, from);
    }

}
