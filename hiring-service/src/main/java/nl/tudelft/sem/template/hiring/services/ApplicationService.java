package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.Example;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * ApplicationService
 * Handles business logic of everthing a user wants to do regarding the application
 */

@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;

    public ApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
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

    public Application getApplication(String netId, String courseId) throws NoSuchElementException {
        if(courseId == null || netId == null){
             throw new NoSuchElementException("An application must have a netId and courseId");
        }
    }


    /**
     * Retrieving the status of the application to see whether someone is accepted or rejected.
     * @param application the application to retrieve the status from
     * @param applicationStatus the actual status of the application
     * @return String containing the status in readable format
     */
    public String retrieveStatus(Application application, ApplicationStatus applicationStatus) throws NoSuchElementException {
        if(applicationStatus.equals(ApplicationStatus.PENDING)) {
            application.setStatus(ApplicationStatus.PENDING);
            return "The selection procedure is still pending";
        }
        if(applicationStatus.equals(ApplicationStatus.REJECTED)){
            application.setStatus(ApplicationStatus.REJECTED);
            return "Unfortunately you have been rejected";
        }
        if(applicationStatus.equals(ApplicationStatus.ACCEPTED)) {
            application.setStatus(ApplicationStatus.ACCEPTED);
            return "Congratulations, you have been accepted";
        }
        else {
            throw new NoSuchElementException("There is no application ");
        }
    }
    
}
