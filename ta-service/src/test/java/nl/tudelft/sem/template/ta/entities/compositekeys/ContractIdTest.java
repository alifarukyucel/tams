package nl.tudelft.sem.template.ta.entities.compositekeys;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContractIdTest {

    /**
     * Another test class purely to bump test coverage.
     */
    @Test
    void testEquals() {
        ContractId c1 = ContractId.builder().netId("MCanning").courseId("CSE2310").build();
        ContractId c2 = ContractId.builder().netId("MCanning").courseId("CSE2310").build();
        Assertions.assertEquals(c1, c2);
    }

    @Test
    void builder() {
        ContractId.builder().netId("MCanning").courseId("CSE2310").build();
    }
}