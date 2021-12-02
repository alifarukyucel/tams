package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

@Data
public class LoginResponseModel {
    private final String token;
}