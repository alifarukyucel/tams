package nl.tudelft.sem.template.hiring.services.communication;

import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConnectedContractInformationService implements ContractInformation {
    @Value("${microservice.ta.base_url}")
    private transient String baseUrl;

    private transient MicroserviceCommunicationHelper comm;

    public ConnectedContractInformationService(MicroserviceCommunicationHelper comm) {
        this.comm = comm;
    }

    @Override
    public boolean createContract(CreateContractRequestModel model) {
        try {
            comm.post(baseUrl + "/contracts/create", null, model);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
