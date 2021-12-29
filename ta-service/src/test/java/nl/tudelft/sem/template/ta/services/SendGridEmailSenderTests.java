package nl.tudelft.sem.template.ta.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SendGridEmailSenderTests {
    private transient SendGrid mockSendGrid;

    private transient SendGridEmailSender sendGridEmailSender;

    @BeforeEach
    public void setup() {
        mockSendGrid = mock(SendGrid.class);
        sendGridEmailSender = new SendGridEmailSender(mockSendGrid);
    }

    @Test
    public void sendEmailSuccessful() throws NoSuchFieldException, IllegalAccessException, IOException {
        // Arrange
        String from = "crewmate@tudelft.nl";
        String to = "impostor@tudelft.nl";
        String subject = "amogus";
        String body = "You are kinda sus!";
        injectFromEmail(from);

        // Act
        sendGridEmailSender.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mockSendGrid).api(captor.capture());

        Request request = captor.getValue();
        assertThat(request.getMethod()).isEqualTo(Method.POST);
        assertThat(request.getEndpoint()).isEqualTo("mail/send");
        assertThat(request.getBody()).isEqualTo(
                "{\"from\":{\"email\":\"crewmate@tudelft.nl\"},\"subject\":\"amogus\"," +
                        "\"personalizations\":[{\"to\":[{\"email\":\"impostor@tudelft.nl\"}]}]," +
                        "\"content\":[{\"type\":\"text/plain\",\"value\":\"You are kinda sus!\"}]}");
    }

    private void injectFromEmail(String from) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = sendGridEmailSender.getClass().getDeclaredField("fromEmail");
        declaredField.setAccessible(true);
        declaredField.set(sendGridEmailSender, from);
    }

}
