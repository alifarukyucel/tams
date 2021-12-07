package nl.tudelft.sem.template.ta.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class RetrieveHoursToBeApprovedRequestModel {
    private String course;
}
