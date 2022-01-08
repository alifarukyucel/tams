package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveTeachingAssistantApplicationStatusModel {
    private String courseId;
    private String netid;
    private String motivation;
    private float grade;
    private ApplicationStatus status;

    /**
     * Constructor that constructs a RetrieveStatusModel from an application and courseId.
     *
     * @param teachingAssistantApplication the application to retrieve a status from
     * @return RetrieveStatusModel of given application
     */
    public static RetrieveTeachingAssistantApplicationStatusModel fromApplication(
            TeachingAssistantApplication teachingAssistantApplication) {
        return new RetrieveTeachingAssistantApplicationStatusModel(
                teachingAssistantApplication.getCourseId(),
                teachingAssistantApplication.getNetId(),
                teachingAssistantApplication.getMotivation(),
                teachingAssistantApplication.getGrade(),
                teachingAssistantApplication.getStatus()
        );
    }
}
