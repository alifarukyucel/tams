package nl.tudelft.sem.tams.hiring.services.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.tams.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.tams.hiring.services.communication.models.CreateContractRequestModel;
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
            String reformattedNetIds = String.join(",", netIds);
            String url = baseUrl + "/contracts/ratings?netIds=" + reformattedNetIds;
            return (Map<String, Double>) comm.get(url, Map.class).getBody();
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}
