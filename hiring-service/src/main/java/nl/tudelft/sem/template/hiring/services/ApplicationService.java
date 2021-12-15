package nl.tudelft.sem.template.hiring.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;


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
     * Gets all applications that belong to a specific user.
     *
     * @param netId the netId of the user to get applications from.
     * @return a list of all applications from the user.
     */
    public List<Application> getApplicationFromStudent(String netId) {
        List<Application> allApplications = applicationRepository.findAll();
        List<Application> result = new ArrayList<>();
        for (Application application : allApplications) {
            if (application.getNetId().equals(netId)) {
                result.add(application);
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
    public boolean maxApplication(String netId) {
        if (getApplicationFromStudent(netId).size() < 3) {
            return false;
        }
        return true;
    }
}
