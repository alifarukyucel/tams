package nl.tudelft.sem.tams.ta.entities;

import java.time.LocalDateTime;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
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

    private LocalDateTime date;

    private String description;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Contract contract;
}
