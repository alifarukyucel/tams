package nl.tudelft.sem.template.ta.entities.builders.interfaces;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;

public interface HourDeclarationBuilder {
    public HourDeclarationBuilder withId(UUID id);

    public HourDeclarationBuilder withWorkedTime(Integer workedTime);

    public HourDeclarationBuilder withApproved(Boolean approved);

    public HourDeclarationBuilder withReviewed(Boolean reviewed);

    public HourDeclarationBuilder withDate(LocalDateTime date);

    public HourDeclarationBuilder withDescription(String desc);

    public HourDeclarationBuilder withContractId(Contract contract);

    public HourDeclaration build();
}
