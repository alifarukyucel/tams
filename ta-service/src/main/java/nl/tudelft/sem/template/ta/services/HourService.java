package nl.tudelft.sem.template.ta.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.repositories.HourDeclarationRepository;
import org.springframework.stereotype.Service;

/**
 * The HourService.
 * Which handles all the business logic of declaring hours worked by TA's
 */
@Service
public class HourService {

    private final transient HourDeclarationRepository hoursRepository;

    public HourService(HourDeclarationRepository hoursRepository) {
        this.hoursRepository = hoursRepository;
    }

    /**
     * Allow a lecturer to approve hours.
     * Approved hours cannot be unapproved.
     *
     * @param id The ID of the hours worked
     * @param status The status to set to the hours, false is ignored.
     * @throws NoSuchElementException Thrown if the worked hours could not be found.
     * @throws IllegalArgumentException Thrown is hours were already approved.
     */
    public void approveHours(UUID id, boolean status)
        throws NoSuchElementException, IllegalArgumentException {

        if (id == null) {
            throw new NoSuchElementException("An Id must be specified");
        }

        var hours = hoursRepository.findById(id);
        if (hours.isEmpty()) {
            throw new NoSuchElementException("Specified hours do not exist");
        }

        HourDeclaration hourDeclaration = hours.get();

        if (hourDeclaration.getReviewed()) {
            throw new IllegalArgumentException("Hours have already been approved");
        }

        if (status) {
            hourDeclaration.setApproved(true);
        }
        hourDeclaration.setReviewed(true);
        hoursRepository.save(hourDeclaration);
    }

    /**
     * Saves an hourDeclaration if it is a valid declaration.
     *
     * @param hourDeclaration The declaration to save.
     * @return Saved version of the declaration.
     * @throws IllegalArgumentException if declaration does not meet requirements.
     */
    public HourDeclaration checkAndSave(HourDeclaration hourDeclaration) {

        return hoursRepository.save(hourDeclaration);
    }

    /**
     * returns the contract associated with this hour UUID.
     *
     * @param uuid the id of the worked hours.
     * @return The contract.
     * @throws NoSuchElementException Thrown if the contract or worked hours do not exist.
     */
    public Contract getAssociatedContract(UUID uuid)
        throws NoSuchElementException {

        if (uuid == null) {
            throw new NoSuchElementException("An Id must be specified");
        }

        var workedHours = hoursRepository.findById(uuid);

        if (workedHours.isEmpty()) {
            throw new NoSuchElementException("Specified hours do not exist");
        }

        return workedHours.get().getContract();
    }


    /**
     * Find and return the worked hours that are still not accepted or rejected.
     *
     * @param courseId the courseId of the WorkedHours (required)
     * @param netId the netId of the WorkedHours (optional)
     * @return a list of workedHours with requested courseId (and netId if given)
     */
    public List<HourDeclaration> getNonReviewedHoursBy(String courseId, String netId)  {
        if (courseId == null) {
            throw new IllegalArgumentException("The courseId should be specified");
        }

        return netId == null
            ? hoursRepository.findNonReviewedHoursBy(courseId)
            : hoursRepository.findNonReviewedHoursBy(courseId, netId);
    }

}
