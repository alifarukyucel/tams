package nl.tudelft.sem.template.ta.services;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.compositekeys.ContractId;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
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

    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }


    /**
     * Create a contract that is unsigned.
     * Note that this method ensures that the contract does not exist.
     *
     * @param courseId courseId of contract
     * @param netId netId of TA.
     * @param maxHours max amount of hours g a TA can work
     * @param duties of the TA
     * @return a saved instance of Contract.
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     *                                  or when contract already exists.
     */
    public Contract createUnsignedContract(String netId, String courseId,
                                                        int maxHours, String duties)
                                            throws IllegalArgumentException {

        // Check if parameters were given are valid.
        if (StringUtils.isEmpty(netId)
            || StringUtils.isEmpty(courseId)
            || maxHours <= 0 || duties == null) {
            throw new IllegalArgumentException("netId, courseId, maxHours and duties should be given and valid.");
        }

        // Check if contract already exists - return an error if not.
        if (contractExists(netId, courseId)) {
            throw new IllegalArgumentException("This contract already exists!");
        }

        // Create the actual contract with the builder.
        Contract contract = Contract.builder()
            .netId(netId)
            .courseId(courseId)
            .maxHours(maxHours)
            .duties(duties)
            .signed(false) // not signed yet!
            .build();

        // save can also throw an IllegalArgumentException if failed.
        contract = save(contract);
        return contract;
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
     * Saves a given contract object back to the database.
     *
     * @param contract The contract to save.
     * @return The newly saved contract.
     * @throws IllegalArgumentException if declaration does not meet requirements.
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
                Contract.builder()
                        .courseId(courseId)
                        .netId(netId)
                        .build(), ignoreAllFields);
        return example;
    }

}
