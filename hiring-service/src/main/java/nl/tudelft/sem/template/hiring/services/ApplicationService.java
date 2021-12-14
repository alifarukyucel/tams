package nl.tudelft.sem.template.hiring.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.models.ExtendedApplicationRequestModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//PMD.DataflowAnomalies are suppressed because they occur in a place where there is no problem at all.
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;

    private final transient ContractInformation contractInformation;

    public ApplicationService(ContractInformation contractInformation) {
        this.contractInformation = contractInformation;
    }

    /**
     * Finds all applications with a given courseId and status.
     * @param courseId The courseId of the course.
     * @param status The status of the application(s).
     * @return a list of applications.
     */
    public List<Application> findAllByCourseAndStatus(String courseId, ApplicationStatus status) {
        return applicationRepository.findAllByCourseIdAndStatus(courseId, status);
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

    /**
     * Takes in a list of applications and extends them with a TA-rating, retreived from the TA-service.
     *
     * @param applications A list of the desired applications to be extended with a rating.
     * @return a list of extendApplicationRequestModels, created with the extended applications and the TA-ratings.
     */
    public List<ExtendedApplicationRequestModel> extendWithRating(List<Application> applications) {
        List<ExtendedApplicationRequestModel> extendedApplications = new ArrayList<>();

        //This check makes sure no data is fetched when there are no applications at all.
        if (applications.isEmpty()) {
            return extendedApplications;
        }

        List<String> netIds = new ArrayList<>();

        for (Application application : applications) {
            netIds.add(application.getNetId());
        }

        Map<String, Float> taRatings = contractInformation.getTaRatings(netIds);

        for (Application application : applications) {
            String netId = application.getNetId();
            Float rating = taRatings.get(netId);
            extendedApplications.add(new ExtendedApplicationRequestModel(application, rating));
        }
        return extendedApplications;
    }
}
