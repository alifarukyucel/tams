package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Service
public class ApplicationService {

    @Autowired
    private transient ApplicationRepository applicationRepository;
    private final transient CourseInformation courseInformation;

    public ApplicationService(CourseInformation courseInformation) {
        this.courseInformation = courseInformation;
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
     * Deletes an application from the database, if more than 3 weeks before start of the course
     * @param application the application to withdraw
     */
    public boolean checkAndWithdraw(Application application) {
        LocalDate deadline = courseInformation.startDate(application.getCourseId()).minusWeeks(3);
        if(LocalDate.now().compareTo(deadline) < 0) {
            applicationRepository.delete(application);
            return true;
        }
        return false;
    }
}
