package nl.tudelft.sem.tams.authentication.models;

import lombok.Data;

@Data
public class LoginRequestModel {
    private String netid;
    private String password;
}