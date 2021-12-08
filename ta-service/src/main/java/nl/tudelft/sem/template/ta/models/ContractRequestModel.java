package nl.tudelft.sem.template.ta.models;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ContractRequestModel {
    private String course;
}
