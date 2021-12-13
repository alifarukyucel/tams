package nl.tudelft.sem.template.hiring.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.compositeKeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
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

    public static Application createPendingApplication(String courseId, String netId, float grade, String motivation) {
        Application application = new Application();
        application.setCourseId(courseId);
        application.setNetId(netId);
        application.setGrade(grade);
        application.setMotivation(motivation);
        application.setStatus(ApplicationStatus.PENDING);
        return application;
    }

    public static Application createRejectedApplication(String courseId, String netId, float grade, String motivation) {
        Application application = new Application();
        application.setCourseId(courseId);
        application.setNetId(netId);
        application.setGrade(grade);
        application.setMotivation(motivation);
        application.setStatus(ApplicationStatus.REJECTED);
        return application;
    }

    public static Application createAcceptedApplication(String courseId, String netId, float grade, String motivation) {
        Application application = new Application();
        application.setCourseId(courseId);
        application.setNetId(netId);
        application.setGrade(grade);
        application.setMotivation(motivation);
        application.setStatus(ApplicationStatus.REJECTED);
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
