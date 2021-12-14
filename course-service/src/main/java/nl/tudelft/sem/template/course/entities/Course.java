package nl.tudelft.sem.template.course.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;


/**
 * Course class stores its start date, name, and description.
 * It has a many-to-one relationship with responsible lecturer.
 * A course can exist without a responsible lecturer assigned to it.
 *
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 30/11/2021, 16:37
 */
@Getter
@Setter
@ToString                                            // Not using @Data because of excessive memory consumption
@RequiredArgsConstructor                             // Use Lombok to get rid of getters, setters,
@AllArgsConstructor                                  // constructors and other java boilerplate code
@Entity
@Table(name = "course")
public class Course {

    @Id
    @Column(name = "CourseID")
    private String id;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "NumberOfStudents")
    private int numberOfStudents;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> responsibleLecturers;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
