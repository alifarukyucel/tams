package nl.tudelft.sem.template.ta.interfaces;

public interface EmailSender {
    void sendEmail(String recipient, String subjectText, String bodyText);
}
