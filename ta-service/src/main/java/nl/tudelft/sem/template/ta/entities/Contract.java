package nl.tudelft.sem.template.ta.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "contracts")
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue
    private UUID id;

    int maxHours;

    @Column(nullable = false)
    String netId;

    @Column(nullable = false)
    String courseId;

    boolean signed;

    private Contract(UUID id, int maxHours, String netId, String courseId, boolean signed) {
        this.id = id;
        this.maxHours = maxHours;
        this.netId = netId;
        this.courseId = courseId;
        this.signed = signed;
    }

    public static class Builder {

        private UUID id;
        private int maxHours;
        private String netId;
        private String courseId;
        private boolean signed;

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setMaxHours(int maxHours) {
            this.maxHours = maxHours;
            return this;
        }

        public Builder setNetId(String netId) {
            this.netId = netId;
            return this;
        }

        public Builder setCourseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder setSigned(boolean signed) {
            this.signed = signed;
            return this;
        }

        public Contract createContract() {
            return new Contract(id, maxHours, netId, courseId, signed);
        }
    }

}
