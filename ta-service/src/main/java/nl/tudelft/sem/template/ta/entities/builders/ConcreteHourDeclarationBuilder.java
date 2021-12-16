package nl.tudelft.sem.template.ta.entities.builders;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.entities.builders.interfaces.HourDeclarationBuilder;

public class ConcreteHourDeclarationBuilder implements HourDeclarationBuilder {
    private UUID id;
    private Integer workedTime;
    private Boolean approved;
    private Boolean reviewed;
    private LocalDateTime date;
    private String desc;
    private Contract contract;

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
