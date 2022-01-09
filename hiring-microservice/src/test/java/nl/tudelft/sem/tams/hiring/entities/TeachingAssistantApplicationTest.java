package nl.tudelft.sem.tams.hiring.entities;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
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
                motivation, ApplicationStatus.PENDING, "sus@amog.us");
    }

    @Test
    public void createPendingApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        //Act
        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);

        //Assert
        assertThat(defaultPendingTaApplication).isEqualTo(pendingTaApplication);
    }

    /**
     * Boundary test off point.
     */
    @Test
    public void doesNotMeetRequirementsApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);
        pendingTaApplication.setGrade(Math.nextDown(6.0f));

        //Act
        boolean meetsRequirements = pendingTaApplication.meetsRequirements();

        //Assert
        assertThat(meetsRequirements).isFalse();
    }

    /**
     * Boundary test on point.
     */
    @Test
    public void meetsRequirementsApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);
        pendingTaApplication.setGrade(6.0f);

        //Act
        boolean meetsRequirements = pendingTaApplication.meetsRequirements();

        //Assert
        assertThat(meetsRequirements).isTrue();
    }

    /**
     * Boundary test off point.
     */
    @Test
    public void gradeBelowOneApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);
        pendingTaApplication.setGrade(Math.nextDown(1.0f));

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isFalse();
    }

    /**
     * Boundary test on point.
     */
    @Test
    public void gradeOneApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);
        pendingTaApplication.setGrade(1.0f);

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isTrue();
    }

    /**
     * Boundary test off point.
     */
    @Test
    public void gradeAboveTenApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);
        pendingTaApplication.setGrade(Math.nextUp(10.0f));

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isFalse();
    }

    /**
     * Boundary test on point.
     */
    @Test
    public void gradeTenApplicationTest() {
        //Arrange
        String courseId = defaultPendingTaApplication.getCourseId();
        String netId = defaultPendingTaApplication.getNetId();
        float grade = defaultPendingTaApplication.getGrade();
        String motivation = defaultPendingTaApplication.getMotivation();
        String contactEmail = defaultPendingTaApplication.getContactEmail();

        TeachingAssistantApplication pendingTaApplication = TeachingAssistantApplication.createPendingApplication(
                courseId, netId, grade, motivation, contactEmail);
        pendingTaApplication.setGrade(10.0f);

        //Act
        boolean validGrade = pendingTaApplication.hasValidGrade();

        //Assert
        assertThat(validGrade).isTrue();
    }
}
