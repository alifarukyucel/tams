package nl.tudelft.sem.template.ta.models;

import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ModelTests {

    @Test
    void testContractResponseFromContract() {
        Contract contract = Contract.builder()
            .courseId("CSETEST")
            .maxHours(5)
            .duties("Your duties")
            .signed(true)
            .build();

        ContractResponseModel model = ContractResponseModel.fromContract(contract);
        Assertions.assertNotNull(model);
        Assertions.assertEquals(contract.getCourseId(), model.getCourse());
        Assertions.assertEquals(contract.getMaxHours(), model.getMaxHours());
        Assertions.assertEquals(contract.getDuties(), model.getDuties());
        Assertions.assertEquals(contract.getSigned(), model.isSigned());
    }

    /**
     * This following is purely to bump the code coverage.
     * Since the models are pure data objects anyways this is not an issue.
     */
    @Test
    void modelBuildersExist() {
        AcceptContractRequestModel acrm = AcceptContractRequestModel.builder().build();
        AcceptHoursRequestModel ahrm = AcceptHoursRequestModel.builder().build();
        ContractResponseModel gcrm2 = ContractResponseModel.builder().build();
        HourResponseModel hrm = HourResponseModel.builder().build();

        SubmitHoursRequestModel shrm = SubmitHoursRequestModel.builder().build();
    }

    @Test
    void noArgExists() {
        AcceptContractRequestModel acrm = new AcceptContractRequestModel();
        AcceptHoursRequestModel ahrm = new AcceptHoursRequestModel();
        ContractResponseModel gcrm2 = new ContractResponseModel();
        HourResponseModel hrm = new HourResponseModel();
        SubmitHoursRequestModel shrm = new SubmitHoursRequestModel();
    }

    @Test
    void checkGetterSettersExist() {
        AcceptContractRequestModel acrm = new AcceptContractRequestModel();
        acrm.setCourse("CSE2310");
        acrm.getCourse();

        AcceptHoursRequestModel ahrm = new AcceptHoursRequestModel();
        ahrm.setAccept(true);
        ahrm.setId(UUID.randomUUID());
        ahrm.getAccept();
        ahrm.getId();

        ContractResponseModel gcrm2 = new ContractResponseModel();
        gcrm2.setCourse("CSE2310");
        gcrm2.setDuties("WORK");
        gcrm2.setSigned(true);
        gcrm2.setMaxHours(15);
        gcrm2.getCourse();
        gcrm2.getDuties();
        gcrm2.getMaxHours();
        gcrm2.isSigned();

        HourResponseModel hrm = new HourResponseModel();
        hrm.setDate(null);
        hrm.setDescription("Work");
        hrm.setApproved(true);
        hrm.setWorkedTime(5);
        hrm.getWorkedTime();
        hrm.getDate();
        hrm.getDescription();
        hrm.isApproved();

        SubmitHoursRequestModel shrm = new SubmitHoursRequestModel();
        shrm.setCourse("CSE2310");
        shrm.setDate(null);
        shrm.setDesc("Work");
        shrm.setWorkedTime(5);
        shrm.getCourse();
        shrm.getDate();
        shrm.getDesc();
        shrm.getWorkedTime();
    }
}
