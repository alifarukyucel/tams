package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.models.ExtendedApplicationRequestModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;

    private final transient ContractInformation contractInformation;

    public ApplicationService(ContractInformation contractInformation) {
        this.contractInformation = contractInformation;
    }


    /**
     * Checks whether an application meets the requirements and saves or discards it based on this.
     *
     * @param application the application to check.
     * @return boolean whether the application meets the requirements and thus saved.
     */
    public boolean checkAndSave(Application application) {
        if (application.meetsRequirements()) {
            applicationRepository.save(application);
            return true;
        } else {
            return false;
        }
    }

    public List<Application> findAllByCourseAndStatus(String course, ApplicationStatus status) {
        return applicationRepository.findAllByCourseIdAndStatus(course, status);
    }

    public List<ExtendedApplicationRequestModel> extendWithRating(List<Application> applications) {
        List<String> netIds = new ArrayList<>();

        for (Application application : applications) {
            netIds.add(application.getNetId());
        }

        Map<String, Float> taRatings = contractInformation.getTARatings(netIds);

        List<ExtendedApplicationRequestModel> extendedApplications = new ArrayList<>();

        for (Application application : applications) {
            String netId = application.getNetId();
            Float rating = taRatings.get(netId);
            extendedApplications.add(new ExtendedApplicationRequestModel(application, rating));
        }
        return extendedApplications;
    }
}
