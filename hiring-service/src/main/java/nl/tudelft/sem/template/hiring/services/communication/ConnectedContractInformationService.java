package nl.tudelft.sem.template.hiring.services.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConnectedContractInformationService implements ContractInformation {
    // Microservice URL from application.properties
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

    @Override
    public Map<String, Double> getTaRatings(List<String> netIds) {
        try {
            String reformattedNetIds = netIds.toString().substring(1, netIds.size()-1);
            String url = baseUrl + "/contracts/ratings?netIds=" + reformattedNetIds;
            return (Map<String, Double>) comm.get(url, Map.class);
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}
