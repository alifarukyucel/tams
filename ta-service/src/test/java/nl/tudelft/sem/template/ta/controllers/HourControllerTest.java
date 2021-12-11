package nl.tudelft.sem.template.ta.controllers;

import static nl.tudelft.sem.template.ta.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.models.AcceptHoursRequestModel;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.repositories.HourDeclarationRepository;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.security.TokenVerifier;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier", "mockCourseInformation"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
class HourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenVerifier mockTokenVerifier;

    @Autowired
    private AuthManager mockAuthenticationManager;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private HourDeclarationRepository hourDeclarationRepository;

    @Autowired
    private CourseInformation courseInformation;

    private Contract defaultContract;
    private HourDeclaration defaultHourDeclaration;

    @BeforeEach
    void setUp() {
        contractRepository.deleteAll();
        hourDeclarationRepository.deleteAll();

        defaultContract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        defaultContract = contractRepository.save(defaultContract);
        defaultHourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(0)
            .approved(false)
            .reviewed(false)
            .build();
        defaultHourDeclaration = hourDeclarationRepository.save(defaultHourDeclaration);

        when(mockAuthenticationManager.getNetid()).thenReturn(defaultContract.getNetId());
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(defaultContract.getNetId());
        when(courseInformation.isResponsibleLecturer(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void approveExistingHours() throws Exception {
        // arrange
        AcceptHoursRequestModel model = AcceptHoursRequestModel.builder()
            .accept(true)
            .id(defaultHourDeclaration.getId())
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/hours/approve")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isOk());
        HourDeclaration hour = hourDeclarationRepository.getOne(defaultHourDeclaration.getId());
        assertThat(hour.getApproved()).isTrue();
    }

    @Test
    void reApproveApprovedExistingHours() throws Exception {
        // arrange
        defaultHourDeclaration.setApproved(true);
        defaultHourDeclaration.setReviewed(true);
        defaultHourDeclaration = hourDeclarationRepository.save(defaultHourDeclaration);
        AcceptHoursRequestModel model = AcceptHoursRequestModel.builder()
            .accept(false)
            .id(defaultHourDeclaration.getId())
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/hours/approve")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isConflict());
        HourDeclaration hour = hourDeclarationRepository.getOne(defaultHourDeclaration.getId());
        assertThat(hour.getApproved()).isTrue();
    }

    @Test
    void approveHoursYouAreNotResponsibleFor() throws Exception {
        when(courseInformation.isResponsibleLecturer(anyString(), anyString())).thenReturn(false);
        // arrange
        AcceptHoursRequestModel model = AcceptHoursRequestModel.builder()
            .accept(true)
            .id(defaultHourDeclaration.getId())
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/hours/approve")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isUnauthorized());
        HourDeclaration hour = hourDeclarationRepository.getOne(defaultHourDeclaration.getId());
        assertThat(hour.getApproved()).isFalse();
    }

    @Test
    void approveNonExistingHours() throws Exception {
        // arrange
        AcceptHoursRequestModel model = AcceptHoursRequestModel.builder()
            .accept(true)
            .id(UUID.randomUUID())
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/hours/approve")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isNotFound());
    }
}