package nl.tudelft.sem.template.authentication.models;

public class LoginResponseModel {
    private final String token;

    public LoginResponseModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}