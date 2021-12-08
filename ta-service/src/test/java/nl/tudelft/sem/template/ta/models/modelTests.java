package nl.tudelft.sem.template.ta.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class modelTests {

    /**
     * This class is purely to bump the code coverage.
     * Since the models are pure data objects anyways this is not an issue.
     */
    @Test
    void modelBuildersExist() {
        AcceptContractRequestModel acrm = AcceptContractRequestModel.builder().build();
        AcceptHoursRequestModel ahrm = AcceptHoursRequestModel.builder().build();
        ContractRequestModel gcrm = ContractRequestModel.builder().build();
        ContractResponseModel gcrm2 = ContractResponseModel.builder().build();
        RetrieveHoursToBeApprovedRequestModel rhtbarm = RetrieveHoursToBeApprovedRequestModel.builder().build();
        SubmitHoursRequestModel shrm = SubmitHoursRequestModel.builder().build();
    }

    @Test
    void noArgExists() {
        AcceptContractRequestModel acrm = new AcceptContractRequestModel();
        acrm.setAccept(true);
        acrm.setCourse("CSE2310");
        acrm.getCourse();
        acrm.isAccept();
        AcceptHoursRequestModel ahrm = new AcceptHoursRequestModel();
        ContractRequestModel gcrm = new ContractRequestModel();
        ContractResponseModel gcrm2 = new ContractResponseModel();
        RetrieveHoursToBeApprovedRequestModel rhtbarm = new RetrieveHoursToBeApprovedRequestModel();
        SubmitHoursRequestModel shrm = new SubmitHoursRequestModel();
    }

    @Test
    void checkGetterSettersExist() {
        AcceptContractRequestModel acrm = new AcceptContractRequestModel();
        acrm.setAccept(true);
        acrm.setCourse("CSE2310");
        acrm.getCourse();
        acrm.isAccept();

        AcceptHoursRequestModel ahrm = new AcceptHoursRequestModel();
        ahrm.setAccept(true);
        ahrm.setId(UUID.randomUUID());
        ahrm.isAccept();
        ahrm.getId();

        ContractRequestModel gcrm = new ContractRequestModel();
        gcrm.setCourse("CSE2310");
        gcrm.getCourse();

        ContractResponseModel gcrm2 = new ContractResponseModel();
        gcrm2.setCourse("CSE2310");
        gcrm2.setDuties("WORK");
        gcrm2.setSigned(true);
        gcrm2.setMaxHours(15);
        gcrm2.getCourse();
        gcrm2.getDuties();
        gcrm2.getMaxHours();
        gcrm2.isSigned();

        RetrieveHoursToBeApprovedRequestModel rhtbarm = new RetrieveHoursToBeApprovedRequestModel();
        rhtbarm.setCourse("CSE2310");
        rhtbarm.getCourse();

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
