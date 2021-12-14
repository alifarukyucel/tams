package nl.tudelft.sem.template.hiring.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import org.springframework.stereotype.Service;

@Service
public class ContractInformationService implements ContractInformation {

    //TODO: Make "real" implementation"
    @Override
    public Map<String, Float> getTaRatings(List<String> netIds) {
        return new HashMap<>();
    }
}
