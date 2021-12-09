package nl.tudelft.sem.template.ta.services;

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

        ExampleMatcher ignoreAllFields = ExampleMatcher.matchingAll().withIgnoreNullValues();

        Example<Contract> example = Example.of(
            Contract.builder()
            .courseId(courseId)
            .netId(netId)
            .build(), ignoreAllFields);

        var optionalContract = contractRepository.findOne(example);
        if (optionalContract.isEmpty()) {
            throw new NoSuchElementException("The requested contract could not be found");
        }
        return optionalContract.get();
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


}
