package nl.tudelft.sem.template.ta.entities.builders;

import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import org.junit.jupiter.api.Test;

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
    }
}
