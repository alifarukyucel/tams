package nl.tudelft.sem.template.ta.entities;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nl.tudelft.sem.template.ta.entities.builders.ConcreteContractBuilder;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContractTest {

    private Contract contract;

    @BeforeEach
    void setUp() {
        contract = new ConcreteContractBuilder()
                .withCourseId("build")
                .withMaxHours(5)
                .withRating(5)
                .withSigned(false)
                .withNetId("PieterDelft")
                .withDuties("You need to work!")
                .build();
    }

    @Test
    void testBuilder() {
        Assertions.assertEquals("build", contract.getCourseId());
        Assertions.assertEquals(5, contract.getMaxHours());
        Assertions.assertFalse(contract.getSigned());
        Assertions.assertEquals("PieterDelft", contract.getNetId());
        Assertions.assertEquals("You need to work!", contract.getDuties());
    }

    /**
     * Boundary test.
     */
    @Test
    void lowerBoundRatingOffPoint() {
        // act
        ThrowableAssert.ThrowingCallable action = () -> contract.setRating(Math.nextDown(0.0d));

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(contract.getRating()).isEqualTo(5);
    }

    /**
     * Boundary test.
     */
    @Test
    void upperBoundRatingOffPoint() {
        // act
        ThrowableAssert.ThrowingCallable action = () -> contract.setRating(Math.nextUp(10.0d));

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(contract.getRating()).isEqualTo(5);
    }

    /**
     * Boundary test.
     */
    @Test
    void lowerBoundRatingOnPoint() {
        // act
        contract.setRating(0.0d);

        // assert
        assertThat(contract.getRating()).isEqualTo(0.0d);
    }

    /**
     * Boundary test.
     */
    @Test
    void upperBoundRatingOnPoint() {
        // act
        contract.setRating(10.0d);

        // assert
        assertThat(contract.getRating()).isEqualTo(10.0d);
    }

    @Test
    void testSetters() {
        contract = new Contract();
        contract.setCourseId("build");
        contract.setMaxHours(5);
        contract.setSigned(false);
        contract.setNetId("PieterDelft");
        contract.setRating(10);
        contract.setDuties("You need to work!");

        Assertions.assertEquals("build", contract.getCourseId());
        Assertions.assertEquals(5, contract.getMaxHours());
        Assertions.assertEquals(10, contract.getRating());
        Assertions.assertFalse(contract.getSigned());
        Assertions.assertEquals("PieterDelft", contract.getNetId());
        Assertions.assertEquals("You need to work!", contract.getDuties());
    }
}