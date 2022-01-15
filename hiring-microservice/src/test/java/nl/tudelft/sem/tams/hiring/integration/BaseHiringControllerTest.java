package nl.tudelft.sem.tams.hiring.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import nl.tudelft.sem.tams.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.providers.TimeProvider;
import nl.tudelft.sem.tams.hiring.repositories.TeachingAssistantApplicationRepository;
import nl.tudelft.sem.tams.hiring.security.AuthManager;
import nl.tudelft.sem.tams.hiring.security.TokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier",
        "mockCourseInformation", "mockContractInformation", "mockTimeProvider"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public abstract class BaseHiringControllerTest {

    protected static final transient String exampleNetId = "johndoe";

    //This is the assumed current time for testing.
    //Because LocalDateTime.now() can't be used to test properly, we use this time as the current time
    protected static final transient LocalDateTime assumedCurrentTime = LocalDateTime.of(2022, 1, 1, 0, 0);

    @Autowired
    protected transient TeachingAssistantApplicationRepository taApplicationRepository;

    @Autowired
    protected transient MockMvc mockMvc;

    @Autowired
    protected transient CourseInformation mockCourseInformation;

    @Autowired
    protected transient ContractInformation mockContractInformation;

    @Autowired
    protected transient TimeProvider timeProvider;

    @Autowired
    protected transient AuthManager mockAuthenticationManager;

    @Autowired
    protected transient TokenVerifier mockTokenVerifier;

    /**
     * Setup mocking before tests run.
     */
    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getNetid()).thenReturn(exampleNetId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(exampleNetId);
        when(timeProvider.getCurrentLocalDateTime()).thenReturn(assumedCurrentTime);
    }
}
