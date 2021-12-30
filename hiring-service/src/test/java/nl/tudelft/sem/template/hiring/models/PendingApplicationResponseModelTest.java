package nl.tudelft.sem.template.hiring.models;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

public class PendingApplicationResponseModelTest {
    @Test
    public void pendingApplicationResponseModelConstructorTest() {
        //Arrange
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);

        //Act
        var model = new PendingApplicationResponseModel(application, 8.0d);

        var expected = new PendingApplicationResponseModel("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", 8.0d);

        //Assert
        assertThat(model).isEqualTo(expected);
    }


}
