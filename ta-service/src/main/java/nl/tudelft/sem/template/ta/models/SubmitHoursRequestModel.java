package nl.tudelft.sem.template.ta.models;

import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Data
public class SubmitHoursRequestModel {
    private String course;
    private int workedTime;
    private Date date;
    private String desc;
}
