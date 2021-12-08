package nl.tudelft.sem.template.ta.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AccessLevel;

import nl.tudelft.sem.template.ta.models.HourResponseModel;

@Entity
@Builder
@Table(name = "workedHours")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@EqualsAndHashCode
public class WorkedHours {

    @Id
    @GeneratedValue
    private UUID id;
    int workedTime;
    boolean approved;
    boolean reviewed; // internal flag to see if it has been processed by an responsible lecturer yet.
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
