package nl.tudelft.sem.template.ta.entities.builders;

import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HourDeclarationBuilderTest {

    private HourDeclaration hourDeclaration;

    @Test
    public void testWithId() {
        //arrange
        UUID id = UUID.randomUUID();

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withId(id)
                .build();

        //assert
        assertThat(hourDeclaration.getId()).isEqualTo(id);
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
    }

    @Test
    public void testWithWorkedTime() {
        //arrange
        UUID id = UUID.randomUUID();

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withId(id)
                .build();

        //assert
        assertThat(hourDeclaration.getId()).isEqualTo(id);
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
    }

    @Test
    public void testAll() {
        //arrange
        UUID id = UUID.randomUUID();
        Integer workedTime = 60;
        boolean approved = true;
        boolean reviewed = false;
        LocalDateTime date = LocalDateTime.now();
        String desc = "description";
        Contract contract = new ConcreteContractBuilder()
                .withNetId("kverhoef")
                .withCourseId("CSE1300")
                .withMaxHours(8)
                .withDuties("DUTIES")
                .withSigned(true)
                .withRating(2.0)
                .build();

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withId(id)
                .withWorkedTime(workedTime)
                .withApproved(approved)
                .withReviewed(reviewed)
                .withDate(date)
                .withDescription(desc)
                .withContractId(contract)
                .build();

        //assert
        assertThat(hourDeclaration.getId()).isEqualTo(id);
        assertThat(hourDeclaration.getApproved()).isEqualTo(approved);
        assertThat(hourDeclaration.getContract()).isEqualTo(contract);
        assertThat(hourDeclaration.getReviewed()).isEqualTo(reviewed);
        assertThat(hourDeclaration.getDate()).isEqualTo(date);
        assertThat(hourDeclaration.getWorkedTime()).isEqualTo(workedTime);
        assertThat(hourDeclaration.getDescription()).isEqualTo(desc);
    }
}
