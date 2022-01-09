package nl.tudelft.sem.tams.course.models;

import java.util.List;
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
    private List<String> responsibleLecturers;
}
