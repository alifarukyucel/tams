package nl.tudelft.sem.template.ta.entities;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HourDeclarationTest {

    @Test
    void testBuilder() {
        LocalDateTime time = LocalDateTime.now();
        Contract c1 = new Contract();
        HourDeclaration hour1 = HourDeclaration.builder()
            .id(UUID.randomUUID())
            .workedTime(15)
            .approved(true)
            .reviewed(true)
            .date(time)
            .desc("test")
            .contract(c1)
            .build();

        Assertions.assertNotNull(hour1.getId());
        Assertions.assertEquals(15, hour1.getWorkedTime());
        Assertions.assertTrue(hour1.getApproved());
        Assertions.assertTrue(hour1.getReviewed());
        Assertions.assertEquals(time, hour1.getDate());
        Assertions.assertEquals("test", hour1.getDesc());
        Assertions.assertEquals(c1, hour1.getContract());
    }


    @Test
    void testSetters() {
        LocalDateTime time = LocalDateTime.now();
        Contract c1 = new Contract();
        HourDeclaration hour1 = new HourDeclaration();
        hour1.setId(UUID.randomUUID());
        hour1.setWorkedTime(15);
        hour1.setApproved(true);
        hour1.setReviewed(true);
        hour1.setDate(time);
        hour1.setDesc("test");
        hour1.setContract(c1);

        Assertions.assertNotNull(hour1.getId());
        Assertions.assertEquals(15, hour1.getWorkedTime());
        Assertions.assertTrue(hour1.getApproved());
        Assertions.assertEquals(time, hour1.getDate());
        Assertions.assertEquals("test", hour1.getDesc());
        Assertions.assertEquals(c1, hour1.getContract());
    }

}