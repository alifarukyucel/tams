package nl.tudelft.sem.tams.ta.entities.compositekeys;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContractIdTest {

    /**
     * Another test class purely to bump test coverage.
     */
    @Test
    void testEquals() {
        ContractId c1 = ContractId.builder().withNetId("MCanning").withCourseId("CSE2310").build();
        ContractId c2 = ContractId.builder().withNetId("MCanning").withCourseId("CSE2310").build();
        Assertions.assertEquals(c1, c2);
    }

    @Test
    void builder() {
        ContractId.builder().withNetId("MCanning").withCourseId("CSE2310").build();
    }
}