package nl.tudelft.sem.template.hiring.services;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
        LocalDate deadline = courseInformation.startDate(courseId).minusWeeks(3);
        if (LocalDate.now().compareTo(deadline) < 0) {

            applicationRepository.delete(this.get(courseId, netId));
            return true;
        }
        return false;
    }
}
