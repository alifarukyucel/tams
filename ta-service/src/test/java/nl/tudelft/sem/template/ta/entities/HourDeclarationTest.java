package nl.tudelft.sem.template.ta.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

class HourDeclarationTest {

    @Test
    void testBuilder() {
        Date time = Calendar.getInstance().getTime();
        Contract contract = new Contract();
        HourDeclaration hours = HourDeclaration.builder()
                .id(UUID.randomUUID())
                .workedTime(15)
                .approved(true)
                .date(time)
                .reviewed(true)
                .desc("test")
                .contract(contract)
                .build();

        Assertions.assertNotNull(hours.getId());
        Assertions.assertEquals(15, hours.getWorkedTime());
        Assertions.assertTrue(hours.getApproved());
        Assertions.assertTrue(hours.getReviewed());
        Assertions.assertEquals(time, hours.getDate());
        Assertions.assertEquals("test", hours.getDesc());
        Assertions.assertEquals(contract, hours.getContract());
    }


    @Test
    void testSetters() {
        Date time = Calendar.getInstance().getTime();
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