package nl.tudelft.sem.tams.ta.entities.builders.interfaces;

import nl.tudelft.sem.tams.ta.entities.Contract;

/**
 * Interface all builders of contracts should implement.
 */
public interface ContractBuilder {
    ContractBuilder withNetId(String netId);

    ContractBuilder withCourseId(String courseId);

    ContractBuilder withMaxHours(Integer maxHours);

    ContractBuilder withDuties(String duties);

    ContractBuilder withSigned(Boolean signed);

    ContractBuilder withRating(double rating);

    Contract build();
}