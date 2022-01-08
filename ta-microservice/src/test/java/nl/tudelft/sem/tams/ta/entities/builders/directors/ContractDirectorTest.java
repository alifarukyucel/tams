package nl.tudelft.sem.tams.ta.entities.builders.directors;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteContractBuilder;
import org.junit.jupiter.api.Test;

public class ContractDirectorTest {

    private Contract contract;

    @Test
    public void testDirector() {
        //arrange
        String netId = "kverhoef";
        String courseId = "CSE1300";
        int maxHours = 8;
        String duties = "DUTIES";

        var builder = new ConcreteContractBuilder();
        new ContractDirector().createUnsignedContract(builder);

        //act
        contract = builder
                .withNetId(netId)
                .withCourseId(courseId)
                .withMaxHours(maxHours)
                .withDuties(duties)
                .build();

        //arrange
        assertThat(contract.getSigned()).isFalse();
        assertThat(contract.getRating()).isZero();
    }

    @Test
    public void testDirectorWithNone() {

        //arrange
        var builder = new ConcreteContractBuilder();
        new ContractDirector().createUnsignedContract(builder);

        //act
        contract = builder
                .build();

        //arrange
        assertThat(contract.getSigned()).isFalse();
        assertThat(contract.getRating()).isZero();
        assertThat(contract.getCourseId()).isNull();
    }
}
