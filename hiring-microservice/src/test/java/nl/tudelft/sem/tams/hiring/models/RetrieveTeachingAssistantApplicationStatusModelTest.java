package nl.tudelft.sem.tams.hiring.models;

import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

import static nl.tudelft.sem.tams.hiring.models.RetrieveTeachingAssistantApplicationStatusModel.fromApplication;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveTeachingAssistantApplicationStatusModelTest {
    @Test
    public void fromApplicationTest() {
        //Arrange
        String courseId = "CSE1200";
        String netId = "johndoe";
        String motivation = "I want to be as cool as George!";
        float grade = 6.0f;
        ApplicationStatus status = ApplicationStatus.PENDING;

        var application = new TeachingAssistantApplication(courseId, netId, grade, motivation, status, null);

        var expected = new RetrieveTeachingAssistantApplicationStatusModel(courseId, netId, motivation, grade, status);

        //Act
        var actual = fromApplication(application);

        //Assert
        assertThat(actual).isEqualTo(expected);
    }
}
