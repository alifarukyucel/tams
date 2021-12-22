package nl.tudelft.sem.template.hiring.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "APPLICATION")
@IdClass(ApplicationKey.class)
public class Application {

    // Lowest possible grade that can be achieved
    private static final transient float minGrade = 1.0f;

    // Highest possible grade that can be achieved
    private static final transient float maxGrade = 10.0f;

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
     * Create an application with the status "Pending".
     *
     * @param courseId String courseId
     * @param netId String netId
     * @param grade float grade
     * @param motivation String motivation
     * @return a newly created instance of an Application with the status "Pending".
     */
    public static Application createPendingApplication(String courseId, String netId,
                                                       float grade, String motivation) {
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
    public boolean meetsRequirements() throws IllegalArgumentException {
        return grade >= minGrade && grade <= maxGrade && grade >= 6.0f;
    }
    
}
