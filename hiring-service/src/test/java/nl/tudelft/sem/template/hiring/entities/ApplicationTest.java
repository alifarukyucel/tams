package nl.tudelft.sem.template.hiring.entities;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApplicationTest {
    private static Application defaultPendingApplication;

    /**
     * Create default objects used for testing.
     */
    @BeforeEach
    public void setUp() {
        String motivation = "I just want to be a TA!";
        defaultPendingApplication = new Application("CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
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

    /**
     * Boundary test off point.
     */
    @Test
    public void doesNotMeetRequirementsApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);
        pendingApplication.setGrade(Math.nextDown(6.0f));

        //Act
        boolean meetsRequirements = pendingApplication.meetsRequirements();

        //Assert
        assertThat(meetsRequirements).isFalse();
    }

    /**
     * Boundary test on point.
     */
    @Test
    public void meetsRequirementsApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);
        pendingApplication.setGrade(6.0f);

        //Act
        boolean meetsRequirements = pendingApplication.meetsRequirements();

        //Assert
        assertThat(meetsRequirements).isTrue();
    }

    /**
     * Boundary test off point.
     */
    @Test
    public void gradeBelowOneApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);
        pendingApplication.setGrade(Math.nextDown(1.0f));

        //Act
        boolean validGrade = pendingApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isFalse();
    }

    /**
     * Boundary test on point.
     */
    @Test
    public void gradeOneApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);
        pendingApplication.setGrade(1.0f);

        //Act
        boolean validGrade = pendingApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isTrue();
    }

    /**
     * Boundary test off point.
     */
    @Test
    public void gradeAboveTenApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);
        pendingApplication.setGrade(Math.nextUp(10.0f));

        //Act
        boolean validGrade = pendingApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isFalse();
    }

    /**
     * Boundary test on point.
     */
    @Test
    public void gradeTenApplicationTest() {
        //Arrange
        String courseId = defaultPendingApplication.getCourseId();
        String netId = defaultPendingApplication.getNetId();
        float grade = defaultPendingApplication.getGrade();
        String motivation = defaultPendingApplication.getMotivation();

        Application pendingApplication = Application.createPendingApplication(courseId, netId,
                grade, motivation);
        pendingApplication.setGrade(10.0f);

        //Act
        boolean validGrade = pendingApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isTrue();
    }
}
