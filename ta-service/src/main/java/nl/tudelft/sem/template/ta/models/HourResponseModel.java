package nl.tudelft.sem.template.ta.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class HourResponseModel {
    private UUID id;
    private String netId;
    private LocalDateTime date;
    private String description;
    private int workedTime;
    private boolean approved;

    /**
     * Create an instance of HourResponseModel based on given HourDeclaration.
     *
     * @return HourResponseModel of given HourDeclaration.
     */
    public static HourResponseModel fromHourDeclaration(HourDeclaration declaration) {
        return new HourResponseModel(
            declaration.getId(),
            declaration.getContract().getNetId(),
            declaration.getDate(),
            declaration.getDescription(),
            declaration.getWorkedTime(),
            declaration.getApproved()
        );
    }
}
