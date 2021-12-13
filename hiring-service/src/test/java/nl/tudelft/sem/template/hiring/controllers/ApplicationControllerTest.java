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
    public void getStatusByCourseTest() throws Exception {
        //Arrange

        // Your function /get status doesn't take a model so you don't need to use one.
        RetrieveStatusModel pendingModel = new RetrieveStatusModel(
                "CSE1200", "kverhoef", "I like TAs",
                9, ApplicationStatus.PENDING);
        RetrieveStatusModel acceptedModel = new RetrieveStatusModel(
                "CSE1200", "dsmith", "I like TAs",
                9, ApplicationStatus.ACCEPTED);
        RetrieveStatusModel rejectedModel = new RetrieveStatusModel(
                "CSE1200", "lbrown", "I like TAs",
                9, ApplicationStatus.REJECTED);

        ApplicationKey pendingKey = new ApplicationKey(pendingModel.getCourseId(), exampleNetId);
        ApplicationKey acceptedKey = new ApplicationKey(acceptedModel.getCourseId(), exampleNetId);
        ApplicationKey rejectedKey = new ApplicationKey(rejectedModel.getCourseId(), exampleNetId);

        //Act
        // This isn't used by anything.
        ResultActions validResults = mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(pendingModel))
                .header("Authorization", "Bearer Joe"));

        // below actions throw user defined error, does not exist.
        // reason for this is that the netId is fetched from the authmanager which returns johndoe, This person is not saved in the db.

        // what you want to be doing is the following
        when(mockAuthenticationManager.getNetid()).thenReturn("kverhoef");
        // you do this because the auth manager is what is used to get the net id.
        // and the default is not used in this test, hence we want to change it.
        // Note the netId is the same one as used in the setUp() function above as this is a pending apllication.

        ResultActions pendingResult  = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));
        ResultActions acceptedResult = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));
        ResultActions rejectedResult = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));

        //Assert
        pendingResult.andExpect(status().isOk());
        acceptedResult.andExpect(status().isOk());
        rejectedResult.andExpect(status().isOk());

        // these asserts are checking your hardcoded variables from above.
        // ApplicationKey pendingKey = new ApplicationKey(pendingModel.getCourseId(), exampleNetId);
        // I assume you are trying to chech the repsonse from the server, in which case you want to deserialize the result you obtain.
        // There are of examples in other integration tests that go into it.
        // But you will need to use Martin's json util
        assertThat(applicationRepository.findById(pendingKey).get().getStatus().equals(ApplicationStatus.PENDING));
        assertThat(applicationRepository.findById(acceptedKey).get().getStatus().equals(ApplicationStatus.ACCEPTED));
        assertThat(applicationRepository.findById(rejectedKey).get().getStatus().equals(ApplicationStatus.REJECTED));
    }

    @Test
    void exampleTest() throws Exception {
        // What you want to be doing is writing smaller tests and focus on those.

        // arrange
        when(mockAuthenticationManager.getNetid()).thenReturn("kverhoef");

        // act
        ResultActions pendingResult  = mockMvc.perform(get("/status/CSE1200").header("Authorization", "Bearer Joe"));

        // assert
        pendingResult.andExpect(status().isOk());
        // other asserts removed for brevity
        // But in reality you would want to also check the returned types.
    }
}
