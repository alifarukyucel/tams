package nl.tudelft.sem.template.ta.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.ta.entities.Contract;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Data
public class ContractResponseModel {
    private String course;
    private String netId;
    private String duties;
    private double rating;
    private int maxHours;
    private boolean signed;

    /**
     * Create an instance of ContractResponseModel based on given contract.
     *
     * @return ContractResponseModel of given contract.
     */
    public static ContractResponseModel fromContract(Contract contract) {
        return new ContractResponseModel(
            contract.getCourseId(),
            contract.getNetId(),
            contract.getDuties(),
            contract.getRating(),
            contract.getMaxHours(),
            contract.getSigned()
        );
    }

}
