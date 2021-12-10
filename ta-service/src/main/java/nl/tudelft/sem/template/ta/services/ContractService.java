package nl.tudelft.sem.template.ta.services;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

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

        List<Contract> contracts = getContractsBy(netId, courseId);
        return contracts.get(0);
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
                                                        .withIgnoreNullValues();
        Example<Contract> example = Example.of(
                Contract.builder()
                        .courseId(courseId)
                        .netId(netId)
                        .build(), ignoreAllFields);
        return example;
    }

}
