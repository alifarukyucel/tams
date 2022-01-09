package nl.tudelft.sem.tams.ta.entities.builders;

import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.builders.interfaces.ContractBuilder;

/*
 * A concrete Builder for the Contract entity.
 */
public class ConcreteContractBuilder implements ContractBuilder {
    private transient String netId;
    private transient String courseId;
    private transient Integer maxHours;
    private transient String duties;
    private transient Boolean signed;
    private transient double rating;
    private transient int actualWorkedHours;

    public ConcreteContractBuilder() {
    }

    public ConcreteContractBuilder withNetId(String netId) {
        this.netId = netId;
        return this;
    }

    public ConcreteContractBuilder withCourseId(String courseId) {
        this.courseId = courseId;
        return this;
    }

    public ConcreteContractBuilder withMaxHours(Integer maxHours) {
        this.maxHours = maxHours;
        return this;
    }

    public ConcreteContractBuilder withDuties(String duties) {
        this.duties = duties;
        return this;
    }

    public ConcreteContractBuilder withSigned(Boolean signed) {
        this.signed = signed;
        return this;
    }

    public ConcreteContractBuilder withRating(double rating) {
        this.rating = rating;
        return this;
    }

    public ConcreteContractBuilder withActualWorkedHours(int hours) {
        this.actualWorkedHours = hours;
        return this;
    }

    public Contract build() {
        return new Contract(netId, courseId, maxHours, duties, signed, rating, actualWorkedHours);
    }
}
