package nl.tudelft.sem.template.ta.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContractTest {

    private Contract contract;

    @BeforeEach
    void setUp() {
        contract = Contract.builder()
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