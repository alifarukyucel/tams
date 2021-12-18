package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveStatusModel {
    private String courseId;
    private String netid;
    private String motivation;
    private float grade;
    private ApplicationStatus status;

    /**
     * Constructor that constructs a RetrieveStatusModel from an application and courseId.
     *
     * @param application
     * @return RetrieveStatusModel of given application
     */
    public static RetrieveStatusModel fromApplication(Application application) { //add String courseId as argument
        return new RetrieveStatusModel(
                application.getCourseId(),
                application.getNetId(),
                application.getMotivation(),
                application.getGrade(),
                application.getStatus()
        );
    }
}
