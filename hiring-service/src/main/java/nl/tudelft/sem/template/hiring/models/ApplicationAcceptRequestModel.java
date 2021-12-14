package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAcceptRequestModel {
    private String courseId;
    private String netId;
    private String duties;
    private int maxHours;
}
