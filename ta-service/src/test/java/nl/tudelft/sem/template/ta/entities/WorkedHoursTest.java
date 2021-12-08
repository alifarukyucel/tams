package nl.tudelft.sem.template.ta.entities;

import nl.tudelft.sem.template.ta.models.HourResponseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

class WorkedHoursTest {

    private WorkedHours hours;
    private Contract contract;
    private Date time;

    @BeforeEach
    void setUp(){
        time = Calendar.getInstance().getTime();
        contract = new Contract();
        hours = WorkedHours.builder()
                .id(UUID.randomUUID())
                .workedTime(15)
                .approved(true)
                .date(time)
                .reviewed(true)
                .desc("test")
                .contract(contract)
                .build();
    }

    @Test
    void testBuilder() {
        Assertions.assertNotNull(hours.getId());
        Assertions.assertEquals(15, hours.getWorkedTime());
        Assertions.assertTrue(hours.isApproved());
        Assertions.assertTrue(hours.isReviewed());
        Assertions.assertEquals(time, hours.getDate());
        Assertions.assertEquals("test", hours.getDesc());
        Assertions.assertEquals(contract, hours.getContract());
    }


    @Test
    void testSetters() {
        time = Calendar.getInstance().getTime();
        contract = new Contract();
        hours = new WorkedHours();
        hours.setId(UUID.randomUUID());
        hours.setWorkedTime(15);
        hours.setApproved(true);
        hours.setDate(time);
        hours.setReviewed(true);
        hours.setDesc("test");
        hours.setContract(contract);

        Assertions.assertNotNull(hours.getId());
        Assertions.assertEquals(15, hours.getWorkedTime());
        Assertions.assertTrue(hours.isApproved());
        Assertions.assertTrue(hours.isReviewed());
        Assertions.assertEquals(time, hours.getDate());
        Assertions.assertEquals("test", hours.getDesc());
        Assertions.assertEquals(contract, hours.getContract());
    }

    @Test
    void testToResponseModel(){
        HourResponseModel model = hours.toResponseModel();
        Assertions.assertNotNull(model);
        Assertions.assertEquals(hours.getDate(), model.getDate());
        Assertions.assertEquals(hours.getDesc(), model.getDescription());
        Assertions.assertEquals(hours.getWorkedTime(), model.getWorkedTime());
        Assertions.assertEquals(contract.getNetId(), model.getTa());
    }

}