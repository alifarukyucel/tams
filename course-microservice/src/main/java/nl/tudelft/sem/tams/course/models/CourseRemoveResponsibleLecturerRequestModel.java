package nl.tudelft.sem.tams.course.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CourseRemoveResponsibleLecturerRequestModel to remove responsible lecturers to courses.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseRemoveResponsibleLecturerRequestModel {
    @NotNull
    private String id;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize
    private LocalDateTime startDate;

    private String name;

    private String description;

    private int numberOfStudents;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> responsibleLecturers;
}
