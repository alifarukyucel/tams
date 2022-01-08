package nl.tudelft.sem.template.authentication.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class AppUser {
    @Id
    @Column(name = "netid", nullable = false)
    private String netid;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
