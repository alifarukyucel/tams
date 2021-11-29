package nl.tudelft.sem.template.authentication.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class AppUser {
    @Id
    @Column(name = "netid", nullable = false)
    private String netid;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    public String getNetid() {
        return netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
