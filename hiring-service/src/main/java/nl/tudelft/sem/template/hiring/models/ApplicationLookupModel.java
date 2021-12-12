package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationLookupModel {
    private String courseId;
    private String netid;
}
