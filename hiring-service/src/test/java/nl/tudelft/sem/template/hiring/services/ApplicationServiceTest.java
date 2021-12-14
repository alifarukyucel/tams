package nl.tudelft.sem.template.hiring.services;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApplicationServiceTest {
    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient ApplicationService applicationService;

    @Test
    public void validCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application validApplication = new Application("CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
        assertThat(validApplication.meetsRequirements()).isTrue();

        //Act
        applicationService.checkAndSave(validApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1200", "johndoe")))
                .isNotEmpty();
    }

    @Test
    public void invalidCheckAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application invalidApplication = new Application("CSE1300", "jsmith", (float) 5.9,
                motivation, ApplicationStatus.PENDING);
        assertThat(invalidApplication.meetsRequirements()).isFalse();

        //Act
        applicationService.checkAndSave(invalidApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
    }

    @Test
    public void getApplicationsAndMaxApplicationsTest() {
        //Arrange
        String motivation = "I am motivated";
        Application firstApplication = new Application("CSE1200", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);
        Application secondApplication = new Application("CSE1300", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);
        Application thirdApplication = new Application("CSE1400", "johndoe", 7.0f,
                motivation, ApplicationStatus.PENDING);

        //Act
        applicationRepository.save(firstApplication);
        applicationRepository.save(secondApplication);
        applicationRepository.save(thirdApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1300", "jsmith")))
                .isEmpty();
        assertThat(applicationService.getApplicationFromStudent("johndoe")).size().isEqualTo(3);
        assertThat(applicationService.maxApplication("johndoe")).isTrue();
    }

}
