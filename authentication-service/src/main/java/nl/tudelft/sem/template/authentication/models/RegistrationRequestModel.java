package nl.tudelft.sem.template.authentication.models;

public class RegistrationRequestModel {
    private String netid;
    private String password;

    public RegistrationRequestModel() {
    }

    public RegistrationRequestModel(String username, String password) {
        this.netid = username;
        this.password = password;
    }

    public String getNetid() {
        return netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}