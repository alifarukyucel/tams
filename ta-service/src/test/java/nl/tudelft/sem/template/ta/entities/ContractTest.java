package nl.tudelft.sem.template.ta.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class ContractTest {

    @Test
    void testBuilder() {
        Contract c1 = new Contract.Builder()
            .setCourseId("build")
            .setMaxHours(5)
            .setId(UUID.randomUUID())
            .setSigned(false)
            .setNetId("PieterDelft")
            .createContract();

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