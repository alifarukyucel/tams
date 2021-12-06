package nl.tudelft.sem.template.hiring.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APPLICATION")
public class Application {

    @Id
    @Column(name = "COURSE_ID")
    //Not sure how to deal with PK
    private String courseId;

    @Column(name = "NETID")
    private String netId;
    //Not sure how to deal with PK

    @Column(name = "MOTIVATION")
    private String motivation;

    @Column(name = "GRADE")
    private float grade;

    @Column(name = "STATUS")
    private String status;

    public Application(String courseId, String netId, float grade, String motivation) {
        this.courseId = courseId;
        this.netId = netId;
        this.motivation = motivation;
        this.grade = grade;
        //TODO: Make status an enum
        this.status = "Pending";
    }

    /**
     * Checks whether the application meets the requirements
     * As of now the only requirement is the grade >= 6.0
     *
     * @return if the application meets the requirements
     */
    public boolean meetsRequirements() {
        return grade >= 6.0;
    }
}
