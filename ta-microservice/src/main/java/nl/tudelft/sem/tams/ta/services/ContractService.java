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
import nl.tudelft.sem.tams.ta.models.CreateContractRequestModel;
import nl.tudelft.sem.tams.ta.repositories.ContractRepository;
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


    private final transient ContractRepository contractRepository;

    private final transient CourseInformation courseInformation;

    private final transient EmailSender emailSender;

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
     * @param contractModel a model containing netId of the TA, courseId of contract,
     *                      max amount of hours a TA can work, duties of the TA
     *                      and an email to contact the TA with.
     * @return a saved instance of Contract.
     * @throws IllegalArgumentException if any of the parameters are null or invalid,
     *                                  the contract already exists, or
     *                                  no more TAs are allowed to be hired for the course.
     */
    public Contract createUnsignedContract(CreateContractRequestModel contractModel) throws IllegalArgumentException {

        // Verify if a contract can be made with the parameters given.
        verifyContractCreationParameters(contractModel.getNetId(),
                                         contractModel.getCourseId(),
                                         contractModel.getMaxHours());

        // Create a new unsigned contract with a builder.
        var builder = new ConcreteContractBuilder();
        new ContractDirector().createUnsignedContract(builder);

        // Create the actual contract with the builder.
        Contract contract = builder
            .withNetId(contractModel.getNetId())
            .withCourseId(contractModel.getCourseId())
            .withMaxHours(contractModel.getMaxHours())
            .withDuties(contractModel.getDuties())
            .build();

        // save can also throw an IllegalArgumentException if failed.
        contract = contractRepository.save(contract);

        // Email the newly-hired TA if a contact email is specified
        emailSender.sendContractCreatedEmail(contractModel.getTaContactEmail(), contract);

        return contract;
    }

    /**
     * Returns the requested contract based on the users netId and the specified CourseId.
     *
     * @param courseId The specified course to which the contract belongs.
     * @return true if more TAs can be hired.
     * @throws IllegalArgumentException if the course cannot be retrieved.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")  // it does not like the try catch trick
    private boolean isTaLimitReached(String courseId) {
        int numberOfTas = courseInformation.getAmountOfStudents(courseId);

        // This value represents how many students there need to be to justify the need of a TA.
        // It is used to calculate how many TAs are needed for a course.
        double studentsPerOneTa = 20f;
        int allowedTas = (int) Math.ceil(numberOfTas / studentsPerOneTa);

        long hiredTas;

        try {
            hiredTas = getContractsBy(null, courseId).size();
        } catch (NoSuchElementException e) {
            hiredTas = 0;
        }

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

        ExampleMatcher ignoreAllFields = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnorePaths("rating", "actualWorkedHours");
        Example<Contract> example = Example.of(
            new ConcreteContractBuilder()
                .withCourseId(courseId)
                .withNetId(netId)
                .build(), ignoreAllFields);

        // Search for all the contract with a certain netId.
        List<Contract> contracts = contractRepository.findAll(example);
        if (contracts.size() == 0) {
            throw new NoSuchElementException("Could not find contracts for " + netId);
        }

        return contracts;
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
     * Updates the actually worked hours of the contract.
     *
     * @param netId user netId
     * @param course course for which user has a contract
     * @param hours the new hour value to set
     */
    public void updateHours(String netId, String course, int hours) {
        Contract contract = getContract(netId, course);
        if (!contract.getSigned()) {
            throw new IllegalCallerException("Contract has not been signed yet");
        }
        contract.setActualWorkedHours(hours);
        contractRepository.save(contract);
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
     * Checks and verifies if a contract can be created with the given netId, courseId and maxHours.
     * Throws an IllegalArgumentException if it is not possible.
     *
     * @param netId the netId of the contract
     * @param courseId the courseId of the contract
     * @param maxHours the maximum hours of the contract
     * @throws IllegalArgumentException if parameters are invalid
     */
    private void verifyContractCreationParameters(String netId, String courseId, int maxHours)
        throws IllegalArgumentException {

        // Check if parameters were given are valid.
        if (StringUtils.isEmpty(netId)
            || StringUtils.isEmpty(courseId)
            || maxHours <= 0) {
            throw new IllegalArgumentException("netId, courseId, maxHours are required and need to be valid.");
        }

        // Check if contract already exists - return an error if not.
        if (contractRepository.existsById(new ContractId(netId, courseId))) {
            throw new IllegalArgumentException("This contract already exists!");
        }

        if (isTaLimitReached(courseId)) {
            throw new IllegalArgumentException("No more TAs can be hired for this course.");
        }
    }



}
