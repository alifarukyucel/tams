package nl.tudelft.sem.template.ta.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class HourResponseModel {
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
            declaration.getDate(),
            declaration.getDesc(),
            declaration.getWorkedTime(),
            declaration.getApproved(),
            declaration.getContract().getNetId()
        );
    }
}
