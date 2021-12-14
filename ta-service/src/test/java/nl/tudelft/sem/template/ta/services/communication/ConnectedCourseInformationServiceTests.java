package nl.tudelft.sem.template.ta.services.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test", "mockMicroserviceCommunicationHelper"})
@TestPropertySource(properties = {"microservice.course.base_url="+ConnectedCourseInformationServiceTests.testUrl})
public class ConnectedCourseInformationServiceTests {
    static final String testUrl = "testUrl";

    @Autowired
    private transient ConnectedCourseInformationService connectedCourseInformationService;

    @Autowired
    private transient MicroserviceCommunicationHelper mockMicroserviceCommunicationHelper;

    @BeforeEach
    public void resetMock(){
        reset(mockMicroserviceCommunicationHelper);
    }



}
