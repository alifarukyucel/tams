package nl.tudelft.sem.template.hiring.models;

public class RetrieveStatusModel {

    String course_id;
    String netid;
    String motivation;
    float grade;
    String status;

    public RetrieveStatusModel(String course_id, String netid, String motivation, float grade, String status) {
        this.course_id = course_id;
        this.netid = netid;
        this.motivation = motivation;
        this.grade = grade;
        this.status = status;
    }
}
