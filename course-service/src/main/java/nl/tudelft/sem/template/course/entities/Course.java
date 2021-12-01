package nl.tudelft.sem.template.course.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 30/11/2021, 16:37
 *
 *
 * Course class stores its start date, name, and description.
 * It has a many-to-one relationship with responsible lecturer.
 * A course can exist without a responsible lecturer assigned to it.
 *
 */
@Data                   // Use Lombok to get rid of getters, setters,
@NoArgsConstructor      // constructors and other java boilerplate code
@AllArgsConstructor     // such as toString(), equals(), hashCode()
@Entity
@Table(name = "COURSE")
public class Course {

    @Id
    @Column(name = "COURSE_ID")
    @SequenceGenerator(
            name = "course_sequence",
            sequenceName = "course_sequence",
            allocationSize = 1 // increment by one
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "course_sequence"
    )
    private int id;

//    @ManyToOne
//    @JoinColumn(name = "responsible_lecturer_id")
//    private Lecturer responsibleLecturer;

    @Column(name = "START_DATE")  // just in case
    private Date startDate;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;

}
