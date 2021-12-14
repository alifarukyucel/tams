package nl.tudelft.sem.template.hiring.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.ExtendedApplicationRequestModel;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;

    private final transient ContractInformation contractInformation;
    private final transient CourseInformation courseInformation;

    public ApplicationService(ContractInformation contractInformation, CourseInformation courseInformation) {
        this.contractInformation = contractInformation;
        this.courseInformation = courseInformation;
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
     * It also checks whether the course isn't starting in less than 3 months already.
     *
     * @param application the application to check.
     * @return boolean whether the application meets the requirements and thus saved.
     */
    public boolean checkAndSave(Application application) {
        if (application.meetsRequirements()) {
            return false;
        } else if (courseInformation.getStartDate(application.getCourseId()).minusMonths(3)
                .isAfter(LocalDateTime.now())) {
            return false;
        }
        applicationRepository.save(application);
        return true;
    }

    /**
     * Takes in a list of applications and extends them with a TA-rating, retreived from the TA-service.
     *
     * @param applications A list of the desired applications to be extended with a rating.
     * @return a list of extendApplicationRequestModels, created with the extended applications and the TA-ratings.
     */
    public List<ExtendedApplicationRequestModel> extendWithRating(List<Application> applications) {
        List<String> netIds = new ArrayList<>();
        for (Application application : applications) {
            netIds.add(application.getNetId());
        }

        List<ExtendedApplicationRequestModel> extendedApplications = new ArrayList<>();
        Map<String, Float> taRatings = contractInformation.getTaRatings(netIds);

        for (Application application : applications) {
            String netId = application.getNetId();
            Float rating = taRatings.get(netId);
            extendedApplications.add(new ExtendedApplicationRequestModel(application, rating));
        }
        return extendedApplications;
    }
}
