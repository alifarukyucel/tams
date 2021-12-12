package nl.tudelft.sem.template.course.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Course model to be sent through HTTP requests.
 *
 * @created 07/12/2021, 17:37
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Data
public class CourseModel {
    private String id;
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime startDate;
    private String name;
    private String description;
    private int numberOfStudents;

    
}
