package nl.tudelft.sem.template.hiring.interfaces;

import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;

public interface ContractInformation {
    boolean createContract(CreateContractRequestModel model);
    Map<String, Float> getTaRatings(List<String> netIds);
}
