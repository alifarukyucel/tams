package nl.tudelft.sem.template.authentication.integration;

import nl.tudelft.sem.template.authentication.entities.AppUser;
import nl.tudelft.sem.template.authentication.models.LoginRequestModel;
import nl.tudelft.sem.template.authentication.models.LoginResponseModel;
import nl.tudelft.sem.template.authentication.models.RegistrationRequestModel;
import nl.tudelft.sem.template.authentication.repositories.UserRepository;
import nl.tudelft.sem.template.authentication.security.TokenGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static nl.tudelft.sem.template.authentication.integration.utils.JsonUtil.deserialize;
import static nl.tudelft.sem.template.authentication.integration.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockPasswordEncoder", "mockTokenGenerator", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UsersTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient PasswordEncoder mockPasswordEncoder;

    @Autowired
    private transient TokenGenerator mockTokenGenerator;

    @Autowired
    private transient AuthenticationManager mockAuthenticationManager;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";
        final String testPasswordHash = "hashedTestPassword";
        when(mockPasswordEncoder.encode(testPassword)).thenReturn(testPasswordHash);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setNetid(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findById(testUser).orElseThrow();

        assertThat(savedUser.getNetid()).isEqualTo(testUser);
        assertThat(savedUser.getPasswordHash()).isEqualTo(testPasswordHash);
    }

    @Test
    public void register_withExistingUser_throwsException() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String existingTestPassword = "password123";
        final String newTestPassword = "password456";

        AppUser existingAppUser = new AppUser();
        existingAppUser.setNetid(testUser);
        existingAppUser.setPasswordHash(existingTestPassword);
        userRepository.save(existingAppUser);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setNetid(testUser);
        model.setPassword(newTestPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findById(testUser).orElseThrow();

        assertThat(savedUser.getNetid()).isEqualTo(testUser);
        assertThat(savedUser.getPasswordHash()).isEqualTo(existingTestPassword);
    }

    @Test
    public void login_withValidUser_returnsToken() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";
        final String testPasswordHash = "hashedTestPassword";
        when(mockPasswordEncoder.encode(testPassword)).thenReturn(testPasswordHash);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                !testUser.equals(authentication.getPrincipal()) ||
                        !testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        final String testToken = "testJWTToken";
        when(mockTokenGenerator.generateJwtToken(argThat(userDetails -> userDetails.getUsername().equals(testUser))))
                .thenReturn(testToken);

        AppUser appUser = new AppUser();
        appUser.setNetid(testUser);
        appUser.setPasswordHash(testPasswordHash);
        userRepository.save(appUser);

        LoginRequestModel model = new LoginRequestModel();
        model.setNetid(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model)));


        // Assert
        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseModel responseModel = deserialize(result.getResponse().getContentAsString(),
                LoginResponseModel.class);

        assertThat(responseModel.getToken()).isEqualTo(testToken);

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal()) &&
                        testPassword.equals(authentication.getCredentials())));
    }

    @Test
    public void login_withNonexistentUsername_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal()) &&
                        testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        LoginRequestModel model = new LoginRequestModel();
        model.setNetid(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal()) &&
                        testPassword.equals(authentication.getCredentials())));

        verify(mockTokenGenerator, times(0)).generateJwtToken(any());
    }

    @Test
    public void login_withInvalidPassword_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String wrongPassword = "password1234";
        final String testPassword = "password123";
        final String testPasswordHash = "hashedTestPassword";
        when(mockPasswordEncoder.encode(testPassword)).thenReturn(testPasswordHash);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal()) &&
                        wrongPassword.equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        AppUser appUser = new AppUser();
        appUser.setNetid(testUser);
        appUser.setPasswordHash(testPasswordHash);
        userRepository.save(appUser);

        LoginRequestModel model = new LoginRequestModel();
        model.setNetid(testUser);
        model.setPassword(wrongPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal()) &&
                        wrongPassword.equals(authentication.getCredentials())));

        verify(mockTokenGenerator, times(0)).generateJwtToken(any());
    }
}
