package nl.tudelft.sem.template.ta.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "HourDeclarations")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class HourDeclaration {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer workedTime;

    @Column(nullable = false)
    private Boolean approved;

    @Column(nullable = false)
    private Boolean reviewed;

    private Date date;

    private String desc;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Contract contract;

}
