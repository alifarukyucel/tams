package nl.tudelft.sem.tams.ta.entities.builders;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.HourDeclaration;
import org.junit.jupiter.api.Test;

public class HourDeclarationBuilderTest {
    //Arbitrary time to use for the hour declarations
    private static final LocalDateTime arbitraryTime = LocalDateTime.of(2000, 1, 1, 0, 0);

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
        Integer workedTime = 60;

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withWorkedTime(workedTime)
                .build();

        //assert
        assertThat(hourDeclaration.getWorkedTime()).isEqualTo(workedTime);
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();
        assertThat(hourDeclaration.getId()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
    }

    @Test
    public void testWithApproved() {
        //arrange
        boolean approved = true;

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withApproved(approved)
                .build();

        //assert
        assertThat(hourDeclaration.getApproved()).isEqualTo(approved);
        assertThat(hourDeclaration.getId()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
    }

    @Test
    public void testWithReviewed() {
        //arrange
        boolean reviewed = false;

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withReviewed(reviewed)
                .build();

        //assert
        assertThat(hourDeclaration.getReviewed()).isEqualTo(reviewed);
        assertThat(hourDeclaration.getId()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
    }

    @Test
    public void testWithDate() {
        //arrange
        LocalDateTime date = arbitraryTime;

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withDate(date)
                .build();

        //assert
        assertThat(hourDeclaration.getDate()).isEqualTo(date);
        assertThat(hourDeclaration.getId()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
    }

    @Test
    public void testWithDescription() {
        //arrange
        String desc = "description";

        //act
        hourDeclaration = new ConcreteHourDeclarationBuilder()
                .withDescription(desc)
                .build();

        //assert
        assertThat(hourDeclaration.getDescription()).isEqualTo(desc);
        assertThat(hourDeclaration.getId()).isNull();
        assertThat(hourDeclaration.getContract()).isNull();
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();
    }

    @Test
    public void testWithContract() {
        //arrange
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
                .withContractId(contract)
                .build();

        //assert
        assertThat(hourDeclaration.getContract()).isEqualTo(contract);
        assertThat(hourDeclaration.getId()).isNull();
        assertThat(hourDeclaration.getDescription()).isNull();
        assertThat(hourDeclaration.getApproved()).isNull();
        assertThat(hourDeclaration.getReviewed()).isNull();
        assertThat(hourDeclaration.getWorkedTime()).isNull();
        assertThat(hourDeclaration.getDate()).isNull();

    }

    @Test
    public void testAll() {
        //arrange
        UUID id = UUID.randomUUID();
        Integer workedTime = 60;
        boolean approved = true;
        boolean reviewed = false;
        LocalDateTime date = arbitraryTime;
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
