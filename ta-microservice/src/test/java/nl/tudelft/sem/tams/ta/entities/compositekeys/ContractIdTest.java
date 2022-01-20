package nl.tudelft.sem.tams.ta.entities.compositekeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ContractIdTest {

    /**
     * Another test class purely to bump test coverage.
     */
    @Test
    void testEquals() {
        ContractId c1 = ContractId.builder().withNetId("MCanning").withCourseId("CSE2310").build();
        ContractId c2 = ContractId.builder().withNetId("MCanning").withCourseId("CSE2310").build();
        assertEquals(c1, c2);
    }

    @Test
    void builder() {
        ContractId contractId = ContractId.builder().withNetId("MCanning").withCourseId("CSE2310").build();
        assertThat(contractId.getNetId()).isEqualTo("MCanning");
        assertThat(contractId.getCourseId()).isEqualTo("CSE2310");
    }
}