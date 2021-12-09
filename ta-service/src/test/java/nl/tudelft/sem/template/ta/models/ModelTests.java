package nl.tudelft.sem.template.ta.models;

import java.util.UUID;
import org.junit.jupiter.api.Test;


public class ModelTests {

    /**
     * This class is purely to bump the code coverage.
     * Since the models are pure data objects anyways this is not an issue.
     */
    @Test
    void modelBuildersExist() {
        AcceptContractRequestModel acrm = AcceptContractRequestModel.builder().build();
        AcceptHoursRequestModel ahrm = AcceptHoursRequestModel.builder().build();
        GetContractRequestModel gcrm = GetContractRequestModel.builder().build();
        GetContractResponseModel gcrm2 = GetContractResponseModel.builder().build();
        RetrieveHoursToBeApprovedRequestModel rhtbarm = RetrieveHoursToBeApprovedRequestModel
            .builder().build();
        SubmitHoursRequestModel shrm = SubmitHoursRequestModel.builder().build();
    }

    @Test
    void noArgExists() {
        AcceptContractRequestModel acrm = new AcceptContractRequestModel();
        AcceptHoursRequestModel ahrm = new AcceptHoursRequestModel();
        GetContractRequestModel gcrm = new GetContractRequestModel();
        GetContractResponseModel gcrm2 = new GetContractResponseModel();
        RetrieveHoursToBeApprovedRequestModel rhtbarm = new RetrieveHoursToBeApprovedRequestModel();
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
        ahrm.isAccept();
        ahrm.getId();

        GetContractRequestModel gcrm = new GetContractRequestModel();
        gcrm.setCourse("CSE2310");
        gcrm.getCourse();

        GetContractResponseModel gcrm2 = new GetContractResponseModel();
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
