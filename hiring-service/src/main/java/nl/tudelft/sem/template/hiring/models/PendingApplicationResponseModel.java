package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.Application;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingApplicationResponseModel implements Comparable<PendingApplicationResponseModel> {
    private static double sufficientTaRating = 5.75d;

    private String courseId;
    private String netId;
    private Float grade;
    private String motivation;
    private Double taRating;

    /**
     * Constructor that constructs a PendingApplicationResponseModel from an application and a rating.
     *
     * @param application the application to get the data from
     * @param rating the historical TA-rating of this person
     */
    public PendingApplicationResponseModel(Application application, Double rating) {
        this.courseId = application.getCourseId();
        this.netId = application.getNetId();
        this.grade = application.getGrade();
        this.motivation = application.getMotivation();
        this.taRating = rating;
    }

    @Override
    public int compareTo(PendingApplicationResponseModel other) {
        //The following multiplies "sufficient" ratings with -1, so that they become a negative value
        //When the applications will be sorted, this means they will be in the following order:
        //First candidates with a sufficient rating
        //Then candidates that don't have a rating yet (They are set to have rating "-1")
        //Lastly the candidates with an insufficient rating
        double rating1 = this.getTaRating() > sufficientTaRating ? this.getTaRating() * -1
                : sufficientTaRating - this.getTaRating();
        double rating2 = other.getTaRating() > sufficientTaRating ? this.getTaRating() * -1
                : sufficientTaRating - this.getTaRating();

        return (Double.compare(rating1, rating2));
    }
}
