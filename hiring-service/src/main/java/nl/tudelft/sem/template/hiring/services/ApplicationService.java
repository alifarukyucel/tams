package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.Example;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositeKeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * ApplicationService
 * Handles business logic of everthing a user wants to do regarding the application
 */

@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;

//    public ApplicationRepository(ApplicationRepository applicationRepository) {
//        this.applicationRepository = applicationRepository;
//    }


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
     * Retrieving the status of the application to see whether someone is accepted or rejected.
     * @param key the key to find the application in the database.
     * @return String containing the status in readable format.
     * @throws NoSuchElementException
     */
//    public String retrieveStatus(ApplicationKey key) throws NoSuchElementException {
//        Optional<Application> application = applicationRepository.findById(key);
//        if(!application.isPresent()) throw new NoSuchElementException("Application does not exist");
//        Application actualApplication = application.get();
//        ApplicationStatus status = actualApplication.getStatus();
//        if(status == null) {
//            actualApplication.setStatus(ApplicationStatus.PENDING);
//            return "The selection procedure is still pending";
//        }
//        if(status.equals(ApplicationStatus.ACCEPTED)) {
//            return "Congratulations, you have been accepted";
//        }
//        if(status.equals(ApplicationStatus.REJECTED)){
//            return "Unfortunately you have been rejected";
//        }
//        else{
//            return "The selection procedure is still pending";
//        }
//    }

    public ApplicationStatus retrieveStatus(ApplicationKey key) throws NoSuchElementException {
        Optional<Application> application = applicationRepository.findById(key);
        if(!application.isPresent()) throw new NoSuchElementException("Application does not exist");
        Application actualApplication = application.get();
        ApplicationStatus status = actualApplication.getStatus();
        if(status == null) {
            actualApplication.setStatus(ApplicationStatus.PENDING);
        }
        return status;
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

}
