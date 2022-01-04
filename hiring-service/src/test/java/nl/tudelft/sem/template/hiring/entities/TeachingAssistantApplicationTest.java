package nl.tudelft.sem.template.hiring.entities;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TeachingAssistantApplicationTest {
    private static TeachingAssistantApplication defaultPendingTaApplication;

    /**
     * Create default objects used for testing.
     */
    @BeforeEach
    public void setUp() {
        String motivation = "I just want to be a TA!";
        defaultPendingTaApplication = new TeachingAssistantApplication("CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
    }

    @Test
    public void createPendingApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        //Act
        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);

        //Assert
        assertThat(defaultPendingTaApplication).isEqualTo(pendingTaApplication);
    }

    @Test
    public void doesNotMeetRequirementsApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);
        pendingTaApplication.setGrade(5.9f);

        //Act
        boolean meetsRequirements = pendingTaApplication.meetsRequirements();

        //Assert
        assertThat(meetsRequirements).isFalse();
    }

    @Test
    public void meetsRequirementsApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);
        pendingTaApplication.setGrade(6.0f);

        //Act
        boolean meetsRequirements = pendingTaApplication.meetsRequirements();

        //Assert
        assertThat(meetsRequirements).isTrue();
    }

    @Test
    public void gradeBelowOneApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);
        pendingTaApplication.setGrade(0.9f);

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isFalse();
    }

    @Test
    public void gradeOneApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);
        pendingTaApplication.setGrade(1.0f);

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isTrue();
    }

    @Test
    public void gradeAboveTenApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);
        pendingTaApplication.setGrade(10.1f);

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isFalse();
    }

    @Test
    public void gradeTenApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation);
        pendingTaApplication.setGrade(10.0f);

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isTrue();
    }
}
