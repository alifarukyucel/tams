package nl.tudelft.sem.template.hiring.services.communication.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Data
public class CreateContractRequestModel {
    private String courseId;
    private String netId;
    private String duties; 
    private int maxHours;
}