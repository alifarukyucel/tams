package nl.tudelft.sem.template.hiring.interfaces;

import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;

public interface ContractInformation {
    boolean createContract(CreateContractRequestModel model);
}
