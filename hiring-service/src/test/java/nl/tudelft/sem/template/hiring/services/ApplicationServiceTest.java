package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositeKeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApplicationServiceTest {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationService applicationService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void checkAndSaveTest() {
        //Arrange
        String motivation = "I just want to be a cool!";
        Application validApplication = new Application("CSE1200", "johndoe", (float) 6.0,
                motivation, ApplicationStatus.PENDING);
        Application invalidApplication = new Application("CSE1200", "jsmith", (float) 5.9,
                motivation, ApplicationStatus.PENDING);
        assertThat(validApplication.meetsRequirements()).isTrue();
        assertThat(invalidApplication.meetsRequirements()).isFalse();

        //Act
        applicationService.checkAndSave(validApplication);
        applicationService.checkAndSave(invalidApplication);

        //Assert
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1200", "johndoe"))).isNotEmpty();
        assertThat(applicationRepository.findById(new ApplicationKey("CSE1200", "jsmith"))).isEmpty();

    }


}
