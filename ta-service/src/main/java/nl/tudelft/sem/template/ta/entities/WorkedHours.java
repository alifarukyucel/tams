package nl.tudelft.sem.template.ta.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
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
@Table(name = "workedHours")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class WorkedHours {

    @Id
    @GeneratedValue
    private UUID id;
    int workedTime;
    boolean approved;
    Date date;
    String desc;
    @ManyToOne(optional = false)
    Contract contract;

}
