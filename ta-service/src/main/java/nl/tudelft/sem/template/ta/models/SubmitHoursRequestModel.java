package nl.tudelft.sem.template.ta.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime date;
    private String desc;
}
