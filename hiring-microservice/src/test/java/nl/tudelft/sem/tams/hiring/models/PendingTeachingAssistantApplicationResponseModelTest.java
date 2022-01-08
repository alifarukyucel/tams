package nl.tudelft.sem.tams.hiring.models;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

public class PendingTeachingAssistantApplicationResponseModelTest {
    @Test
    public void pendingApplicationResponseModelConstructorTest() {
        //Arrange
        TeachingAssistantApplication teachingAssistantApplication = new TeachingAssistantApplication(
                "CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);

        //Act
        var model = new PendingTeachingAssistantApplicationResponseModel(teachingAssistantApplication, 8.0d);

        var expected = new PendingTeachingAssistantApplicationResponseModel("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", 8.0d);

        //Assert
        assertThat(model).isEqualTo(expected);
    }

    /**
     * Boundary test for the compare to method in relation to the sufficient rating.
     */
    @Test
    public void compareToInsufficientBoundary() {
        TeachingAssistantApplication application = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
            "I want to be cool too!", ApplicationStatus.PENDING);
        var p1 = new PendingTeachingAssistantApplicationResponseModel(application, 5.75d);
        var p2 = new PendingTeachingAssistantApplicationResponseModel(application, 5.75d);
        var p3 = new PendingTeachingAssistantApplicationResponseModel(application, 5.74d);

        // on point
        assertThat(p1.compareTo(p2)).isEqualTo(0);
        assertThat(p2.compareTo(p1)).isEqualTo(0);

        // off point
        assertThat(p1.compareTo(p3)).isEqualTo(-1);
        assertThat(p3.compareTo(p1)).isEqualTo(1);
    }

    @Test
    public void compareToTest() {
        TeachingAssistantApplication application = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        var p1 = new PendingTeachingAssistantApplicationResponseModel(application, 9.0d);
        var p2 = new PendingTeachingAssistantApplicationResponseModel(application, 5.75d);
        var p3 = new PendingTeachingAssistantApplicationResponseModel(application, 5.74d);
        var p4 = new PendingTeachingAssistantApplicationResponseModel(application, -1.0d);
        var p5 = new PendingTeachingAssistantApplicationResponseModel(application, 9.0d);

        //Compare 2 sufficient ratings
        assertThat(p1.compareTo(p2)).isEqualTo(-1);
        assertThat(p2.compareTo(p1)).isEqualTo(1);

        //Compare sufficient to insufficient and non-existing
        assertThat(p2.compareTo(p3)).isEqualTo(-1);
        assertThat(p2.compareTo(p4)).isEqualTo(-1);

        //Compare insufficient to non-existing
        assertThat(p3.compareTo(p4)).isEqualTo(1);

        //Compare 2 equal ratings
        assertThat(p1.compareTo(p5)).isEqualTo(0);
    }

}
