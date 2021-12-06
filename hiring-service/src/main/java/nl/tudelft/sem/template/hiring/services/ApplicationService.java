package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;



    public boolean checkAndSave(Application application) {
        if (application.getGrade() >= 6.0) {
            applicationRepository.save(application);
            return true;
        } else {
            return false;
        }
    }
}
