package nl.tudelft.sem.tams.hiring.interfaces;

import java.util.List;
import java.util.Map;
import nl.tudelft.sem.tams.hiring.services.communication.models.CreateContractRequestModel;

public interface ContractInformation {
    boolean createContract(CreateContractRequestModel model);

    Map<String, Double> getTaRatings(List<String> netIds);
}
