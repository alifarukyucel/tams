package nl.tudelft.sem.template.hiring.models;

import lombok.Data;

@Data
public class ApplicationRequestModel {
    private String courseId;
    private float grade;
    private String motivation;

    public ApplicationRequestModel(String courseId, float grade, String motivation) {
        this.courseId = courseId;
        this.grade = grade;
        this.motivation = motivation;
    }



}
