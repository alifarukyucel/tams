package nl.tudelft.sem.template.ta.services.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CourseInformationResponseModel {
    private String id;

    private LocalDateTime startDate;

    private String name;

    private String description;

    private int numberOfStudents;

    private List<String> responsibleLecturers;
}
