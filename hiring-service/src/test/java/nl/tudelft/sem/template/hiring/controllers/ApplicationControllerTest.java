package nl.tudelft.sem.template.hiring.controllers;

import nl.tudelft.sem.template.hiring.entities.compositeKeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.models.ApplicationRequestModel;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static nl.tudelft.sem.template.hiring.utils.JsonUtil.serialize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier"})
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthManager mockAuthenticationManager;

    @Autowired
    private TokenVerifier mockTokenVerifier;

    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getNetid()).thenReturn("johndoe");
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn("johndoe");
    }

    @Test
    public void applyTest() throws Exception {
        //Arrange
        ApplicationRequestModel validModel = new ApplicationRequestModel("cse1200", (float) 6.0,
                "I want to");
        ApplicationRequestModel invalidModel = new ApplicationRequestModel("cse1300", (float) 5.9,
                "I want to");

        ApplicationKey validKey = new ApplicationKey(validModel.getCourseId(), "johndoe");
        ApplicationKey invalidKey = new ApplicationKey(invalidModel.getCourseId(), "johndoe");

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

}
