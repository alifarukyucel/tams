package nl.tudelft.sem.tams.ta.entities.builders;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.tams.ta.entities.Contract;
import org.junit.jupiter.api.Test;

public class ContractBuilderTest {

    private Contract contract;

    @Test
    public void testWithNetId() {
        //arrange
        String netId = "kverhoef";

        //act
        contract = new ConcreteContractBuilder()
                .withNetId(netId)
                .build();

        //assert
        assertThat(contract.getNetId()).isEqualTo(netId);
        assertThat(contract.getCourseId()).isNull();
        assertThat(contract.getMaxHours()).isNull();
        assertThat(contract.getDuties()).isNull();
        assertThat(contract.getSigned()).isNull();
        assertThat(contract.getRating()).isZero();
    }

    @Test
    public void testWithCourseId() {
        //arrange
        String courseId = "CSE1300";

        //act
        contract = new ConcreteContractBuilder()
                .withCourseId(courseId)
                .build();

        //assert
        assertThat(contract.getCourseId()).isEqualTo(courseId);
        assertThat(contract.getNetId()).isNull();
        assertThat(contract.getMaxHours()).isNull();
        assertThat(contract.getDuties()).isNull();
        assertThat(contract.getSigned()).isNull();
        assertThat(contract.getRating()).isZero();
    }

    @Test
    public void testWithMaxHours() {
        //arrange
        int maxHours = 8;

        //act
        contract = new ConcreteContractBuilder()
                .withMaxHours(maxHours)
                .build();

        //assert
        assertThat(contract.getMaxHours()).isEqualTo(maxHours);
        assertThat(contract.getNetId()).isNull();
        assertThat(contract.getCourseId()).isNull();
        assertThat(contract.getDuties()).isNull();
        assertThat(contract.getSigned()).isNull();
        assertThat(contract.getRating()).isZero();
    }

    @Test
    public void testWithDuties() {
        //arrange
        String duties = "DUTIES";

        //act
        contract = new ConcreteContractBuilder()
                .withDuties(duties)
                .build();

        //assert
        assertThat(contract.getDuties()).isEqualTo(duties);
        assertThat(contract.getNetId()).isNull();
        assertThat(contract.getCourseId()).isNull();
        assertThat(contract.getMaxHours()).isNull();
        assertThat(contract.getSigned()).isNull();
        assertThat(contract.getRating()).isZero();
    }

    @Test
    public void testWithSigned() {
        //arrange
        boolean signed = true;

        //act
        contract = new ConcreteContractBuilder()
                .withSigned(signed)
                .build();

        //assert
        assertThat(contract.getSigned()).isEqualTo(signed);
        assertThat(contract.getNetId()).isNull();
        assertThat(contract.getCourseId()).isNull();
        assertThat(contract.getDuties()).isNull();
        assertThat(contract.getMaxHours()).isNull();
        assertThat(contract.getRating()).isZero();
    }

    @Test
    public void testWithRating() {
        //arrange
        double rating = 2.0;

        //act
        contract = new ConcreteContractBuilder()
                .withRating(rating)
                .build();

        //assert
        assertThat(contract.getRating()).isEqualTo(rating);
        assertThat(contract.getNetId()).isNull();
        assertThat(contract.getCourseId()).isNull();
        assertThat(contract.getDuties()).isNull();
        assertThat(contract.getSigned()).isNull();
        assertThat(contract.getMaxHours()).isNull();
    }

    @Test
    public void testAll() {
        //arrange
        String netId = "kverhoef";
        String courseId = "CSE1300";
        int maxHours = 8;
        String duties = "DUTIES";
        boolean signed = true;
        double rating = 2.0;

        //act
        contract = new ConcreteContractBuilder()
                .withNetId(netId)
                .withCourseId(courseId)
                .withMaxHours(maxHours)
                .withDuties(duties)
                .withSigned(signed)
                .withRating(rating)
                .build();

        //assert
        assertThat(contract.getRating()).isEqualTo(rating);
    }



}
