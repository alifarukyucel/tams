package nl.tudelft.sem.tams.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingAssistantApplicationRequestModel {
    private String courseId;
    private float grade;
    private String motivation;
    private String contactEmail;

    public TeachingAssistantApplicationRequestModel(String courseId, float grade, String motivation) {
        this(courseId, grade, motivation, null);
    }
}
