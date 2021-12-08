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
                .desc("test")
                .contract(contract)
                .build();
    }

    @Test
    void testBuilder() {
        Assertions.assertNotNull(hours.getId());
        Assertions.assertEquals(15, hours.getWorkedTime());
        Assertions.assertTrue(hours.isApproved());
        Assertions.assertEquals(time, hours.getDate());
        Assertions.assertEquals("test", hours.getDesc());
        Assertions.assertEquals(contract, hours.getContract());
    }


    @Test
    void testSetters() {
        Assertions.assertNotNull(hours.getId());
        Assertions.assertEquals(15, hours.getWorkedTime());
        Assertions.assertTrue(hours.isApproved());
        Assertions.assertEquals(time, hours.getDate());
        Assertions.assertEquals("test", hours.getDesc());
        Assertions.assertEquals(contract, hours.getContract());
    }

    private Date date;
    private String desc;
    private int workedTime;
    private boolean approved;
    private String ta;

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