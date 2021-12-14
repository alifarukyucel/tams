package nl.tudelft.sem.template.hiring.interfaces;

import java.util.List;
import java.util.Map;

public interface ContractInformation {
    Map<String, Float> getTaRatings(List<String> netIds);
}
