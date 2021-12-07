package nl.tudelft.sem.template.ta.services;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import org.springframework.data.domain.Example;
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

        Example<Contract> example = Example.of(
            Contract.builder()
            .courseId(courseId)
            .netId(netId)
            .build());

        var optionalContract = contractRepository.findOne(example);
        if (optionalContract.isEmpty()) {
            throw new NoSuchElementException("The requested contract could not be found");
        }
        return optionalContract.get();
    }


}
