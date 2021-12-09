package nl.tudelft.sem.template.ta.entities;

import nl.tudelft.sem.template.ta.models.ContractResponseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class ContractTest {

    private Contract contract;

    @BeforeEach
    void setUp(){
        contract = Contract.builder()
                .courseId("build")
                .maxHours(5)
                .id(UUID.randomUUID())
                .signed(false)
                .netId("PieterDelft")
                .duties("You need to work!")
                .build();
    }

    @Test
    void testBuilder() {
        Assertions.assertEquals("build", contract.getCourseId());
        Assertions.assertEquals(5, contract.getMaxHours());
        Assertions.assertNotNull(contract.getId());
        Assertions.assertFalse(contract.getSigned());
        Assertions.assertEquals("PieterDelft", contract.getNetId());
        Assertions.assertEquals("You need to work!", contract.getDuties());
    }

    @Test
    void testSetters() {
        contract = new Contract();
        contract.setCourseId("build");
        contract.setMaxHours(5);
        contract.setId(UUID.randomUUID());
        contract.setSigned(false);
        contract.setNetId("PieterDelft");
        contract.setDuties("You need to work!");

        Assertions.assertEquals("build", contract.getCourseId());
        Assertions.assertEquals(5, contract.getMaxHours());
        Assertions.assertNotNull(contract.getId());
        Assertions.assertFalse(contract.getSigned());
        Assertions.assertEquals("PieterDelft", contract.getNetId());
        Assertions.assertEquals("You need to work!", contract.getDuties());
    }

}