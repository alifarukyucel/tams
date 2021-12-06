package nl.tudelft.sem.template.hiring.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APPLICATION")
public class Application {

    @Id
    @Column(name = "COURSE_ID")
    //Not sure how to deal with PK
    private int courseId;

    @Column(name = "NETID")
    private int netId;
    //Not sure how to deal with PK

    @Column(name = "MOTIVATION")
    private String motivation;

    @Column(name = "GRADE")
    private float grade;

    @Column(name = "STATUS")
    private String status;
}
