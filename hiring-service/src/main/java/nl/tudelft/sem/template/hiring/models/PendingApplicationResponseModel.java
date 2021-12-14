package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.Application;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingApplicationResponseModel {
    private String courseId;
    private String netId;
    private Float grade;
    private String motivation;
    private Float taRating;

    public PendingApplicationResponseModel(Application application, Float rating) {
        this.courseId = application.getCourseId();
        this.netId = application.getNetId();
        this.grade = application.getGrade();
        this.motivation = application.getMotivation();
        this.taRating = rating;
    }

}
