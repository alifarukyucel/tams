package nl.tudelft.sem.tams.ta.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteHourDeclarationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HourDeclarationTest {
    //Arbitrary time to use for the hour declarations
    private static final LocalDateTime arbitraryTime = LocalDateTime.of(2000, 1, 1, 0, 0);

    @Test
    void testBuilder() {
        LocalDateTime time = arbitraryTime;
        Contract c1 = new Contract();
        HourDeclaration hour1 = new ConcreteHourDeclarationBuilder()
            .withId(UUID.randomUUID())
            .withWorkedTime(15)
            .withApproved(true)
            .withReviewed(true)
            .withDate(time)
            .withDescription("test")
            .withContractId(c1)
            .build();

        Assertions.assertNotNull(hour1.getId());
        Assertions.assertEquals(15, hour1.getWorkedTime());
        Assertions.assertTrue(hour1.getApproved());
        Assertions.assertTrue(hour1.getReviewed());
        Assertions.assertEquals(time, hour1.getDate());
        Assertions.assertEquals("test", hour1.getDescription());
        Assertions.assertEquals(c1, hour1.getContract());
    }


    @Test
    void testSetters() {
        LocalDateTime time = arbitraryTime;
        Contract c1 = new Contract();
        HourDeclaration hour1 = new HourDeclaration();
        hour1.setId(UUID.randomUUID());
        hour1.setWorkedTime(15);
        hour1.setApproved(true);
        hour1.setReviewed(true);
        hour1.setDate(time);
        hour1.setDescription("test");
        hour1.setContract(c1);

        Assertions.assertNotNull(hour1.getId());
        Assertions.assertEquals(15, hour1.getWorkedTime());
        Assertions.assertTrue(hour1.getApproved());
        Assertions.assertEquals(time, hour1.getDate());
        Assertions.assertEquals("test", hour1.getDescription());
        Assertions.assertEquals(c1, hour1.getContract());
    }

}