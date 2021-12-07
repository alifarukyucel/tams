package nl.tudelft.sem.template.ta.models;


import lombok.Data;

import java.util.Date;

@Data
public class SubmitHoursRequestModel {
    // We need the course to find the contract.
    private String course;

    private boolean accept;
    private int workedTime;
    private boolean approved;
    private Date date;
    private String desc;
}
