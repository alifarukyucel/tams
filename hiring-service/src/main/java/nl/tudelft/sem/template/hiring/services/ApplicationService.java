package nl.tudelft.sem.template.hiring.services;

import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.interfaces.ContractInformation;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import nl.tudelft.sem.template.hiring.services.communication.models.CreateContractRequestModel;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final transient ApplicationRepository applicationRepository;

    private final transient ContractInformation contractInformation;

    public ApplicationService(ApplicationRepository applicationRepository, ContractInformation contractInformation) {
        this.applicationRepository = applicationRepository;
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
                .courseId(courseId)
                .netId(netId)
                .duties(duties)
                .maxHours(maxHours)
                .build());

        if (!result) {
            // contract creation failed
            throw new IllegalArgumentException();
        }

        application.setStatus(ApplicationStatus.ACCEPTED);

        applicationRepository.save(application);
    }
}
