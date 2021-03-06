package nl.tudelft.sem.tams.hiring.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.tams.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.tams.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.tams.hiring.models.PendingTeachingAssistantApplicationResponseModel;
import nl.tudelft.sem.tams.hiring.providers.TimeProvider;
import nl.tudelft.sem.tams.hiring.repositories.TeachingAssistantApplicationRepository;
import nl.tudelft.sem.tams.hiring.services.communication.models.CreateContractRequestModel;
import org.springframework.stereotype.Service;


@Service
public class HiringService {

    private final transient TeachingAssistantApplicationRepository taApplicationRepository;

    private final transient ContractInformation contractInformation;
    private final transient CourseInformation courseInformation;

    private final transient TimeProvider timeProvider;

    // maximum number of applications per student
    private static final transient int maxCandidacies = 3;

    // Amount of weeks before a course starts when withdrawal is still allowed
    // e.g. withdrawal is allowed x weeks before the course starts.
    private static final transient int applicationWindowDurationInWeeks = 3;

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
                         CourseInformation courseInformation, TimeProvider timeProvider) {
        this.taApplicationRepository = taApplicationRepository;
        this.contractInformation = contractInformation;
        this.courseInformation = courseInformation;
        this.timeProvider = timeProvider;
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
        checkMaximumApplications(teachingAssistantApplication.getNetId());

        checkApplicationDeadline(teachingAssistantApplication);

        checkApplicationRequirements(teachingAssistantApplication);

        taApplicationRepository.save(teachingAssistantApplication);
    }

    private void checkMaximumApplications(String netId) {
        if (hasReachedMaxApplication(netId)) {
            throw new IllegalArgumentException("Maximum number of applications has been reached!");
        }
    }

    private void checkApplicationDeadline(TeachingAssistantApplication teachingAssistantApplication) {
        if (!isApplicationPeriodOpen(teachingAssistantApplication.getCourseId())) {
            throw new IllegalArgumentException("The deadline for applying for this course has already passed");
        }
    }

    private void checkApplicationRequirements(TeachingAssistantApplication teachingAssistantApplication) {
        if (!teachingAssistantApplication.hasValidGrade()) {
            throw new IllegalArgumentException("Please provide a valid grade between 1.0 and 10.0.");
        }

        if (!teachingAssistantApplication.meetsRequirements()) {
            throw new IllegalArgumentException("Your TA-application does not meet the requirements.");
        }
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
        boolean canApplyAndWithdraw = isApplicationPeriodOpen(courseId);

        if (!canApplyAndWithdraw) {
            return false;
        }

        taApplicationRepository.delete(this.get(courseId, netId));
        return true;
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
     * Takes in a list of applications and extends them with a TA-rating, retreived from the TA-microservice.
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

        List<String> netIds = teachingAssistantApplications
                .stream()
                .map(application ->
                        application.getNetId()).collect(Collectors.toList());

        Map<String, Double> taRatings = contractInformation.getTaRatings(netIds);

        return teachingAssistantApplications
                .stream()
                .map(application ->
                        new PendingTeachingAssistantApplicationResponseModel(
                                application, taRatings.get(application.getNetId())))
                .collect(Collectors.toList());
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
        return allTeachingAssistantApplications
                .stream()
                .filter(application ->
                        application.getNetId().equals(netId)).collect(Collectors.toList());
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

    private boolean isApplicationPeriodOpen(String courseId) {
        return courseInformation.startDate(courseId).isAfter(timeProvider.getCurrentLocalDateTime()
                .plusWeeks(applicationWindowDurationInWeeks));
    }

    /**
     * Creates a list of pending TA-applications, extended with a rating fetched from the TA-microservice.
     *
     * @param courseId  The course to fetch the applications for.
     * @param sorted    Whether the list should be sorted.
     * @param amount    Number of entries in the returned list, null indicates the full list.
     * @return a list of extended TeachingAssistantApplications.
     */
    public List<PendingTeachingAssistantApplicationResponseModel> getExtendedPendingApplications(String courseId,
                                                                                                 boolean sorted,
                                                                                                 Integer amount) {
        List<TeachingAssistantApplication> applications = findAllByCourseAndStatus(courseId, ApplicationStatus.PENDING);
        var extended = extendWithRating(applications);

        if (amount == null || amount > extended.size()) {
            amount = extended.size();
        }

        if (sorted) {
            Collections.sort(extended);
        }

        return extended.subList(0, amount);
    }
}
