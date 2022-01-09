package nl.tudelft.sem.tams.ta.integration;

import static nl.tudelft.sem.tams.ta.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteContractBuilder;
import nl.tudelft.sem.tams.ta.entities.compositekeys.ContractId;
import nl.tudelft.sem.tams.ta.interfaces.CourseInformation;
import nl.tudelft.sem.tams.ta.interfaces.EmailSender;
import nl.tudelft.sem.tams.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.tams.ta.models.ContractResponseModel;
import nl.tudelft.sem.tams.ta.models.CreateContractRequestModel;
import nl.tudelft.sem.tams.ta.models.RateContractRequestModel;
import nl.tudelft.sem.tams.ta.repositories.ContractRepository;
import nl.tudelft.sem.tams.ta.security.AuthManager;
import nl.tudelft.sem.tams.ta.security.TokenVerifier;
import nl.tudelft.sem.tams.ta.services.ContractService;
import nl.tudelft.sem.tams.ta.services.communication.models.CourseInformationResponseModel;
import nl.tudelft.sem.tams.ta.utils.JsonUtil;
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
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier", "mockCourseInformation", "mockEmailSender"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractService contractService;

    @Autowired
    private TokenVerifier mockTokenVerifier;

    @Autowired
    private AuthManager mockAuthenticationManager;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private CourseInformation mockCourseInformation;

    @Autowired
    private EmailSender mockEmailSender;

    private Contract defaultContract;
    private List<Contract> contracts;

    @BeforeEach
    void setUp() {
        contracts = new ArrayList<Contract>();

        // Save basic contract in db.
        defaultContract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
            .withRating(5)
            .build();
        contracts.add(defaultContract);

        contracts.add(new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withMaxHours(10)
            .withDuties("Work really hard")
            .withRating(8)
            .withSigned(true)
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE1250")
            .withMaxHours(2)
            .withDuties("No need to work hard")
            .withRating(8.6)
            .withSigned(false)
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2300")
            .withMaxHours(2)
            .withDuties("Work really hard")
            .withRating(9.4)
            .withSigned(true)
            .build()
        );

        contractRepository.saveAll(contracts);
        mockAuthentication(defaultContract.getNetId());
    }

    // Mock authentication to show that we are signed in as a certain user.
    void mockAuthentication(String netId, boolean isResponsibleLecturer) {
        when(mockAuthenticationManager.getNetid()).thenReturn(netId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(netId);
        when(mockCourseInformation.isResponsibleLecturer(anyString(), anyString()))
            .thenReturn(isResponsibleLecturer);
    }

    void mockAuthentication(String netId) {
        mockAuthentication(netId, false);
    }

    @Test
    void signExistingContract() throws Exception {
        // arrange
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .course(defaultContract.getCourseId())
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService
            .getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isOk());
        assertThat(savedContract.getSigned()).isTrue();
    }

    @Test
    void reSignAlreadySignedExistingContract() throws Exception {
        // arrange
        defaultContract.setSigned(true);
        defaultContract = contractRepository.save(defaultContract);
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .course(defaultContract.getCourseId())
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService
            .getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isConflict());
        assertThat(savedContract.getSigned()).isTrue();
    }

    @Test
    void signNonExistingContract() throws Exception {
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .course("CSEISFAKE")
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService
            .getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isNotFound());
        assertThat(savedContract.getSigned()).isFalse();
    }

    @Test
    void signExistingContractByPassingNullValues() throws Exception {
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .course(null)
            .build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService
            .getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isNotFound());
        assertThat(savedContract.getSigned()).isFalse();
    }

    @Test
    void getSignedInContracts_one() throws Exception {
        // Arrange
        mockAuthentication("PVeldHuis");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/mine")
                .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThatResponseContains(responseContracts, contracts.get(0)).isTrue();
        assertThatResponseContains(responseContracts, contracts.get(1)).isFalse();
        assertThatResponseContains(responseContracts, contracts.get(2)).isFalse();
    }

    @Test
    void getContracts_multiple() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/mine")
                .header("Authorization", "Bearer Winstijn"));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThatResponseContains(responseContracts, contracts.get(0)).isFalse();
        assertThatResponseContains(responseContracts, contracts.get(1)).isTrue();
        assertThatResponseContains(responseContracts, contracts.get(2)).isTrue();
    }

    @Test
    void getSignedInContracts_filterCourse() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/CSE2310/mine")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThatResponseContains(responseContracts, contracts.get(0)).isFalse();
        assertThatResponseContains(responseContracts, contracts.get(1)).isTrue();
        assertThatResponseContains(responseContracts, contracts.get(2)).isFalse();
    }

    @Test
    void getSignedInContracts_noContractsFound() throws Exception {
        // Arrange
        mockAuthentication("nonexistent");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/mine")
                .header("Authorization", "Bearer Lol"));

        // Assert
        action.andExpect(status().isNotFound());
    }

    @Test
    void getContracts() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/CSE2310/PVeldHuis")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThatResponseContains(responseContracts, contracts.get(0)).isTrue();
        assertThat(responseContracts.size()).isEqualTo(1);
    }

    @Test
    void getContracts_notFound() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/CSE1000/WinstijnSmit")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isNotFound());
    }

    @Test
    void getContracts_ownContract() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", false);

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/CSE2310/WinstijnSmit")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThatResponseContains(responseContracts, contracts.get(1)).isTrue();
        assertThat(responseContracts.size()).isEqualTo(1);
    }

    @Test
    void getContracts_forbidden() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", false);

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/CSE2310/PVeldHuis")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isForbidden());
    }

    @Test
    void createContract() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        CreateContractRequestModel model = CreateContractRequestModel.builder()
            .courseId("CSE2310").netId("BillGates").maxHours(10).duties("My duties").build();
        int size = contractRepository.findAll().size();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(41)
                .build());

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        ContractResponseModel response =
            JsonUtil.deserialize(result.getResponse().getContentAsString(), ContractResponseModel.class);

        assertThat(response).isNotNull();
        assertThat(ContractResponseModel.fromContract(
                        contractRepository.getOne(new ContractId("BillGates", "CSE2310"))
                    ))
                    .isEqualTo(response); // verify that is saved is ours.
        assertThat(contractRepository.findAll().size()).isEqualTo(size + 1);

        verifyNoInteractions(mockEmailSender);
    }


    @Test
    void createContractExceedingLimit() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        CreateContractRequestModel model = CreateContractRequestModel.builder()
            .courseId("CSE2310").netId("BillGates").maxHours(10).duties("My duties").build();
        int size = contractRepository.findAll().size();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(39)
                .build());

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
            .andExpect(status().isBadRequest())
            .andReturn();

        assertThat(contractRepository.findAll().size()).isEqualTo(size);

        verifyNoInteractions(mockEmailSender);
    }

    @Test
    void createContractWithContactEmail() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        String testContactEmail = "winstijn@tudelft.nl";
        CreateContractRequestModel model = CreateContractRequestModel.builder()
                .courseId("CSE2310")
                .netId("BillGates")
                .maxHours(10)
                .duties("My duties")
                .taContactEmail(testContactEmail)
                .build();
        int size = contractRepository.findAll().size();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(41)
                .build());

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        ContractResponseModel response =
            JsonUtil.deserialize(result.getResponse().getContentAsString(), ContractResponseModel.class);

        assertThat(response).isNotNull();
        assertThat(ContractResponseModel.fromContract(
                        contractRepository.getOne(new ContractId("BillGates", "CSE2310"))
                    ))
                    .isEqualTo(response); // verify that is saved is ours.
        assertThat(contractRepository.findAll().size()).isEqualTo(size + 1);

        verify(mockEmailSender).sendEmail(testContactEmail,
                "You have been offered a TA position for CSE2310",
                "Hi BillGates,\n\n"
                        + "The course staff of CSE2310 is offering you a TA position. Congratulations!\n"
                        + "Your duties are \"My duties\", and the maximum number of hours is 10.\n"
                        + "Please log into TAMS to review and sign the contract.\n\n"
                        + "Best regards,\nThe programme administration of your faculty");
        verifyNoMoreInteractions(mockEmailSender);
    }

    @Test
    void createContractExceedingTaLimit() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        CreateContractRequestModel model = CreateContractRequestModel.builder()
            .courseId("CSE2310").netId("BillGates").maxHours(10).duties("My duties").build();
        int size = contractRepository.findAll().size();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(40)
                .build());

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
            .andExpect(status().isBadRequest())
            .andReturn();

        assertThat(contractRepository.findAll().size()).isEqualTo(size);

    }

    @Test
    void createContract_forbidden() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", false);
        CreateContractRequestModel model = CreateContractRequestModel.builder()
            .courseId("CSE2310").netId("WinstijnSmit").maxHours(10).duties("My duties").build();
        int size = contractRepository.findAll().size();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isForbidden());
        assertThat(contractRepository.findAll().size()).isEqualTo(size);
    }

    @Test
    void createContract_badRequest() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", true);
        CreateContractRequestModel model = CreateContractRequestModel.builder()
            .courseId("CSE2310").netId("SteveJobs").maxHours(-10).duties("My duties").build();
        int size = contractRepository.findAll().size();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(10000)
                .build());

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isBadRequest());
        assertThat(contractRepository.findAll().size()).isEqualTo(size);
    }

    @Test
    void createContract_alreadyExists() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", true);
        CreateContractRequestModel model = CreateContractRequestModel.builder()
            .courseId("CSE2310").netId("WinstijnSmit").maxHours(10).duties("My duties").build();
        int size = contractRepository.findAll().size();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(10000)
                .build());

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isBadRequest());
        assertThat(contractRepository.findAll().size()).isEqualTo(size);
    }

    @Test
    void rateContract() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        RateContractRequestModel model = RateContractRequestModel.builder()
            .courseId("CSE2310").netId("PVeldHuis").rating(9.66).build();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/rate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        assertThat(ContractResponseModel.fromContract(
            contractRepository.getOne(new ContractId("PVeldHuis", "CSE2310"))
        ).getRating()).isEqualTo(9.66);

    }

    @Test
    void rateContract_forbidden() throws Exception {
        // Arrange
        mockAuthentication("PVeldHuis", false);
        RateContractRequestModel model = RateContractRequestModel.builder()
            .courseId("CSE2310").netId("WinstijnSmit").rating(3).build();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/rate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isForbidden());
        assertThat(ContractResponseModel.fromContract(
            contractRepository.getOne(new ContractId("WinstijnSmit", "CSE2310"))
        ).getRating()).isEqualTo(8); // ensure that it did not change.
    }

    @Test
    void rateContract_forbidden_2() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit", false);
        RateContractRequestModel model = RateContractRequestModel.builder()
            .courseId("CSE2310").netId("WinstijnSmit").rating(10).build();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/rate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isForbidden());
        assertThat(ContractResponseModel.fromContract(
            contractRepository.getOne(new ContractId("WinstijnSmit", "CSE2310"))
        ).getRating()).isEqualTo(8); // ensure that it did not change.
    }


    @Test
    void rateContract_invalidRating() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        RateContractRequestModel model = RateContractRequestModel.builder()
            .courseId("CSE2310").netId("WinstijnSmit").rating(-1).build();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/rate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isBadRequest());
        assertThat(ContractResponseModel.fromContract(
            contractRepository.getOne(new ContractId("WinstijnSmit", "CSE2310"))
        ).getRating()).isEqualTo(8); // ensure that it did not change.
    }

    @Test
    void rateContract_invalidRating_2() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        RateContractRequestModel model = RateContractRequestModel.builder()
            .courseId("CSE2310").netId("WinstijnSmit").rating(11).build();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/rate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isBadRequest());
        assertThat(ContractResponseModel.fromContract(
            contractRepository.getOne(new ContractId("WinstijnSmit", "CSE2310"))
        ).getRating()).isEqualTo(8); // ensure that it did not change.

    }

    @Test
    void rateContract_nonExistent() throws Exception {
        // Arrange
        mockAuthentication("Stefan", true);
        RateContractRequestModel model = RateContractRequestModel.builder()
            .courseId("ES2525").netId("WinstijnSmit").rating(5).build();

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/rate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Winstijn")
        );

        // Assert
        action.andExpect(status().isNotFound());
    }

    @Test
    void getRatings_null() throws Exception {
        // Act
        ResultActions actionNull = mockMvc.perform(get("/contracts/ratings")
            .header("Authorization", "Bearer Lol"));

        // Assert
        actionNull.andExpect(status().isBadRequest());
    }

    @Test
    void getRatings_empty() throws Exception {
        // Act
        ResultActions actionEmpty = mockMvc.perform(get("/contracts/ratings?netIds=")
            .header("Authorization", "Bearer Lol"));

        // Assert
        actionEmpty.andExpect(status().isBadRequest());
    }

    @Test
    void getRatings_one() throws Exception {
        // Arrange
        String netIds = String.join(",", "WinstijnSmit");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/ratings?netIds=" + netIds)
            .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        var data = parseAverageRatingResult(result);
        assertThat(data.keySet().size()).isEqualTo(1);
        assertThat(data.get("WinstijnSmit")).isEqualTo(8);
    }

    @Test
    void getRatings_notFound() throws Exception {
        // Arrange
        String netIds = String.join(",", "SteveJobs");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/ratings?netIds=" + netIds)
            .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        var data = parseAverageRatingResult(result);
        assertThat(data.keySet().size()).isEqualTo(1);
        assertThat(data.get("SteveJobs")).isEqualTo(-1);
    }

    @Test
    void getRatings_multiple() throws Exception {
        // Arrange
        String netIds = String.join(",", "WinstijnSmit", "PVeldHuis");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/ratings?netIds=" + netIds)
            .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        var data = parseAverageRatingResult(result);
        assertThat(data.keySet().size()).isEqualTo(2);
        assertThat(data.get("WinstijnSmit")).isEqualTo(8);
        assertThat(data.get("PVeldHuis")).isEqualTo(9.4);

    }

    @Test
    void getRatings_multipleWithNotFound() throws Exception {
        // Arrange
        String netIds = String.join(",", "WinstijnSmit", "SteveJobs", "PVeldHuis", "ElonMusk");

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/ratings?netIds=" + netIds)
            .header("Authorization", "Bearer Lol"));

        // Assert
        MvcResult result = action
            .andExpect(status().isOk())
            .andReturn();

        var data = parseAverageRatingResult(result);
        assertThat(data.keySet().size()).isEqualTo(4);
        assertThat(data.get("WinstijnSmit")).isEqualTo(8);
        assertThat(data.get("PVeldHuis")).isEqualTo(9.4);
        assertThat(data.get("SteveJobs")).isEqualTo(-1);
        assertThat(data.get("ElonMusk")).isEqualTo(-1);
    }

    /**
     * Helper method that asserts whether the response contains the contract.
     *
     * @param response list of ContractResponseModel
     * @param contract contract
     * @return assert that the response contains
     *         the contract response model of the contract.
     */
    private org.assertj.core.api.AbstractBooleanAssert<?>
        assertThatResponseContains(List<ContractResponseModel> response, Contract contract) {
        return assertThat(response.contains(ContractResponseModel.fromContract(contract)));
    }

    /**
     * Helper method to convert the MvcResult to a list of ContractResponseModel.
     */
    private List<ContractResponseModel> parseContractsResult(MvcResult result) throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        var list = new ArrayList<ContractResponseModel>();
        List<Map<String, Object>> parsed = JsonUtil.deserialize(jsonString, list.getClass());


        // JsonUtil returns a map of items. Parse them and put them in our list.
        for (Map<String, Object> map : parsed) {
            list.add(new ContractResponseModel(
                        (String) map.get("course"),
                        (String) map.get("netId"),
                        (String) map.get("duties"),
                        (Double) map.get("rating"),
                        (int) map.get("maxHours"),
                        (boolean) map.get("signed")
            ));
        }
        return list;
    }

    /**
     * Helper method to convert MvcResult to HashMap with average ratings.
     */
    private HashMap<String, Double> parseAverageRatingResult(MvcResult result) throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        return (HashMap<String, Double>) JsonUtil.deserialize(jsonString, HashMap.class);
    }


}