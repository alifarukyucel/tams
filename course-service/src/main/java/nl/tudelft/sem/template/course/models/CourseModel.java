package nl.tudelft.sem.template.course.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * Course model to be sent through HTTP requests.
 *
 * @created 07/12/2021, 17:37
 */
@Data
public class CourseModel {

    private String id;
    private LocalDateTime startDate;
    private String name;
    private String description;
    private int numberOfStudents;
    private List<String> responsibleLecturers;

    /**
     * Instantiates a new Course model.
     *
     * @param id               the id
     * @param startDate        the start date
     * @param name             the name
     * @param description      the description
     * @param numberOfStudents the number of students
     */
    public CourseModel(String id, LocalDateTime startDate, String name,
                       String description, int numberOfStudents) {
        this.id = id;
        this.startDate = startDate;
        this.name = name;
        this.description = description;
        this.numberOfStudents = numberOfStudents;
    }

    
}
