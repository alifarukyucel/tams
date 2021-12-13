package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public boolean maxApplication(List<Application> applications) {
        if(applications.size() < 3) return false;
        return true;
    }
}
