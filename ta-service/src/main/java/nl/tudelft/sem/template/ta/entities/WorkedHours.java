package nl.tudelft.sem.template.ta.entities;

import java.util.Date;
import java.util.UUID;
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
import nl.tudelft.sem.template.ta.models.HourResponseModel;

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
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Contract contract;

    /**
     * Create an instance of HourResponseModel based on this WorkedHours.
     * @return ContractResponseModel of this contract.
     */
    public HourResponseModel toResponseModel(){
        return new HourResponseModel(date, desc, workedTime, approved, contract.getNetId());
    }

}
