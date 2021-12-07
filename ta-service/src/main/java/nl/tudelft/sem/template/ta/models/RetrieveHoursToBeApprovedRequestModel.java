package nl.tudelft.sem.template.ta.models;

import lombok.Data;

@Data
public class RetrieveHoursToBeApprovedRequestModel {
    // NetID of user (available in header) needs to be checked for permissions (reposible lecturer)
    private String course;
}
