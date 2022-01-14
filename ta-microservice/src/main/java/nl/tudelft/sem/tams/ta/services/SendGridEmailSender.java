package nl.tudelft.sem.tams.ta.services;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import nl.tudelft.sem.tams.ta.entities.Contract;
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
    protected void sendEmail(String recipient, String subjectText, String bodyText) {
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

    /**
     * Sends an email to the given email address describing the given contract.
     * Does nothing when the email is null.
     *
     * @param email email address to which the email should be sent
     * @param contract the contract that will be detailed inside of the email.
     */
    @Override
    public void sendContractCreatedEmail(String email, Contract contract) {
        if (email != null && contract != null) {
            // Subject and body of the email sent to TAs when creating a contract
            String taEmailSubjectTemplate = "You have been offered a TA position for %s";
            String emailSubject = String.format(taEmailSubjectTemplate, contract.getCourseId());
            String taEmailBodyTemplate = "Hi %s,\n\n"
                + "The course staff of %s is offering you a TA position. Congratulations!\n"
                + "Your duties are \"%s\", and the maximum number of hours is %s.\n"
                + "Please log into TAMS to review and sign the contract.\n\n"
                + "Best regards,\nThe programme administration of your faculty";

            String emailBody = String.format(taEmailBodyTemplate, contract.getNetId(), contract.getCourseId(),
                contract.getDuties(), contract.getMaxHours());
            sendEmail(email, emailSubject, emailBody);
        }
    }
}
