package nl.tudelft.sem.template.ta.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.template.ta.models.ContractRequestModel;
import nl.tudelft.sem.template.ta.models.ContractResponseModel;
import nl.tudelft.sem.template.ta.models.CourseRequestModel;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.security.TokenVerifier;
import nl.tudelft.sem.template.ta.services.ContractService;
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

import static nl.tudelft.sem.template.ta.utils.JsonUtil.deserialize;
import static nl.tudelft.sem.template.ta.utils.JsonUtil.serialize;

import javax.transaction.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockAuthenticationManager", "mockTokenVerifier"})
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

    private Contract defaultContract;
    private List<Contract> contracts;

    @BeforeEach
    void setUp() {
        contractRepository.deleteAll();
        contracts = new ArrayList<Contract>();

        // Save basic contract in db.
        defaultContract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        defaultContract = contractRepository.save(defaultContract);
        contracts.add(defaultContract);

        Contract secondContract = Contract.builder()
                .netId("WinstijnSmit")
                .courseId("CSE2310")
                .maxHours(10)
                .duties("Work really hard")
                .signed(true)
                .build();
        contractRepository.save(secondContract);
        contracts.add(secondContract);

        Contract thirdContract = Contract.builder()
                .netId("WinstijnSmit")
                .courseId("CSE1250")
                .maxHours(2)
                .duties("No need to work hard")
                .signed(false)
                .build();
        contractRepository.save(thirdContract);
        contracts.add(thirdContract);

        mockAuthentication(defaultContract.getNetId());
    }

    // Mock authentication to show that we are signed in as a certain user.
    void mockAuthentication(String netId){
        when(mockAuthenticationManager.getNetid()).thenReturn(netId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(netId);
    }

    @Test
    void signExistingContract() throws Exception{
        // arrange
        AcceptContractRequestModel model = AcceptContractRequestModel.builder().accept(true).course(defaultContract.getCourseId()).build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService.getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isOk());
        assertThat(savedContract.getSigned()).isTrue();
    }

    @Test
    void unSignExistingContract() throws Exception {
        // arrange
        defaultContract.setSigned(true);
        defaultContract = contractRepository.save(defaultContract);
        AcceptContractRequestModel model = AcceptContractRequestModel.builder().accept(false).course(defaultContract.getCourseId()).build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService.getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isOk());
        assertThat(savedContract.getSigned()).isTrue();
    }

    @Test
    void signNonExistingContract() throws Exception {
        AcceptContractRequestModel model = AcceptContractRequestModel.builder().accept(true).course("CSEISFAKE").build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService.getContract(defaultContract.getNetId(), defaultContract.getCourseId());

        results.andExpect(status().isNotFound());
        assertThat(savedContract.getSigned()).isFalse();
    }

    @Test
    void signExistingContractByPassingNullValues() throws Exception {
        AcceptContractRequestModel model = AcceptContractRequestModel.builder().accept(true).course(null).build();

        // act
        ResultActions results = mockMvc.perform(put("/contracts/sign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        Contract savedContract = contractService.getContract(defaultContract.getNetId(), defaultContract.getCourseId());

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

        // Assert
        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThat(responseContracts.contains(contracts.get(0).toResponseModel())).isTrue();
        assertThat(responseContracts.contains(contracts.get(1).toResponseModel())).isFalse();
        assertThat(responseContracts.contains(contracts.get(2).toResponseModel())).isFalse();
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
        assertThat(responseContracts.contains(contracts.get(0).toResponseModel())).isFalse();
        assertThat(responseContracts.contains(contracts.get(1).toResponseModel())).isTrue();
        assertThat(responseContracts.contains(contracts.get(2).toResponseModel())).isTrue();
    }

    @Test
    void getSignedInContracts_filterCourse() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit");
        CourseRequestModel model = new CourseRequestModel();
        model.setCourse("CSE2310");

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/mine")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Winstijn")
                .content(serialize(model)));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThat(responseContracts.contains(contracts.get(0).toResponseModel())).isFalse();
        assertThat(responseContracts.contains(contracts.get(1).toResponseModel())).isTrue();
        assertThat(responseContracts.contains(contracts.get(2).toResponseModel())).isFalse();
    }


    @Test
    void getSignedInContracts_notSignedIn() throws Exception {
        // Arrange
        mockAuthentication(null);

        // Act
        ResultActions action = mockMvc.perform(get("/contracts/mine")
                .header("Authorization", "Bearer Lol"));

        // Assert
        action.andExpect(status().isUnauthorized());
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
        mockAuthentication("WinstijnSmit");
        ContractRequestModel model = new ContractRequestModel();
        model.setCourse("CSE2310");
        model.setNetId("PVeldHuis");

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/get")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Winstijn")
                .content(serialize(model)));

        // Assert
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        List<ContractResponseModel> responseContracts = parseContractsResult(result);
        assertThat(responseContracts.contains(contracts.get(0).toResponseModel())).isTrue();
        assertThat(responseContracts.contains(contracts.get(1).toResponseModel())).isFalse();
        assertThat(responseContracts.contains(contracts.get(2).toResponseModel())).isFalse();
    }

    @Test
    void getContracts_notFound() throws Exception {
        // Arrange
        mockAuthentication("WinstijnSmit");
        ContractRequestModel model = new ContractRequestModel();
        model.setCourse("CSE1000");
        model.setNetId("WinstijnSmit");

        // Act
        ResultActions action = mockMvc.perform(post("/contracts/get")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Winstijn")
                .content(serialize(model)));

        // Assert
        action.andExpect(status().isNotFound());
    }


    /**
     * Helper method to convert the MvcResult to a list of ContractResponseModel
     */
    private List<ContractResponseModel> parseContractsResult(MvcResult result) throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        var list = new ArrayList<ContractResponseModel>();
        List<Map<String, Object>> parsed = JsonUtil.deserialize(jsonString, list.getClass());

        // JsonUtil returns a map of items. Parse them and put them in our list.
        for ( Map<String, Object> map : parsed ) {
            list.add(new ContractResponseModel((String) map.get("course"), (String) map.get("duties"), (int) map.get("maxHours"), (boolean) map.get("signed")));
        }
        return list;
    }


}