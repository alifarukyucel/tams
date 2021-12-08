package nl.tudelft.sem.template.hiring.models;

import lombok.Data;

@Data
public class RetrieveStatusModel {

    private String courseId;
    private String netid;
    private String motivation;
    private float grade;
    private String status;

    public RetrieveStatusModel(String courseId, String netid, String motivation, float grade, String status) {
        this.courseId = courseId;
        this.netid = netid;
        this.motivation = motivation;
        this.grade = grade;
        this.status = status;
    }
}
