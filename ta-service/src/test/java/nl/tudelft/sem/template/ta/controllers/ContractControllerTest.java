package nl.tudelft.sem.template.ta.controllers;

import static nl.tudelft.sem.template.ta.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.models.AcceptContractRequestModel;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.security.AuthManager;
import nl.tudelft.sem.template.ta.security.TokenVerifier;
import nl.tudelft.sem.template.ta.services.ContractService;
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

    @BeforeEach
    void setUp() {
        contractRepository.deleteAll();
        // Save basic contract in db.
        defaultContract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        defaultContract = contractRepository.save(defaultContract);
        when(mockAuthenticationManager.getNetid()).thenReturn(defaultContract.getNetId());
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(defaultContract.getNetId());
    }

    @Test
    void signExistingContract() throws Exception {
        // arrange
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .accept(true)
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
    void unSignExistingContract() throws Exception {
        // arrange
        defaultContract.setSigned(true);
        defaultContract = contractRepository.save(defaultContract);
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .accept(false)
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
    void signNonExistingContract() throws Exception {
        AcceptContractRequestModel model = AcceptContractRequestModel.builder()
            .accept(true)
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
            .accept(true)
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


}