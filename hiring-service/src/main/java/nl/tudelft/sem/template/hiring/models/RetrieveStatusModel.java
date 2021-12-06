package nl.tudelft.sem.template.hiring.models;

public class RetrieveStatusModel {

    String courseId;
    String netid;
    String motivation;
    float grade;
    String status;

    public RetrieveStatusModel(String courseId, String netid, String motivation, float grade, String status) {
        this.courseId = courseId;
        this.netid = netid;
        this.motivation = motivation;
        this.grade = grade;
        this.status = status;
    }
}
