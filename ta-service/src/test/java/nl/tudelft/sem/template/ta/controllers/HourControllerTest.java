package nl.tudelft.sem.template.ta.controllers;

import static nl.tudelft.sem.template.ta.utils.JsonUtil.deserialize;
import static nl.tudelft.sem.template.ta.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.models.AcceptHoursRequestModel;
import nl.tudelft.sem.template.ta.models.ContractResponseModel;
import nl.tudelft.sem.template.ta.models.HourResponseModel;
import nl.tudelft.sem.template.ta.models.SubmitHoursRequestModel;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.repositories.HourDeclarationRepository;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.security.TokenVerifier;
import nl.tudelft.sem.template.ta.utils.JsonUtil;
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
import org.springframework.test.web.servlet.MvcResult;
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
    private List<HourDeclaration> hourDeclarations;
    private List<Contract> contracts;

    @BeforeEach
    void setUp() {
        contractRepository.deleteAll();
        hourDeclarationRepository.deleteAll();
        hourDeclarations = new ArrayList<>();
        contracts = new ArrayList<>();

        defaultContract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(20)
            .duties("Work really hard")
            .signed(false)
            .build();
        defaultContract = contractRepository.save(defaultContract);
        contracts.add(defaultContract);

        Contract secondContract = contractRepository.save(Contract.builder()
            .netId("WinstijnSmit")
            .courseId("CSE2310")
            .maxHours(40)
            .duties("Work really hard")
            .signed(true)
            .build()
        );
        contracts.add(contractRepository.save(secondContract));

        defaultHourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(0)
            .approved(false)
            .reviewed(false)
            .build();
        defaultHourDeclaration = hourDeclarationRepository.save(defaultHourDeclaration);
        hourDeclarations.add(defaultHourDeclaration);

        // Declarations for contract 1.
        hourDeclarations.add(hourDeclarationRepository.save(HourDeclaration.builder()
            .workedTime(2).contract(defaultContract).approved(false).reviewed(false).build()));
        hourDeclarations.add(hourDeclarationRepository.save(HourDeclaration.builder()
            .workedTime(2).contract(defaultContract).approved(true).reviewed(true).build()));

        // Declarations for contract 2.
        hourDeclarations.add(hourDeclarationRepository.save(HourDeclaration.builder()
            .workedTime(10).contract(secondContract).approved(false).reviewed(false).build()));
        hourDeclarations.add(hourDeclarationRepository.save(HourDeclaration.builder()
            .workedTime(5).contract(secondContract).approved(true).reviewed(true).build()));

        mockAuthentication(defaultContract.getNetId(), true);
    }

    // Mock authentication to show that we are signed in as a certain user.
    void mockAuthentication(String netId, boolean isResponsibleLecturer) {
        when(mockAuthenticationManager.getNetid()).thenReturn(netId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(netId);
        when(courseInformation.isResponsibleLecturer(anyString(), anyString()))
            .thenReturn(isResponsibleLecturer);
    }

    @Test
    void submitHoursWithExistingContract() throws Exception {
        // arrange
        SubmitHoursRequestModel model = SubmitHoursRequestModel.builder()
            .course("CSE2310")
            .desc("this is a test.")
            .workedTime(5)
            .build();

        HourDeclaration expected = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(5)
            .reviewed(false)
            .approved(false)
            .desc("this is a test.")
            .build();

        // act
        ResultActions results = mockMvc.perform(post("/hours/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));


        // assert
        var result = results.andExpect(status().isOk()).andReturn();

        UUID responseModel = deserialize(result.getResponse().getContentAsString(),
            UUID.class);

        var submitted = hourDeclarationRepository.findById(responseModel).orElseThrow();

        assertThat(submitted.getId()).isNotNull();
        submitted.setId(null);
        assertThat(submitted).isEqualTo(expected);
    }


    @Test
    void submitHoursWithNonExistingCourse() throws Exception {
        // arrange
        SubmitHoursRequestModel model = SubmitHoursRequestModel.builder()
            .course("CSE8764")
            .desc("this is a test.")
            .workedTime(5)
            .build();

        // act
        ResultActions results = mockMvc.perform(post("/hours/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isNotFound());

        assertThat(hourDeclarationRepository
                .findAll().size()).isGreaterThan(1);  // account for setup()
    }

    @Test
    void submitHoursWithNonExistingContract() throws Exception {
        // arrange
        when(mockAuthenticationManager.getNetid()).thenReturn("JohnDoe");
        SubmitHoursRequestModel model = SubmitHoursRequestModel.builder()
            .course("CSE2310")
            .desc("this is a test.")
            .workedTime(5)
            .build();

        // act
        ResultActions results = mockMvc.perform(post("/hours/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isNotFound());

        assertThat(hourDeclarationRepository
                    .findAll().size()).isGreaterThan(0);  // account for setup()
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

    @Test
    void getOpenHours() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/CSE2310/WinstijnSmit")
            .header("Authorization", "Bearer Pieter"));

        // Assert
        MvcResult results = action
            .andExpect(status().isOk())
            .andReturn();

        List<HourResponseModel> response = parseHourResponseResult(results);
        assertThat(response.size()).isEqualTo(1);
        assertThatResponseContains(response, hourDeclarations.get(3)).isTrue();
    }


    @Test
    void getOpenHours_myOwn() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", false);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/CSE2310/WinstijnSmit")
            .header("Authorization", "Bearer Pieter"));

        // Assert
        MvcResult results = action
            .andExpect(status().isOk())
            .andReturn();

        List<HourResponseModel> response = parseHourResponseResult(results);
        assertThat(response.size()).isEqualTo(1);
        assertThatResponseContains(response, hourDeclarations.get(3)).isTrue();
    }

    @Test
    void getOpenHours_unauthorized() throws Exception {
        mockAuthentication("Maurits", false);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/CSE2310/WinstijnSmit")
            .header("Authorization", "Bearer Lol"));

        // Assert
        action.andExpect(status().isUnauthorized());
    }


    @Test
    void getOpenHours_nonExistingNetId() throws Exception {
        mockAuthentication("Stefan", true);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/CSE2310/Max")
            .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult results = action
            .andExpect(status().isOk())
            .andReturn();

        List<HourResponseModel> response = parseHourResponseResult(results);
        assertThat(response.size()).isEqualTo(0);
    }

    @Test
    void getAllOpenHours() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/CSE2310")
            .header("Authorization", "Bearer Pieter"));

        // Assert
        MvcResult results = action
            .andExpect(status().isOk())
            .andReturn();

        List<HourResponseModel> response = parseHourResponseResult(results);
        assertThat(response.size()).isEqualTo(3);
        assertThatResponseContains(response, hourDeclarations.get(0)).isTrue();
        assertThatResponseContains(response, hourDeclarations.get(1)).isTrue();
        assertThatResponseContains(response, hourDeclarations.get(3)).isTrue();
    }


    @Test
    void getAllOpenHours_nonExitingCourse() throws Exception {
        mockAuthentication("Stefam", true);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/EE2122")
            .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult results = action
            .andExpect(status().isOk())
            .andReturn();

        List<HourResponseModel> response = parseHourResponseResult(results);
        assertThat(response.size()).isEqualTo(0);
    }

    @Test
    void getAllOpenHours_unauthorized() throws Exception {
        mockAuthentication("WinstijnSmit", false);

        // Act
        ResultActions action = mockMvc.perform(get("/hours/open/CSE2310")
            .header("Authorization", "Bearer Lol"));

        // Assert
        action.andExpect(status().isUnauthorized());
    }


    /**
     * Helper method that asserts whether the response contains the HourResponseModel.
     *
     * @param response list of HourResponseModel
     * @param hourDeclaration hourDeclaration
     * @return assert that the response contains
     *         the hour response model of the hour declaration.
     */
    private org.assertj.core.api.AbstractBooleanAssert<?>
        assertThatResponseContains(List<HourResponseModel> response,
                                   HourDeclaration hourDeclaration) {
        return assertThat(response.contains(
                        HourResponseModel.fromHourDeclaration(hourDeclaration)
                         ));
    }

    /**
     * Helper method to convert the MvcResult to a list of HourResponseModel.
     */
    private List<HourResponseModel> parseHourResponseResult(MvcResult result) throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        var list = new ArrayList<HourResponseModel>();
        List<Map<String, Object>> parsed = JsonUtil.deserialize(jsonString, list.getClass());

        // JsonUtil returns a map of items. Parse them and put them in our list.
        for (Map<String, Object> map : parsed) {
            list.add(new HourResponseModel(
                (Date) map.get("date"),
                (String) map.get("description"),
                (int) map.get("workedTime"),
                (boolean) map.get("approved"),
                (String) map.get("ta")
            ));
        }
        return list;
    }

}