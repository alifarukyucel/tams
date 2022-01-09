package nl.tudelft.sem.tams.ta.entities.builders.directors;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.HourDeclaration;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteContractBuilder;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteHourDeclarationBuilder;
import org.junit.jupiter.api.Test;

public class HourDeclarationDirectorTest {
    //Arbitrary time to use for the hour declarations
    private static final LocalDateTime arbitraryTime = LocalDateTime.of(2000, 1, 1, 0, 0);

    private HourDeclaration hourDeclaration;

    @Test
    public void testDirectorWithAll() {
        //arrange
        UUID id = UUID.randomUUID();
        Integer workedTime = 60;
        String desc = "description";
        Contract contract = new ConcreteContractBuilder()
                .withNetId("kverhoef")
                .withCourseId("CSE1300")
                .withMaxHours(8)
                .withDuties("DUTIES")
                .withSigned(true)
                .withRating(2.0)
                .build();

        var builder = new ConcreteHourDeclarationBuilder();
        new HourDeclarationDirector().createUnsignedContract(builder);


        //act
        hourDeclaration = builder
                .withId(id)
                .withWorkedTime(workedTime)
                .withDate(arbitraryTime)
                .withDescription(desc)
                .withContractId(contract)
                .build();

        //arrange
        assertThat(hourDeclaration.getApproved()).isFalse();
        assertThat(hourDeclaration.getReviewed()).isFalse();
    }

    @Test
    public void testDirectorWithNone() {

        //arrange
        var builder = new ConcreteHourDeclarationBuilder();
        new HourDeclarationDirector().createUnsignedContract(builder);

        //act
        hourDeclaration = builder
                .build();

        //arrange
        assertThat(hourDeclaration.getApproved()).isFalse();
        assertThat(hourDeclaration.getReviewed()).isFalse();
        assertThat(hourDeclaration.getId()).isNull();
    }
}
