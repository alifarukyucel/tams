package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.Application;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedApplicationRequestModel extends ApplicationRequestModel {
    private Float taRating;

    public ExtendedApplicationRequestModel (Application application, Float rating) {
        super(application.getCourseId(), application.getGrade(), application.getMotivation());
        this.taRating = rating;
    }
}
