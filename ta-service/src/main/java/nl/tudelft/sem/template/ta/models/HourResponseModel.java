package nl.tudelft.sem.template.ta.models;

import java.util.Date;
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
    private Date date;
    private String description;
    private int workedTime;
    private boolean approved;
    private String ta;

    /**
     * Create an instance of HourResponseModel based on given HourDeclaration.
     *
     * @return HourResponseModel of given HourDeclaration.
     */
    public static HourResponseModel fromHourDeclaration(HourDeclaration declaration) {
        return new HourResponseModel(
            declaration.getId(),
            declaration.getDate(),
            declaration.getDesc(),
            declaration.getWorkedTime(),
            declaration.getApproved(),
            declaration.getContract().getNetId()
        );
    }
}
