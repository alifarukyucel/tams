package nl.tudelft.sem.tams.ta.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.tams.ta.entities.compositekeys.ContractId;

@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@IdClass(ContractId.class)
@Table(name = "contracts")
public class Contract {

    // The min and max rating of a TA.
    private final transient int minRating = 0;
    private final transient int maxRating = 10;

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

    @Column
    private int actualWorkedHours;


    /**
     * Set the rating of this contract.
     * Rating needs to be >= 0 and <= 10
     *
     * @param rating new value
     * @throws IllegalArgumentException if rating is < 0 or > 10
     */
    public void setRating(double rating) throws IllegalArgumentException {
        if (rating < minRating || rating > maxRating) {
            throw new IllegalArgumentException("Rating must be between 0 and 10.");
        }

        this.rating = rating;
    }

    /**
     * Set the actual worked hours of this contract.
     * Hours need to be larger or equal to 0
     *
     * @param hours new value
     * @throws IllegalArgumentException hours < 0
     */
    public void setActualWorkedHours(int hours) throws IllegalArgumentException {
        if (hours < 0) {
            throw new IllegalArgumentException("Actual worked hours cannot be smaller than 0");
        }

        this.actualWorkedHours = hours;
    }
}
