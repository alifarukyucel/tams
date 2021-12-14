package nl.tudelft.sem.template.hiring.controllers;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositeKeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
import nl.tudelft.sem.template.hiring.models.RetrieveStatusModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.security.AuthManager;
import nl.tudelft.sem.template.hiring.security.TokenVerifier;
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

import java.security.Key;
import java.util.Optional;

import static nl.tudelft.sem.template.hiring.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier"})
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

    private Application pendingApplication;
    private Application acceptedApplication;
    private Application rejectedApplication;

    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getNetid()).thenReturn(exampleNetId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(exampleNetId);
        applicationRepository.deleteAll();
        // Save applications in db (Not sure whether this is necessary)
        pendingApplication = Application.builder()
                .netId("kverhoef")
                .courseId("CSE1200")
                .grade(9)
                .motivation("I like TAs")
                .status(ApplicationStatus.PENDING)
                .build();
        applicationRepository.save(pendingApplication);

        acceptedApplication = Application.builder()
                .netId("dsmith")
                .courseId("CSE1200")
                .grade(9)
                .motivation("I like TAs")
                .status(ApplicationStatus.ACCEPTED)
                .build();
        applicationRepository.save(acceptedApplication);

        rejectedApplication = Application.builder()
                .netId("lbrown")
                .courseId("CSE1200")
                .grade(9)
                .motivation("I like TAs")
                .status(ApplicationStatus.REJECTED)
                .build();
        applicationRepository.save(rejectedApplication);
    }

    @Test
    public void applyTest() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("cse1200", (float) 6.0,
                "I want to");
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("cse1300", (float) 5.9,
                "I want to");

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), exampleNetId);
        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), exampleNetId);

        //Act
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(validModel))
                .header("Authorization", "Bearer Joe"));



        ResultActions invalidResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(invalidModel))
                .header("Authorization", "Bearer Joe"));

        //assert
        validResults.andExpect(status().isOk());
        invalidResults.andExpect(status().isBadRequest());
        assertThat(applicationRepository.findById(validKey)).isNotEmpty();
        assertThat(applicationRepository.findById(invalidKey)).isEmpty();

    }

    @Test
    void pendingStatusTest() throws Exception {
        // arrange
        when(mockAuthenticationManager.getNetid()).thenReturn("kverhoef");
        ApplicationKey key = new ApplicationKey(pendingApplication.getCourseId(), pendingApplication.getNetId());

        // act
        ResultActions pendingResult  = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));

        // assert
        pendingResult.andExpect(status().isOk());
        assertThat(pendingApplication.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void acceptedStatusTest() throws Exception {
        // arrange
        when(mockAuthenticationManager.getNetid()).thenReturn("kverhoef");
        ApplicationKey key = new ApplicationKey(acceptedApplication.getCourseId(), acceptedApplication.getNetId());

        // act
        ResultActions pendingResult  = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));

        // assert
        pendingResult.andExpect(status().isOk());
        assertThat(acceptedApplication.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    void rejectedStatusTest() throws Exception {
        // arrange
        when(mockAuthenticationManager.getNetid()).thenReturn("kverhoef");
        ApplicationKey key = new ApplicationKey(rejectedApplication.getCourseId(), rejectedApplication.getNetId());

        // act
        ResultActions pendingResult  = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));

        // assert
        pendingResult.andExpect(status().isOk());
        assertThat(rejectedApplication.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(applicationRepository.findById(key).get().getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }


}
