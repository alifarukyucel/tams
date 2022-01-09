package nl.tudelft.sem.tams.ta.entities.builders.directors;

import nl.tudelft.sem.tams.ta.entities.builders.interfaces.ContractBuilder;

public class ContractDirector {

    /**
     * Creates an unsigned contract with default rating 0.
     *
     * @param contractBuilder the contract Builder to direct.
     */
    public void createUnsignedContract(ContractBuilder contractBuilder) {
        contractBuilder.withRating(0);
        contractBuilder.withSigned(false);
        contractBuilder.withActualWorkedHours(0);
    }
}
