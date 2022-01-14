package nl.tudelft.sem.tams.hiring.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.tams.hiring.models.TeachingAssistantApplicationAcceptRequestModel;
import nl.tudelft.sem.tams.hiring.utils.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static nl.tudelft.sem.tams.hiring.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class LecturerHiringControllerTest extends BaseHiringControllerTest {
    @Test
    public void rejectValidApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId(taApplication.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isOk());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void rejectValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId(taApplication.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isForbidden());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    public void rejectNonexistentApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId("invalidNetid")
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isNotFound());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    /**
     * Test for rejecting an application in a non-pending state.
     *
     * @param status the test status (non-pending)
     */
    @ParameterizedTest
    @CsvSource({"ACCEPTED", "REJECTED"})
    public void rejectNonPendingApplication(String status) throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationKey lookup = TeachingAssistantApplicationKey.builder()
                .courseId(taApplication.getCourseId())
                .netId(taApplication.getNetId())
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(lookup))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .get();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
    }

    @Test
    public void getPendingApplicationsTest() throws Exception {
        //Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication taApplication2 = new TeachingAssistantApplication("CSE1300", "wsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication taApplication3 = new TeachingAssistantApplication("CSE1300", "nsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.ACCEPTED);
        taApplicationRepository.save(taApplication);
        taApplicationRepository.save(taApplication2);
        taApplicationRepository.save(taApplication3);
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"jsmith", "wsmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("jsmith", 8.0d);
                put("wsmith", 9.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();


        //Parse json
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);

        PendingTeachingAssistantApplicationResponseModel model = new PendingTeachingAssistantApplicationResponseModel(
                taApplication, 8.0d);
        PendingTeachingAssistantApplicationResponseModel model2 = new PendingTeachingAssistantApplicationResponseModel(
                taApplication2, 9.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedResult = new ArrayList<>() {{
                add(model);
                add(model2);
            }
        };

        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void getPendingApplicationsWhileNotBeingResponsibleLecturerTest() throws Exception {
        //Arrange
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(false);

        //Act
        ResultActions result = mockMvc.perform(get("/applications/CSE1300/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        result.andExpect(status().isForbidden());
    }

    private List<PendingTeachingAssistantApplicationResponseModel> parsePendingApplicationsResult(MvcResult result)
            throws Exception {
        String jsonString = result.getResponse().getContentAsString();
        var res = new ArrayList<PendingTeachingAssistantApplicationResponseModel>();
        List<Map<String, Object>> parsed = JsonUtil.deserialize(jsonString, res.getClass());

        for (Map<String, Object> map : parsed) {
            res.add(new PendingTeachingAssistantApplicationResponseModel(
                    (String) map.get("courseId"),
                    (String) map.get("netId"),
                    ((Double) map.get("grade")).floatValue(),
                    (String) map.get("motivation"),
                    ((Double) map.get("taRating")))
            );
        }
        return res;
    }

    @Test
    public void getRecommendedApplicationsWhileNotBeingResponsibleLecturerTest() throws Exception {
        //Arrange
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(false);

        //Act
        ResultActions result = mockMvc.perform(get("/applications/CSE1300/recommended/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        result.andExpect(status().isForbidden());
    }

    /**
     * Test for checking if the method still works when an invalid index (too low) is provided.
     * A parameterized test is used here to make sure it works for both 0 and a negative amount.
     *
     * @param amount The (invalid) amount of recommended applications to request.
     */
    @ParameterizedTest
    @CsvSource({"0", "-1"})
    public void getRecommendedApplicationsIndexTooLow(String amount) throws Exception {
        //Arrange
        List<PendingTeachingAssistantApplicationResponseModel> expected = new ArrayList<>();
        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/" + amount)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expected);
    }

    @Test
    public void getRecommendedApplicationsIndexTooHigh() throws Exception {
        //Arrange
        TeachingAssistantApplication application = new TeachingAssistantApplication("CSE1300", "asmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        taApplicationRepository.save(application);

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"asmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("asmith", 8.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        var model = new PendingTeachingAssistantApplicationResponseModel(application, 8.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedResult = List.of(model);


        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void getRecommendedApplications() throws Exception {
        //Arrange
        TeachingAssistantApplication application = new TeachingAssistantApplication("CSE1300", "asmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application2 = new TeachingAssistantApplication("CSE1300", "bsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application3 = new TeachingAssistantApplication("CSE1300", "csmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application4 = new TeachingAssistantApplication("CSE1300", "dsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application5 = new TeachingAssistantApplication("CSE1300", "esmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.PENDING);
        TeachingAssistantApplication application6 = new TeachingAssistantApplication("CSE1300", "fsmith", 7.0f,
                "I want to be cool too!", ApplicationStatus.ACCEPTED);
        taApplicationRepository.save(application);
        taApplicationRepository.save(application2);
        taApplicationRepository.save(application3);
        taApplicationRepository.save(application4);
        taApplicationRepository.save(application5);
        taApplicationRepository.save(application6);

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, "CSE1300"))
                .thenReturn(true);

        String[] netIds = new String[]{"asmith", "bsmith", "csmith", "dsmith", "esmith"};
        Map<String, Double> expectedMap = new HashMap<>() {{
                put("asmith", 8.0d);
                put("bsmith", 9.0d);
                put("csmith", 3.0d);
                put("dsmith", 2.0d);
                put("esmith", -1.0d);
            }
        };
        when(mockContractInformation.getTaRatings(List.of(netIds)))
                .thenReturn(expectedMap);

        //Notice how in the following code application4 is not added because it isn't in the top-4 recommended.
        //Also notice the order of which the applications were added to the expected result, in the order of TA-rating.
        PendingTeachingAssistantApplicationResponseModel model2 = new PendingTeachingAssistantApplicationResponseModel(
                application2, 9.0d);
        PendingTeachingAssistantApplicationResponseModel model = new PendingTeachingAssistantApplicationResponseModel(
                application, 8.0d);
        PendingTeachingAssistantApplicationResponseModel model5 = new PendingTeachingAssistantApplicationResponseModel(
                application5, -1.0d);
        PendingTeachingAssistantApplicationResponseModel model3 = new PendingTeachingAssistantApplicationResponseModel(
                application3, 3.0d);
        List<PendingTeachingAssistantApplicationResponseModel> expectedResult = List.of(model2, model, model5, model3);

        //Act
        ResultActions action = mockMvc.perform(get("/applications/CSE1300/recommended/4")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer Joe"));
        MvcResult result = action
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        List<PendingTeachingAssistantApplicationResponseModel> res = parsePendingApplicationsResult(result);
        assertThat(res).isEqualTo(expectedResult);
    }

    @Test
    public void acceptValidApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING, "tueindhoven@utwente.nl");
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isOk());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(taApplication.getCourseId())
                        && contract.getNetId().equals(taApplication.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
                        && contract.getTaContactEmail().equals(taApplication.getContactEmail())
        ));
    }

    @Test
    public void acceptValidApplicationButContractCreationFails() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation).createContract(argThat(contract ->
                contract.getCourseId().equals(taApplication.getCourseId())
                        && contract.getNetId().equals(taApplication.getNetId())
                        && contract.getDuties().equals(model.getDuties())
                        && contract.getMaxHours() == model.getMaxHours()
        ));
    }

    @Test
    public void acceptValidApplicationWhileNotBeingResponsibleLecturer() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(false);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isForbidden());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation, times(0)).createContract(any());
    }

    @Test
    public void acceptNonexistentApplication() throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.PENDING);
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId("invalidNetid")
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isNotFound());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(mockContractInformation, times(0)).createContract(any());
    }

    /**
     * Test for accepting an application in a non-pending state.
     *
     * @param status the test status (non-pending)
     */
    @ParameterizedTest
    @CsvSource({"ACCEPTED", "REJECTED"})
    public void acceptNonPendingApplication(String status) throws Exception {
        // Arrange
        TeachingAssistantApplication taApplication = new TeachingAssistantApplication("CSE1300", "jsmith", 7.0f,
                "I just want to be a cool!", ApplicationStatus.valueOf(status));
        taApplicationRepository.save(taApplication);

        TeachingAssistantApplicationAcceptRequestModel model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(taApplication.getCourseId())
                .withNetId(taApplication.getNetId())
                .withDuties("Be a good TA")
                .withMaxHours(42)
                .build();

        when(mockCourseInformation.isResponsibleLecturer(exampleNetId, taApplication.getCourseId()))
                .thenReturn(true);

        when(mockContractInformation.createContract(any())).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(model))
                .header("Authorization", "Bearer Joe"));

        // Assert
        result.andExpect(status().isConflict());

        TeachingAssistantApplication actual = taApplicationRepository
                .findById(new TeachingAssistantApplicationKey(taApplication.getCourseId(), taApplication.getNetId()))
                .orElseThrow();
        assertThat(actual.getStatus()).isEqualTo(ApplicationStatus.valueOf(status));
        verify(mockContractInformation, times(0)).createContract(any());
    }
}
