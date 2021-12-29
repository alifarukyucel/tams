package nl.tudelft.sem.template.ta.interfaces;

/**
 * An abstract interface for an email sender.
 */
public interface EmailSender {
    /**
     * Send an email message from the default application email address.
     *
     * @param recipient the email address of the recipient
     * @param subjectText the subject of the email
     * @param bodyText the plain-text body of the email
     */
    void sendEmail(String recipient, String subjectText, String bodyText);
}
