package nl.tudelft.sem.template.hiring.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;
import org.springframework.stereotype.Service;


@Service
public class ApplicationService {

    private final transient ApplicationRepository applicationRepository;

    private final transient ContractInformation contractInformation;
    private final transient CourseInformation courseInformation;


    /**
     * Constructor for the application service, with the corresponding repositories / information classes.
     * Spring automatically chooses the best implementation for those interfaces.
     *
     * @param applicationRepository     An applicationRepository
     * @param contractInformation       The contract information
     * @param courseInformation         The course information
     */
    public ApplicationService(ApplicationRepository applicationRepository, ContractInformation contractInformation,
                              CourseInformation courseInformation) {
        this.applicationRepository = applicationRepository;
        this.contractInformation = contractInformation;
        this.courseInformation = courseInformation;
    }

    /**
     * Checks whether an application meets the requirements and saves or discards it based on this.
     * It also checks whether the course isn't starting in less than 3 months already.
     *
     * @param application the application to check.
     * @return boolean whether the application meets the requirements and thus saved.
     */
    public boolean checkAndSave(Application application) {
        CourseInformationResponseModel course = courseInformation.getCourseById(application.getCourseId());
        if (course == null) {
            //Course does not exist
            return false;
        } else if (!application.meetsRequirements()) {
            return false;
        } else if (course.getStartDate().minusWeeks(3)
                .isBefore(LocalDateTime.now())) {
            return false;
        }
        applicationRepository.save(application);
        return true;
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
     * Deletes an application from the database, if more than 3 weeks before start of the course.
     *
     * @param courseId courseId from course to withdraw from
     * @param netId netId from user who wants to withdraw
     * @return true if on time or false if too late
     */
    public boolean checkAndWithdraw(String courseId, String netId) {
        LocalDateTime deadline = courseInformation.startDate(courseId).minusWeeks(3);
        if (LocalDateTime.now().compareTo(deadline) < 0) {

            applicationRepository.delete(this.get(courseId, netId));
            return true;
        }
        return false;
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
     * Sets the application status to ACCEPTED and creates a contract.
     *
     * @param courseId the course id of the application
     * @param netId    the netid of the application
     * @throws NoSuchElementException   if the application is not found
     * @throws IllegalArgumentException if the application is not in pending state or contract creation fails
     */
    public void accept(String courseId, String netId, String duties, int maxHours)
            throws NoSuchElementException, IllegalArgumentException {
        Application application = this.get(courseId, netId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            // Application is already accepted or rejected
            throw new IllegalArgumentException();
        }

        boolean result = contractInformation.createContract(CreateContractRequestModel.builder()
                .withCourseId(courseId)
                .withNetId(netId)
                .withDuties(duties)
                .withMaxHours(maxHours)
                .build());

        if (!result) {
            // contract creation failed
            throw new IllegalArgumentException();
        }

        application.setStatus(ApplicationStatus.ACCEPTED);

        applicationRepository.save(application);
    }

    /**
     * Takes in a list of applications and extends them with a TA-rating, retreived from the TA-service.
     *
     * @param applications A list of the desired applications to be extended with a rating.
     * @return a list of extendApplicationRequestModels, created with the extended applications and the TA-ratings.
     */
    //PMD.DataflowAnomalies are suppressed because they occur in a place where there is no problem at all.
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
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

    /**
     * Retrieving the status of the application to see whether someone is accepted or rejected.
     * @param courseId
     * @param netId
     * @return String containing the status in readable format.
     * @throws NoSuchElementException
     */
    public ApplicationStatus retrieveStatus(String courseId, String netId) {
        ApplicationKey key = new ApplicationKey(courseId, netId);
        Optional<Application> applicationOptional = applicationRepository.findById(key);

        try {
            Application application = applicationOptional.get();
            return application.getStatus();

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException();
        }
    }

}
