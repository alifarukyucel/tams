package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingApplicationResponseModel {
    private String courseId;
    private String netId;
    private Float grade;
    private String motivation;
    private Float taRating;

    /**
     * Constructor that constructs a PendingApplicationResponseModel from an application and a rating.
     *
     * @param teachingAssistantApplication the application to get the data from
     * @param rating the historical TA-rating of this person
     */
    public PendingApplicationResponseModel(TeachingAssistantApplication teachingAssistantApplication, Float rating) {
        this.courseId = teachingAssistantApplication.getCourseId();
        this.netId = teachingAssistantApplication.getNetId();
        this.grade = teachingAssistantApplication.getGrade();
        this.motivation = teachingAssistantApplication.getMotivation();
        this.taRating = rating;
    }

}
