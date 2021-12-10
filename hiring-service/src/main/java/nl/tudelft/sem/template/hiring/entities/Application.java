package nl.tudelft.sem.template.hiring.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.compositeKeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APPLICATION")
@IdClass(ApplicationKey.class)
public class Application {
    @Id
    @Column(name = "COURSE_ID")
    private String courseId;

    @Id
    @Column(name = "NETID")
    private String netId;

    @Column(name = "GRADE")
    private float grade;

    @Column(name = "MOTIVATION")
    private String motivation;

    @Column(name = "STATUS")
    private ApplicationStatus status;

    /**
     * Create an application with the status "Pending"
     *
     * @param courseId String courseId
     * @param netId String netId
     * @param grade float grade
     * @param motivation String motivation
     * @return a newly created instance of an Application with the status "Pending" and the given parameters.
     */
    public static Application createPendingApplication(String courseId, String netId, float grade, String motivation) {
        Application application = new Application();
        application.setCourseId(courseId);
        application.setNetId(netId);
        application.setGrade(grade);
        application.setMotivation(motivation);
        application.setStatus(ApplicationStatus.PENDING);
        return application;
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
