package nl.tudelft.sem.tams.ta.services;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import nl.tudelft.sem.tams.ta.interfaces.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * An EmailSender implementation leveraging the SendGrid API.
 */
@Service
public class SendGridEmailSender implements EmailSender {

    private final transient SendGrid sendGrid;

    @Value("${sendgrid.from}")
    private transient String fromEmail;

    public SendGridEmailSender(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    /**
     * Send an email message from the default application email address.
     *
     * @param recipient the email address of the recipient
     * @param subjectText the subject of the email
     * @param bodyText the plain-text body of the email
     */
    @Override
    public void sendEmail(String recipient, String subjectText, String bodyText) {
        try {
            Email from = new Email(fromEmail);
            Email to = new Email(recipient);
            Content content = new Content("text/plain", bodyText);
            Mail mail = new Mail(from, subjectText, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            sendGrid.api(request);
        } catch (Exception ignored) {
            // ignore
        }
    }
}
