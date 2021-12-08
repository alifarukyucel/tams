package nl.tudelft.sem.template.ta.models;

import lombok.Data;

import javax.persistence.Column;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class ContractResponseModel {
    private String course;
    private String duties;
    private int maxHours;
    private boolean signed;
}
