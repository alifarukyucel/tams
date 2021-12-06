package nl.tudelft.sem.template.ta.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "contracts")
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue
    private UUID id;

    int maxHours;

    @Column(nullable = false)
    String netId;

    @Column(nullable = false)
    String courseId;

    boolean signed;

}
