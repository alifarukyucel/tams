package nl.tudelft.sem.tams.course.models;

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
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> responsibleLecturers;
}
