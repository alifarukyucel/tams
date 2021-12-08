package nl.tudelft.sem.template.ta.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.WorkedHours;
import nl.tudelft.sem.template.ta.repositories.WorkedHoursRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

/**
 * The HourService.
 * Which handles all the business logic of declaring hours worked by TA's
 */
@Service
public class HourService {

    private final transient WorkedHoursRepository hoursRepository;

    public HourService(WorkedHoursRepository hoursRepository) {
        this.hoursRepository = hoursRepository;
    }

    /**
     * Allow a lecturer to approve hours.
     * Approved hours cannot be unapproved.
     *
     * @param id The ID of the hours worked
     * @param status The status to set to the hours, false is ignored.
     * @throws NoSuchElementException Thrown if the worked hours could not be found.
     * @throws NullPointerException Thrown if no id was specified.
     */
    public void approveHours(UUID id, boolean status)
        throws NoSuchElementException, NullPointerException {

        if (id == null) {
            throw new NoSuchElementException("An Id must be specified");
        }

        var hours = hoursRepository.findById(id);
        if (hours.isEmpty()) {
            throw new NoSuchElementException("Specified hours do not exist");
        }

        WorkedHours workedHours = hours.get();

        if (status || workedHours.isApproved()) {
            workedHours.setApproved((true));
            hoursRepository.save(workedHours);
        } else {
            hoursRepository.delete(workedHours);
        }
    }

    /**
     * returns the contract associated with this hour UUID.
     *
     * @param uuid the id of the worked hours.
     * @return The contract.
     */
    public Contract getAssociatedContract(UUID uuid)
        throws NoSuchElementException, NullPointerException {

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
    public List<WorkedHours> getNonReviewedHoursBy(String courseId, String netId)  {
        if (courseId == null)
            throw new NoSuchElementException("The courseId should be specified");

        return netId == null ?
                hoursRepository.findNonReviewedHoursBy(courseId) :
                hoursRepository.findNonReviewedHoursBy(courseId, netId);
    }


}
