package nl.tudelft.sem.template.ta.models;

import lombok.Data;

import java.util.UUID;

@Data
public class AcceptContractRequestModel {
    // netID of user is available in request header.

    // We need have to course to find the correct contract.
    private String course;
    private boolean accept;
}