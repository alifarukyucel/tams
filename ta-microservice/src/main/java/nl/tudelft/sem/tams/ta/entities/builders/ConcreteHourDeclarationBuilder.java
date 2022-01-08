package nl.tudelft.sem.tams.ta.entities.builders;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.HourDeclaration;
import nl.tudelft.sem.tams.ta.entities.builders.interfaces.HourDeclarationBuilder;

public class ConcreteHourDeclarationBuilder implements HourDeclarationBuilder {
    private transient UUID id;
    private transient Integer workedTime;
    private transient Boolean approved;
    private transient Boolean reviewed;
    private transient LocalDateTime date;
    private transient String desc;
    private transient Contract contract;

    public ConcreteHourDeclarationBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ConcreteHourDeclarationBuilder withWorkedTime(Integer workedTime) {
        this.workedTime = workedTime;
        return this;
    }

    public ConcreteHourDeclarationBuilder withApproved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    public ConcreteHourDeclarationBuilder withReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
        return this;
    }

    public ConcreteHourDeclarationBuilder withDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public ConcreteHourDeclarationBuilder withDescription(String desc) {
        this.desc = desc;
        return this;
    }

    public ConcreteHourDeclarationBuilder withContractId(Contract contract) {
        this.contract = contract;
        return this;
    }

    public HourDeclaration build() {
        return new HourDeclaration(id, workedTime, approved, reviewed, date, desc, contract);
    }
}
