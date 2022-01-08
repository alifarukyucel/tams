package nl.tudelft.sem.template.hiring.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.template.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.template.hiring.repositories.TeachingAssistantApplicationRepository;
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;
import org.springframework.stereotype.Service;


@Service
public class HiringService {

    private final transient TeachingAssistantApplicationRepository taApplicationRepository;

    private final transient ContractInformation contractInformation;
    private final transient CourseInformation courseInformation;

    // maximum number of applications per student
    private static final transient int maxCandidacies = 3;

    /**
     * Constructor for the application service, with the corresponding repositories / information classes.
     * Spring automatically chooses the best implementation for those interfaces.
     *
     * @param taApplicationRepository     An applicationRepository
     * @param contractInformation       The contract information
     * @param courseInformation         The course information
     */
    public HiringService(TeachingAssistantApplicationRepository taApplicationRepository,
                         ContractInformation contractInformation,
                         CourseInformation courseInformation) {
        this.taApplicationRepository = taApplicationRepository;
        this.contractInformation = contractInformation;
        this.courseInformation = courseInformation;
    }

    /**
     * Checks whether an application meets the requirements and saves or discards it based on this.
     * It also checks whether the course isn't starting in less than 3 months already.
     *
     * @param teachingAssistantApplication the application to check.
     * @throws NoSuchElementException when the provided course does not exist
     * @throws IllegalArgumentException when the provided grade is not valid
     * @throws IllegalArgumentException when the application doesn't meet the requirements
     * @throws IllegalArgumentException when the deadline for the course has already passed
     */
    public void checkAndSave(TeachingAssistantApplication teachingAssistantApplication) {
        CourseInformationResponseModel course = courseInformation.getCourseById(teachingAssistantApplication.getCourseId());
        if (course == null) {
            //Course does not exist
            throw new NoSuchElementException("This course does not exist.");
        } else if (!teachingAssistantApplication.hasValidGrade()) {
            throw new IllegalArgumentException("Please provide a valid grade between 1.0 and 10.0.");
        } else if (!teachingAssistantApplication.meetsRequirements()) {
            throw new IllegalArgumentException("Your TA-application does not meet the requirements.");
        } else if (course.getStartDate().minusWeeks(3)
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The deadline for applying for this course has already passed");
        }
        taApplicationRepository.save(teachingAssistantApplication);
    }

    /**
     * Finds all applications with a given courseId and status.
     *
     * @param courseId The courseId of the course.
     * @param status The status of the application(s).
     * @return a list of applications.
     */
    public List<TeachingAssistantApplication> findAllByCourseAndStatus(String courseId, ApplicationStatus status) {
        return taApplicationRepository.findAllByCourseIdAndStatus(courseId, status);
    }

    /**
     * Retrieves an application by its course id and netid.
     *
     * @param courseId the course id of the application
     * @param netId    the netid of the application
     * @return the application
     * @throws NoSuchElementException if the application is not found
     */
    public TeachingAssistantApplication get(String courseId, String netId) throws NoSuchElementException {
        TeachingAssistantApplicationKey key = new TeachingAssistantApplicationKey(courseId, netId);
        Optional<TeachingAssistantApplication> applicationOptional = taApplicationRepository.findById(key);

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

            taApplicationRepository.delete(this.get(courseId, netId));
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
        TeachingAssistantApplication teachingAssistantApplication = this.get(courseId, netId);

        if (teachingAssistantApplication.getStatus() != ApplicationStatus.PENDING) {
            // Application is already accepted or rejected
            throw new IllegalArgumentException();
        }

        teachingAssistantApplication.setStatus(ApplicationStatus.REJECTED);

        taApplicationRepository.save(teachingAssistantApplication);
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
        TeachingAssistantApplication teachingAssistantApplication = this.get(courseId, netId);

        if (teachingAssistantApplication.getStatus() != ApplicationStatus.PENDING) {
            // Application is already accepted or rejected
            throw new IllegalArgumentException();
        }

        boolean result = contractInformation.createContract(CreateContractRequestModel.builder()
                .withCourseId(courseId)
                .withNetId(netId)
                .withDuties(duties)
                .withMaxHours(maxHours)
                .withTaContactEmail(teachingAssistantApplication.getContactEmail())
                .build());

        if (!result) {
            // contract creation failed
            throw new IllegalArgumentException();
        }

        teachingAssistantApplication.setStatus(ApplicationStatus.ACCEPTED);

        taApplicationRepository.save(teachingAssistantApplication);
    }

    /**
     * Takes in a list of applications and extends them with a TA-rating, retreived from the TA-service.
     *
     * @param teachingAssistantApplications A list of the desired applications to be extended with a rating.
     * @return a list of extendApplicationRequestModels, created with the extended applications and the TA-ratings.
     */
    //PMD.DataflowAnomalies are suppressed because they occur in a place where there is no problem at all.
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<PendingTeachingAssistantApplicationResponseModel> extendWithRating(
            List<TeachingAssistantApplication> teachingAssistantApplications) {
        List<PendingTeachingAssistantApplicationResponseModel> extendedApplications = new ArrayList<>();

        //This check makes sure no data is fetched when there are no applications at all.
        if (teachingAssistantApplications.isEmpty()) {
            return extendedApplications;
        }

        List<String> netIds = new ArrayList<>();

        for (TeachingAssistantApplication teachingAssistantApplication : teachingAssistantApplications) {
            netIds.add(teachingAssistantApplication.getNetId());
        }

        Map<String, Double> taRatings = contractInformation.getTaRatings(netIds);

        for (TeachingAssistantApplication teachingAssistantApplication : teachingAssistantApplications) {
            String netId = teachingAssistantApplication.getNetId();
            Double rating = taRatings.get(netId);
            extendedApplications.add(new PendingTeachingAssistantApplicationResponseModel(
                    teachingAssistantApplication, rating));
        }
        return extendedApplications;
    }

    /**
     * Retrieving the status of the application to see whether someone is accepted or rejected.
     *
     * @param courseId the courseId of the course for which we want to retrieve the status.
     * @param netId the netId of the user that wants to retrieve a status.
     * @return String containing the status in readable format.
     * @throws NoSuchElementException when there is no application for that key
     */
    public ApplicationStatus retrieveStatus(String courseId, String netId) {
        TeachingAssistantApplicationKey key = new TeachingAssistantApplicationKey(courseId, netId);
        Optional<TeachingAssistantApplication> applicationOptional = taApplicationRepository.findById(key);

        if (applicationOptional.isEmpty()) {
            throw new NoSuchElementException();
        }

        TeachingAssistantApplication teachingAssistantApplication = applicationOptional.get();
        return teachingAssistantApplication.getStatus();
    }


    /**
     * Gets all applications that belong to a specific user.
     *
     * @param netId the netId of the user to get applications from.
     * @return a list of all applications from the user.
     */
    public List<TeachingAssistantApplication> getApplicationFromStudent(String netId) {
        List<TeachingAssistantApplication> allTeachingAssistantApplications = taApplicationRepository.findAll();
        List<TeachingAssistantApplication> result = new ArrayList<>();
        for (TeachingAssistantApplication teachingAssistantApplication : allTeachingAssistantApplications) {
            if (teachingAssistantApplication.getNetId().equals(netId)) {
                result.add(teachingAssistantApplication);
            }
        }
        return result;
    }

    /**
     * Checks whether a user has already applied for 3 courses.
     *
     * @param netId the netid of the user for which we check the amount of applications.
     * @return false when the maximum number of applications hasn't been reached or true otherwise.
     */
    public boolean hasReachedMaxApplication(String netId) {
        long pendingApplicationsCount = getApplicationFromStudent(netId)
                .stream()
                .filter(application ->
                        application.getStatus().equals(ApplicationStatus.PENDING))
                .count();

        return pendingApplicationsCount >= maxCandidacies;
    }
}
