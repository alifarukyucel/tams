package nl.tudelft.sem.tams.ta.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteContractBuilder;
import nl.tudelft.sem.tams.ta.entities.builders.directors.ContractDirector;
import nl.tudelft.sem.tams.ta.entities.compositekeys.ContractId;
import nl.tudelft.sem.tams.ta.interfaces.CourseInformation;
import nl.tudelft.sem.tams.ta.interfaces.EmailSender;
import nl.tudelft.sem.tams.ta.repositories.ContractRepository;
import nl.tudelft.sem.tams.ta.services.communication.models.CourseInformationResponseModel;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The ContractService.
 * Handles the business logic of everything a user wants to do with a contract.
 */
@Service
public class ContractService {

    private final transient double studentsPerOneTa = 20f;

    private final transient ContractRepository contractRepository;

    private final transient CourseInformation courseInformation;

    private final transient EmailSender emailSender;

    // Subject and body of the email sent to TAs when creating a contract
    private final transient String taEmailSubjectTemplate = "You have been offered a TA position for %s";
    private final transient String taEmailBodyTemplate = "Hi %s,\n\n"
            + "The course staff of %s is offering you a TA position. Congratulations!\n"
            + "Your duties are \"%s\", and the maximum number of hours is %s.\n"
            + "Please log into TAMS to review and sign the contract.\n\n"
            + "Best regards,\nThe programme administration of your faculty";

    /**
     * Create an instance of a ContractService.
     *
     * @param contractRepository the contract repository
     * @param courseInformation  the course information service
     * @param emailSender        an email sender
     */
    public ContractService(ContractRepository contractRepository, CourseInformation courseInformation,
                           EmailSender emailSender) {
        this.contractRepository = contractRepository;
        this.courseInformation = courseInformation;
        this.emailSender = emailSender;
    }


    /**
     * Create a contract that is unsigned.
     * Optionally email the newly-hired TA to inform them they have a new contract to sign.
     * Note that this method ensures that the contract does not exist.
     *
     * @param courseId courseId of contract
     * @param netId netId of TA.
     * @param maxHours max amount of hours a TA can work
     * @param duties duties of the TA
     * @param taContactEmail an email to contact the TA
     * @return a saved instance of Contract.
     * @throws IllegalArgumentException if any of the parameters are null or invalid,
     *                                  the contract already exists, or
     *                                  no more TAs are allowed to be hired for the course.
     */
    public Contract createUnsignedContract(String netId, String courseId, int maxHours, String duties,
                                           String taContactEmail) throws IllegalArgumentException {

        // Check if parameters were given are valid.
        if (StringUtils.isEmpty(netId)
            || StringUtils.isEmpty(courseId)
            || maxHours <= 0) {
            throw new IllegalArgumentException("netId, courseId, maxHours are required and need to be valid.");
        }

        // Check if contract already exists - return an error if not.
        if (contractExists(netId, courseId)) {
            throw new IllegalArgumentException("This contract already exists!");
        }

        if (isTaLimitReached(courseId)) {
            throw new IllegalArgumentException("No more TAs can be hired for this course.");
        }

        var builder = new ConcreteContractBuilder();
        new ContractDirector().createUnsignedContract(builder);

        // Create the actual contract with the builder.
        Contract contract = builder
            .withNetId(netId)
            .withCourseId(courseId)
            .withMaxHours(maxHours)
            .withDuties(duties)
            .build();

        // save can also throw an IllegalArgumentException if failed.
        contract = save(contract);

        // email the newly-hired TA if a contact email is specified
        if (taContactEmail != null) {
            String emailSubject = String.format(taEmailSubjectTemplate, courseId);
            String emailBody = String.format(taEmailBodyTemplate, netId, courseId, duties, maxHours);
            this.emailSender.sendEmail(taContactEmail, emailSubject, emailBody);
        }

        return contract;
    }


    /**
     * Returns the requested contract based on the users netId and the specified CourseId.
     *
     * @param courseId The specified course to which the contract belongs.
     * @return true if more TAs can be hired.
     * @throws IllegalArgumentException if the course cannot be retrieved.
     */
    private boolean isTaLimitReached(String courseId) {
        CourseInformationResponseModel model = courseInformation.getCourseById(courseId);
        if (model == null) {
            throw new IllegalArgumentException("Could not retrieve course");
        }

        int allowedTas = (int) Math.ceil(model.getNumberOfStudents() / studentsPerOneTa);

        long hiredTas = contractRepository.count(createContractExample(null, courseId));

        return hiredTas >= allowedTas;
    }

    /**
     * Returns the requested contract based on the users netId and the specified CourseId.
     *
     * @param netId     The users netId.
     * @param courseId  The specified course to which the contract belongs.
     * @return          The contract as specified by the user.
     * @throws NoSuchElementException Thrown if the contract was not found.
     */
    public Contract getContract(String netId, String courseId) throws NoSuchElementException {
        if (netId == null || courseId == null) {
            throw new NoSuchElementException("A contract must have a netId and courseId");
        }

        var contract = contractRepository.findById(new ContractId(netId, courseId));
        if (contract.isEmpty()) {
            throw new NoSuchElementException("The requested contract does not exist");
        }

        return contract.get();
    }

    /**
     * Returns all the contracts that have a certain netId and courseId.
     * If null is given to one of the arguments it will be ignored in the query.
     *
     * @param netId The users netId (required)
     * @param courseId The contracts courseId (may be null)
     * @return a list of contracts with the requested netId and courseId.
     * @throws NoSuchElementException Thrown when no contracts were found.
     */
    public List<Contract> getContractsBy(String netId, String courseId)
            throws NoSuchElementException {
        if (netId == null) {
            throw new NoSuchElementException("netId must be specified to search for contracts");
        }

        Example<Contract> example = createContractExample(netId, courseId);

        // Search for all the contract with a certain netId.
        List<Contract> contracts = contractRepository.findAll(example);
        if (contracts.size() == 0) {
            throw new NoSuchElementException("Could not find contracts for " + netId);
        }

        return contracts;
    }


    /**
     * Returns all the contracts that have a certain netId.
     *
     * @param netId The users netid
     * @return a list of contracts with the requested netId.
     * @throws NoSuchElementException Thrown when no contracts were found.
     */
    public List<Contract> getContractsBy(String netId) throws NoSuchElementException {
        return getContractsBy(netId, null);
    }


    /**
     * Returns whether a contract already exists.
     *
     * @param netId The contract's netId (required)
     * @param courseId The contract's courseId (required)
     * @returns boolean whether contract exists.
     */
    public boolean contractExists(String netId, String courseId) {
        try {
            // This method throws an exception when contract does not exist.
            getContract(netId, courseId);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Set a contracts signed status to true.
     *
     * @param netId     The netId of the user whose contract we are changing.
     * @param courseId  The course to which the contract belongs.
     * @throws NoSuchElementException Thrown if contract could not be found.
     * @throws IllegalArgumentException Thrown if the contract was already signed.
     */
    public void sign(String netId, String courseId)
        throws NoSuchElementException, IllegalArgumentException {
        Contract contract = getContract(netId, courseId);
        if (contract.getSigned()) {
            throw new IllegalArgumentException("Contract was already accepted.");
        }
        contract.setSigned(true);
        contractRepository.save(contract);
    }

    /**
     * Set a contracts rating to a new value.
     *
     * @param netId     The netId of the user whose contract we are changing.
     * @param courseId  The course to which the contract belongs.
     * @throws NoSuchElementException Thrown if contract could not be found.
     * @throws IllegalArgumentException Thrown if rating was not between 0 and 10.
     */
    public void rate(String netId, String courseId, double rating)
        throws NoSuchElementException, IllegalArgumentException {
        Contract contract = getContract(netId, courseId);
        contract.setRating(rating);
        contractRepository.save(contract);
    }

    /**
     * Retrieve the average ratings of a list of netIds
     * Calls the query method in the ContractRepository and parses the resulting query.
     * If netId could have not been found the rating is set to -1 in the result map.
     *
     * @param netIds a collection of netIds.
     * @return map with netId as key and the average rating as value.
     * @throws IllegalArgumentException if netIds is null or empty.
     */
    public Map<String, Double> getAverageRatingOfNetIds(Collection<String> netIds)
        throws IllegalArgumentException {

        if (netIds == null || netIds.isEmpty()) {
            throw new IllegalArgumentException("netIds should atleast contain one netId");
        }

        // Create and fill hash with empty ratings.
        Map<String, Double> result = new HashMap<>();
        for (String netId : netIds) {
            result.put(netId, -1.0);
        }

        // Query the average ratings of netIds.
        List<Object[]> queryResult = contractRepository.queryAverageRatingOfNetIds(netIds);
        for (Object[] data : queryResult) {
            result.put((String) data[0], (Double) data[1]);
        }
        return result;
    }


    /**
     * Saves a given contract object back to the database.
     *
     * @param contract The contract to save.
     * @return The newly saved contract.
     */
    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    /**
     * Creates an example which can be used to find a Contract
     * with a certain netId and courseId inside the database.
     *
     * @param netId the example's netId
     * @param courseId  the example's courseId
     * @return an example contract.
     */
    private Example<Contract> createContractExample(String netId, String courseId) {
        ExampleMatcher ignoreAllFields = ExampleMatcher.matchingAll()
                                                        .withIgnoreNullValues()
                                                        .withIgnorePaths("rating");
        Example<Contract> example = Example.of(
                new ConcreteContractBuilder()
                        .withCourseId(courseId)
                        .withNetId(netId)
                        .build(), ignoreAllFields);
        return example;
    }

}