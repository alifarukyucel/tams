package nl.tudelft.sem.template.ta.entities;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContractTest {

    @Test
    void testBuilder() {
        Contract c1 = Contract.builder()
            .courseId("build")
            .maxHours(5)
            .id(UUID.randomUUID())
            .signed(false)
            .netId("PieterDelft")
            .build();

        Assertions.assertEquals("build", c1.getCourseId());
        Assertions.assertEquals(5, c1.getMaxHours());
        Assertions.assertNotNull(c1.getId());
        Assertions.assertFalse(c1.isSigned());
        Assertions.assertEquals("PieterDelft", c1.getNetId());

    }

    @Test
    void testSetters() {
        Contract c1 = new Contract();
        c1.setCourseId("build");
        c1.setMaxHours(5);
        c1.setId(UUID.randomUUID());
        c1.setSigned(false);
        c1.setNetId("PieterDelft");

        Assertions.assertEquals("build", c1.getCourseId());
        Assertions.assertEquals(5, c1.getMaxHours());
        Assertions.assertNotNull(c1.getId());
        Assertions.assertFalse(c1.isSigned());
        Assertions.assertEquals("PieterDelft", c1.getNetId());

    }
}