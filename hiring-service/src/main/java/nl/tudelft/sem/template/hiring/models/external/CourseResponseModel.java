package nl.tudelft.sem.template.hiring.models.external;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseModel {

    @NotNull
    private String id;

    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime startDate;

    private String name;

    private String description;

    private int numberOfStudents;
}

