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
        Assertions.assertEquals("build", contract.getCourseId());
        Assertions.assertEquals(5, contract.getMaxHours());
        Assertions.assertNotNull(contract.getId());
        Assertions.assertFalse(contract.getSigned());
        Assertions.assertEquals("PieterDelft", contract.getNetId());
        Assertions.assertEquals("You need to work!", contract.getDuties());
    }

    @Test
    void testToResponseModel(){
        ContractResponseModel model = contract.toResponseModel();
        Assertions.assertNotNull(model);
        Assertions.assertEquals(contract.getCourseId(), model.getCourse());
        Assertions.assertEquals(contract.getMaxHours(), model.getMaxHours());
        Assertions.assertEquals(contract.getDuties(), model.getDuties());
        Assertions.assertEquals(contract.getSigned(), model.isSigned());
    }
}