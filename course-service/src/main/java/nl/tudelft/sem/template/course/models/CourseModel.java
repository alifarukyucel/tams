package nl.tudelft.sem.template.course.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ali Faruk Yücel
 * @version 1.0
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

    public CourseModel(String id, LocalDateTime startDate, String name, String description, int numberOfStudents) {
        this.id = id;
        this.startDate = startDate;
        this.name = name;
        this.description = description;
        this.numberOfStudents = numberOfStudents;
    }

    
}
