package nl.tudelft.sem.tams.course.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * CourseCreationRequestModel to be received through HTTP requests to create courses.
 *
 * @created 09/12/2021, 19:47
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseCreationRequestModel {

    @NotNull
    private String id;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize
    private LocalDateTime startDate;

    private String name;

    private String description;

    @NotNull
    private int numberOfStudents;
}
