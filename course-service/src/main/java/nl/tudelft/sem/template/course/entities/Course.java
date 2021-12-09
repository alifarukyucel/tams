package nl.tudelft.sem.template.course.entities;

import static javax.persistence.GenerationType.SEQUENCE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Course class stores its start date, name, and description.
 * It has a many-to-one relationship with responsible lecturer.
 * A course can exist without a responsible lecturer assigned to it.
 *
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 30/11/2021, 16:37
 */
@Data                                                // Use Lombok to get rid of getters, setters,
@NoArgsConstructor(access = AccessLevel.PUBLIC)      // constructors and other java boilerplate code
@AllArgsConstructor(access = AccessLevel.PUBLIC)     // such as toString(), equals(), hashCode()
@Entity
@Table(name = "course")
public class Course {

    @Id
    @Column(name = "CourseID")
    private String id;

    @Column(name = "StartDate")
    private Date startDate;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "NumberOfStudents")
    private int numberOfStudents;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> responsibleLecturers;

}
