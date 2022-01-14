package nl.tudelft.sem.tams.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.lang.reflect.Field;

import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteContractBuilder;
import nl.tudelft.sem.tams.ta.entities.builders.interfaces.ContractBuilder;
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

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        mockSendGrid = mock(SendGrid.class);
        sendGridEmailSender = new SendGridEmailSender(mockSendGrid);
        injectFromEmail(from);
    }

    @Test
    public void sendEmailSuccessful() throws IOException {
        // Arrange
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode expectedJson = mapper.readTree("{"
                + "  \"from\": {"
                + "    \"email\": \"" + from + "\""
                + "  },"
                + "  \"subject\": \"" + subject + "\","
                + "  \"personalizations\": ["
                + "    {"
                + "      \"to\": ["
                + "        {"
                + "          \"email\": \"" + to + "\""
                + "        }"
                + "      ]"
                + "    }"
                + "  ],"
                + "  \"content\": ["
                + "    {"
                + "      \"type\": \"text/plain\","
                + "      \"value\": \"" + body + "\""
                + "    }"
                + "  ]"
                + "}"
        );

        // Act
        sendGridEmailSender.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mockSendGrid).api(captor.capture());
        verifyNoMoreInteractions(mockSendGrid);

        Request request = captor.getValue();
        assertThat(request.getMethod()).isEqualTo(Method.POST);
        assertThat(request.getEndpoint()).isEqualTo("mail/send");

        JsonNode actualJson = mapper.readTree(request.getBody());
        assertThat(actualJson).isEqualTo(expectedJson);
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

    @Test
    public void sendContractCreatedEmailValidArguments() {
        // Arrange
        SendGridEmailSender real = new SendGridEmailSender(mockSendGrid);
        SendGridEmailSender partialMock = spy(real);
        String testEmail = "winstijn@tudelft.nl";
        Contract contract = new ConcreteContractBuilder()
            .withCourseId("CSE2310")
            .withNetId("BillGates")
            .withMaxHours(10)
            .withDuties("My duties")
            .build();


        String expectedSubject = "You have been offered a TA position for CSE2310";
        String expectedBody =             "Hi BillGates,\n\n"
            + "The course staff of CSE2310 is offering you a TA position. Congratulations!\n"
            + "Your duties are \"My duties\", and the maximum number of hours is 10.\n"
            + "Please log into TAMS to review and sign the contract.\n\n"
            + "Best regards,\nThe programme administration of your faculty\n"
            + "Feel free to contact us at null";  // contact email is not loaded because of partial mocking

        doNothing().when(partialMock).sendEmail(any(), any(), any());

        // Act
        partialMock.sendContractCreatedEmail(testEmail, contract);

        // Assert
        verify(partialMock).sendEmail(testEmail, expectedSubject, expectedBody);
    }

    @Test
    public void sendContractCreatedEmailInValidArguments() {
        // Arrange
        SendGridEmailSender real = new SendGridEmailSender(mockSendGrid);
        SendGridEmailSender partialMock = spy(real);

        String testEmail = "winstijn@tudelft.nl";
        Contract contract = new ConcreteContractBuilder()
            .withCourseId("CSE2310")
            .withNetId("BillGates")
            .withMaxHours(10)
            .withDuties("My duties")
            .build();

        // Assert, working around illegal calling of underlying function. Must violate AAA structure for this.
        doThrow(new IllegalCallerException("This method should not be called"))
            .when(partialMock)
            .sendEmail(any(), any(), any());

        // Act
        partialMock.sendContractCreatedEmail(null, contract);
        partialMock.sendContractCreatedEmail(testEmail, null);
        partialMock.sendContractCreatedEmail(null, null);
    }

    private void injectFromEmail(String from) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = sendGridEmailSender.getClass().getDeclaredField("fromEmail");
        declaredField.setAccessible(true);
        declaredField.set(sendGridEmailSender, from);
    }

}
