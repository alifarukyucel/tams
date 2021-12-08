package nl.tudelft.sem.template.hiring.entities;

import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTest {
    private static Application defaultPendingApplication;

    @BeforeEach
    public void setUp() {
        String motivation = "I just want to be a TA!";
        defaultPendingApplication = new Application("CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
    }

    @Test
    public void meetsRequirementsTest() {
        //Arrange
        Application lowGradeApplication = new Application("CSE1200", "johndoe", (float) 5.9,
                "Let me in!", ApplicationStatus.PENDING);

        //Act + assert
        assertThat(defaultPendingApplication.meetsRequirements()).isTrue();
        assertThat(lowGradeApplication.meetsRequirements()).isFalse();
    }

    @Test
    public void createPendingApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        //Act
        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);

        //Assert
        assertThat(defaultPendingApplication).isEqualTo(pendingApplication);
    }
}
