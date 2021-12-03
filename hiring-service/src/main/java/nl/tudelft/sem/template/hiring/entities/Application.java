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

    @Column("NETID")
    //Not sure how to deal with PK

    @Column("MOTIVATION")
    private String motivation;

    @Column("GRADE")
    private float grade;

    @Column("STATUS")
    private String status;
}
