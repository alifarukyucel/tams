package nl.tudelft.sem.template.ta.models;

import lombok.Data;

import javax.persistence.Column;
import java.util.UUID;

@Data
public class GetContractResponseModel {
    private String course;
    private String duties;
    private int maxHours;
    boolean signed;
}
