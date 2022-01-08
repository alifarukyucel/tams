package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingTeachingAssistantApplicationResponseModel
        implements Comparable<PendingTeachingAssistantApplicationResponseModel> {
    //Minimum rating for a TA that is considered sufficient.
    //TA's with a sufficient historical rating will be recommended faster than new TA's
    //The new TA's will be recommended faster than TA's with an insufficient historical rating.
    private static final double sufficientTaRating = 5.75d;

    private String courseId;
    private String netId;
    private Float grade;
    private String motivation;
    private Double taRating;

    /**
     * Constructor that constructs a PendingApplicationResponseModel from an application and a rating.
     *
     * @param teachingAssistantApplication the application to get the data from
     * @param rating the historical TA-rating of this person
     */
    public PendingTeachingAssistantApplicationResponseModel(
            TeachingAssistantApplication teachingAssistantApplication, Double rating) {
        this.courseId = teachingAssistantApplication.getCourseId();
        this.netId = teachingAssistantApplication.getNetId();
        this.grade = teachingAssistantApplication.getGrade();
        this.motivation = teachingAssistantApplication.getMotivation();
        this.taRating = rating;
    }

    @Override
    public int compareTo(PendingTeachingAssistantApplicationResponseModel other) {
        //The following multiplies "sufficient" ratings with -1, so that they become a negative value
        //When the applications will be sorted, this means they will be in the following order:
        //First candidates with a sufficient rating
        //Then candidates that don't have a rating yet (They are set to have rating "-1")
        //Lastly the candidates with an insufficient rating
        double rating1 = this.getReformattedRating();
        double rating2 = other.getReformattedRating();
        return (Double.compare(rating1, rating2));
    }

    private double getReformattedRating() {
        double rating = this.getTaRating();

        //Using a switch here is impossible because we are working with doubles
        if (rating >= sufficientTaRating) {
            return rating * -1;
        } else if (rating == -1d) {
            return rating;
        } else {
            return sufficientTaRating - rating;
        }
    }

}
