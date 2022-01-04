package nl.tudelft.sem.template.hiring.models;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

public class PendingTeachingAssistantApplicationResponseModelTest {
    @Test
    public void pendingApplicationResponseModelConstructorTest() {
        //Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);

        //Act
        var model = new PendingApplicationResponseModel(teachingAssistantApplication, 8.0f);

        var expected = new PendingApplicationResponseModel("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", 8.0f);

        //Assert
        assertThat(model).isEqualTo(expected);
    }


}
