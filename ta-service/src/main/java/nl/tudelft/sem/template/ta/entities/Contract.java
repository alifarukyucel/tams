package nl.tudelft.sem.template.ta.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.ta.entities.compositekeys.ContractId;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@IdClass(ContractId.class)
@Table(name = "contracts")
public class Contract {

    @Id
    private String netId;

    @Id
    private String courseId;

    @Column(nullable = false)
    private Integer maxHours;

    @Column(columnDefinition = "TEXT")
    private String duties;

    @Column(nullable = false)
    private Boolean signed;

    @Column(columnDefinition = "double precision default 0")
    private double rating;

    /**
     * Set the rating of this contract.
     * Rating needs to be >= 0 and <= 10
     *
     * @param rating new value
     * @throws IllegalArgumentException if rating is < 0 or > 10
     */
    public void setRating(double rating) {
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10.");
        }

        this.rating = rating;
    }
}
