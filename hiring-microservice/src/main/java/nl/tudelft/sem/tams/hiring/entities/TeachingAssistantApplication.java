package nl.tudelft.sem.tams.hiring.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationRequestModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "TA_APPLICATION")
@IdClass(TeachingAssistantApplicationKey.class)
public class TeachingAssistantApplication {

    // Lowest possible grade that can be achieved
    private static final transient float minGrade = 1.0f;

    // Highest possible grade that can be achieved
    private static final transient float maxGrade = 10.0f;

    // Minimum grade that is needed in order to be a TA
    private static final transient float reqGrade = 6.0f;

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

    @Column(name = "CONTACT_EMAIL")
    private String contactEmail;

    public TeachingAssistantApplication(String courseId, String netId, float grade, String motivation,
                                        ApplicationStatus status) {
        this(courseId, netId, grade, motivation, status, null);
    }

    /**
     * Create a TA-application with the status "Pending".
     *
     * @param courseId String courseId
     * @param netId String netId
     * @param grade float grade
     * @param motivation String motivation
     * @return a newly created instance of an Application with the status "Pending".
     */
    public static TeachingAssistantApplication createPendingApplication(String courseId, String netId,
                                                                        float grade, String motivation,
                                                                        String contactEmail) {
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication();
        teachingAssistantApplication.setCourseId(courseId);
        teachingAssistantApplication.setNetId(netId);
        teachingAssistantApplication.setGrade(grade);
        teachingAssistantApplication.setMotivation(motivation);
        teachingAssistantApplication.setContactEmail(contactEmail);
        teachingAssistantApplication.setStatus(ApplicationStatus.PENDING);
        return teachingAssistantApplication;
    }

    /**
     * Create a TA-application with the status "Pending" from a requestModel.
     *
     * @param model TeachingAssistantApplicationRequestModel model
     * @param netId String netId
     * @return a newly created instance of an Application with the status "Pending".
     */
    public static TeachingAssistantApplication createPendingApplication(TeachingAssistantApplicationRequestModel model,
                                                                        String netId) {
        return new TeachingAssistantApplication(model.getCourseId(),
                netId,
                model.getGrade(),
                model.getMotivation(),
                ApplicationStatus.PENDING,
                model.getContactEmail());
    }

    /**
     * Checks whether the application meets the requirements
     * As of now the only requirement is the grade >= 6.0
     *
     * @return if the application meets the requirements
     */
    public boolean meetsRequirements() {
        return grade >= reqGrade;
    }

    /**
     * Checks if the grade is a valid grade (Between 1.0 and 10.0 inclusive)
     *
     * @return if the grade is a valid grade
     */
    public boolean hasValidGrade() {
        return grade >= minGrade && grade <= maxGrade;
    }

}
