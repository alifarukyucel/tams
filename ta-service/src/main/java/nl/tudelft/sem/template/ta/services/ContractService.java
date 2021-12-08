package nl.tudelft.sem.template.ta.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

        // Create contract example with this netId and courseId.
        Example<Contract> example = createContractExample(netId, courseId);

        var optionalContract = contractRepository.findOne(example);
        if (optionalContract.isEmpty()) {
            throw new NoSuchElementException("The requested contract could not be found");
        }
        return optionalContract.get();
    }


    /**
     * Returns all the contracts that have a certain netId.
     *
     * @param netId The users netId
     * @return      A list of contracts with the given netId.
     * @throws NoSuchElementException Thrown when no contracts were found.
     */
    public List<Contract> getContractsOfNetID(String netId) throws NoSuchElementException {
        if (netId == null)
            throw new NoSuchElementException("netId must be specified to search for contracts");

        Example<Contract> example = createContractExample(netId);

        // Search for all the contract with a certain netId.
        List<Contract> contracts = contractRepository.findAll(example);
        if (contracts.size() == 0)
            throw new NoSuchElementException("Could not find contracts for " + netId);

        return contracts;
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
     * Creates an example which can be used to find a Contract with a certain netId and courseId inside of the database.
     * @param netId the example's netId
     * @param courseId  the example's courseId
     * @return an example contract.
     */
    private Example<Contract> createContractExample(String netId, String courseId){
        ExampleMatcher ignoreAllFields = ExampleMatcher.matchingAll().withIgnoreNullValues();
        Example<Contract> example = Example.of(
                Contract.builder()
                        .courseId(courseId)
                        .netId(netId)
                        .build(), ignoreAllFields);
        return example;
    }

    /**
     * Creates an example which can be used to find a Contract with a certain netId inside of the database.
     * @param netId the example's netId
     * @return an example contract.
     */
    private Example<Contract> createContractExample(String netId) {
        return createContractExample(netId, null);
    }
}
