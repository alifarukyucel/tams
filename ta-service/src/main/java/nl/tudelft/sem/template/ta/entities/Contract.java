package nl.tudelft.sem.template.ta.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.ta.entities.compositekeys.ContractId;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@IdClass(ContractId.class)
@Table(name = "contracts")
public class Contract {

    @Id
    String netId;

    @Id
    String courseId;

    @Column(nullable = false)
    Integer maxHours;

    @Column(columnDefinition = "TEXT")
    String duties;

    @Column(nullable = false)
    Boolean signed;
}
