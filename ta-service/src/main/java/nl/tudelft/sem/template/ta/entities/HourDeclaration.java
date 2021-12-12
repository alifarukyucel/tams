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

import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AccessLevel;

import nl.tudelft.sem.template.ta.models.HourResponseModel;

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

    /**
     * Create an instance of HourResponseModel based on this WorkedHours.
     * @return ContractResponseModel of this contract.
     */
    // TODO: Move this to the HourResponseModel itself (as per Martin request).
    public HourResponseModel toResponseModel(){
        return new HourResponseModel(date, desc, workedTime, approved, contract.getNetId());
    }

}
