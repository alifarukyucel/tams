package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

@Data
public class RegistrationRequestModel {
    private String netid;
    private String password;
}