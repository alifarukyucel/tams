package nl.tudelft.sem.template.ta.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
}
