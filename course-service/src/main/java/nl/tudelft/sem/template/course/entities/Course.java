package nl.tudelft.sem.template.course.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


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
@AllArgsConstructor(access = AccessLevel.PUBLIC)     // constructors and other java boilerplate code
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
        return id != null && Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
