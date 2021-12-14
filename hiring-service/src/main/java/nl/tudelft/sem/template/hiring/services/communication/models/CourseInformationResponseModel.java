package nl.tudelft.sem.template.hiring.services.communication.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sun.istack.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseInformationResponseModel {

    @NotNull
    private String id;

    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime startDate;

    private String name;

    private String description;

    private int numberOfStudents;
}

