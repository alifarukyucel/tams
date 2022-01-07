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

    /**
     * Boundary test for the compare to method in relation to the sufficient rating.
     */
    @Test
    public void compareToInsufficientBoundary() {
        Application application = new Application("CSE1300", "jsmith", 7.0f,
            "I want to be cool too!", ApplicationStatus.PENDING);
        var p1 = new PendingApplicationResponseModel(application, 5.75d);
        var p2 = new PendingApplicationResponseModel(application, 5.75d);
        var p3 = new PendingApplicationResponseModel(application, 5.74d);

        // on point
        assertThat(p1.compareTo(p2)).isEqualTo(0);
        assertThat(p2.compareTo(p1)).isEqualTo(0);

        // off point
        assertThat(p1.compareTo(p3)).isEqualTo(-1);
        assertThat(p3.compareTo(p1)).isEqualTo(1);
    }

    @Test
    public void compareToTest() {
        Application application = new Application("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        var p1 = new PendingApplicationResponseModel(application, 9.0d);
        var p2 = new PendingApplicationResponseModel(application, 5.75d);
        var p3 = new PendingApplicationResponseModel(application, 5.74d);
        var p4 = new PendingApplicationResponseModel(application, -1.0d);
        var p5 = new PendingApplicationResponseModel(application, 9.0d);

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
