package nl.tudelft.sem.tams.ta.interfaces;

import nl.tudelft.sem.tams.ta.entities.Contract;

/**
 * An abstract interface for an email sender.
 */
public interface EmailSender {
    /**
     * Sends an email to the given email address describing the given contract.
     * Does nothing when the email is null.
     *
     * @param email email address to which the email should be sent
     * @param contract the contract that will be detailed inside of the email.
     */
    void sendContractCreatedEmail(String email, Contract contract);
}
