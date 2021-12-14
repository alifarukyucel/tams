package nl.tudelft.sem.template.hiring.services;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.PendingApplicationResponseModel;
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
    private final transient CourseInformation courseInformation;

    public ApplicationService(ContractInformation contractInformation, CourseInformation courseInformation) {
        this.contractInformation = contractInformation;
        this.courseInformation = courseInformation;
    }

    /**
     * Finds all applications with a given courseId and status.
     *
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
        System.out.println(courseInformation.getStartDate(application.getCourseId()));
        if (!application.meetsRequirements()) {
            return false;
        } else if (courseInformation.getStartDate(application.getCourseId()).minusMonths(3)
                .isBefore(LocalDateTime.now())) {
            return false;
        }
        applicationRepository.save(application);
        return true;
    }

    /**
     * Retrieves an application by its course id and netid.
     *
     * @param courseId the course id of the application
     * @param netId    the netid of the application
     * @return the application
     * @throws NoSuchElementException if the application is not found
     */
    public Application get(String courseId, String netId) throws NoSuchElementException {
        ApplicationKey key = new ApplicationKey(courseId, netId);
        Optional<Application> applicationOptional = applicationRepository.findById(key);

        if (applicationOptional.isEmpty()) {
            // Application does not exist
            throw new NoSuchElementException();
        }

        return applicationOptional.get();
    }

    /**
     * Sets the application status to REJECTED.
     *
     * @param courseId the course id of the application
     * @param netId    the netid of the application
     * @throws NoSuchElementException   if the application is not found
     * @throws IllegalArgumentException if the application is not in pending state
     */
    public void reject(String courseId, String netId) throws NoSuchElementException, IllegalArgumentException {
        Application application = this.get(courseId, netId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            // Application is already accepted or rejected
            throw new IllegalArgumentException();
        }

        application.setStatus(ApplicationStatus.REJECTED);

        applicationRepository.save(application);
    }

    /**
     * Takes in a list of applications and extends them with a TA-rating, retreived from the TA-service.
     *
     * @param applications A list of the desired applications to be extended with a rating.
     * @return a list of extendApplicationRequestModels, created with the extended applications and the TA-ratings.
     */
    public List<PendingApplicationResponseModel> extendWithRating(List<Application> applications) {
        List<PendingApplicationResponseModel> extendedApplications = new ArrayList<>();

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
            extendedApplications.add(new PendingApplicationResponseModel(application, rating));
        }
        return extendedApplications;
    }
}
