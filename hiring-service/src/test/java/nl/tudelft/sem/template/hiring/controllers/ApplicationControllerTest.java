package nl.tudelft.sem.template.hiring.controllers;

import static nl.tudelft.sem.template.hiring.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.security.TokenVerifier;
import nl.tudelft.sem.template.hiring.services.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier", "mockCourseInformation"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ApplicationControllerTest {
    private static String exampleNetId = "johndoe";

    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient TokenVerifier mockTokenVerifier;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    /**
     * Setup mocking before tests run.
     */
    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getNetid()).thenReturn(exampleNetId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(exampleNetId);
    }

    @Test
    public void validApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("cse1200", (float) 6.0,
                "I want to");

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), exampleNetId);

        //Act
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(validModel))
                .header("Authorization", "Bearer Joe"));
        //assert
        validResults.andExpect(status().isOk());
        assertThat(applicationRepository.findById(validKey)).isNotEmpty();

    }

    @Test
    public void invalidApplicationTest() throws Exception {
        //Arrange
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("cse1300", (float) 5.9,
                "I want to");

        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

        //Act
        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        invalidResults.andExpect(status().isBadRequest());
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();
    }


    @Test
    void withdrawOnTime() throws Exception {
        // arrange
        Application onTime = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(onTime);

        ApplicationKey key = ApplicationKey.builder()
                .courseId(onTime.getCourseId())
                .netId(onTime.getNetId())
                .build();

        when(mockCourseInformation.startDate(onTime.getCourseId()))
                .thenReturn(LocalDate.MAX);

        // act
        ResultActions onTimeResult  = mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(key))
                .header("Authorization", "Bearer Joe"));

        // assert
        assertThat(applicationRepository.findById(key)).isEmpty();
        onTimeResult.andExpect(status().isOk());
    }

    @Test
    void withdrawTooLate() throws Exception {
        // arrange
        Application tooLate = new Application("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        applicationRepository.save(tooLate);

        ApplicationKey key = ApplicationKey.builder()
                .courseId(tooLate.getCourseId())
                .netId(tooLate.getNetId())
                .build();

        when(mockCourseInformation.startDate(tooLate.getCourseId()))
                .thenReturn(LocalDate.now());

        // act
        ResultActions onTimeResult  = mockMvc.perform(delete("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(key))
                .header("Authorization", "Bearer Joe"));

        // assert
        assertThat(applicationRepository.findById(key)).isNotEmpty();
        onTimeResult.andExpect(status().isForbidden());

    }

}
