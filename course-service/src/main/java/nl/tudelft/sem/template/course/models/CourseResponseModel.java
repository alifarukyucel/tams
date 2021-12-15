package nl.tudelft.sem.template.course.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.course.entities.Course;


/**
 * CourseResponseModel to be sent through HTTP responses after course queries.
 *
 * @created 09/12/2021, 19:43
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseResponseModel {

    @NotNull
    private String id;

    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime startDate;

    private String name;

    private String description;

    private int numberOfStudents;

    /**
     * Create an instance of CourseResponseModel based on given course.
     *
     * @return CourseResponseModel of given course.
     */
    public static CourseResponseModel fromCourse(Course course) {
        return new CourseResponseModel(
                course.getId(),
                course.getStartDate(),
                course.getName(),
                course.getDescription(),
                course.getNumberOfStudents()
        );
    }
}