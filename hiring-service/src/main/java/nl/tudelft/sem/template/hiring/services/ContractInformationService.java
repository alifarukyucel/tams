package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContractInformationService implements ContractInformation {

    //TODO: Make "real" implementation"
    @Override
    public Map<String, Float> getTARatings(List<String> netIds) {
        return new HashMap<>();
    }
}
