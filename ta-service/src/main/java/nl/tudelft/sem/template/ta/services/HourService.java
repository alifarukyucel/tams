package nl.tudelft.sem.template.ta.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.models.SubmitHoursRequestModel;
import nl.tudelft.sem.template.ta.repositories.HourDeclarationRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

/**
 * The HourService.
 * Which handles all the business logic of declaring hours worked by TA's
 */
@Service
public class HourService {

    private final transient HourDeclarationRepository hoursRepository;
    private final transient ContractService contractService;

    public HourService(HourDeclarationRepository hoursRepository, ContractService contractService) {
        this.hoursRepository = hoursRepository;
        this.contractService = contractService;
    }

    /**
     * Automatically create a new hourDeclaration and save it to the database if it is valid.
     *
     * @param netId     The netId of the user declaring the hours.
     * @param request   The SubmitHoursRequestModel.
     * @return The saved HourDeclaration object.
     * @throws NoSuchElementException if the required contract does not exist.
     */
    public HourDeclaration createAndSaveDeclaration(String netId, SubmitHoursRequestModel request)
        throws NoSuchElementException {
        
        Contract contract = contractService.getContract(
            netId, request.getCourse());

        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .workedTime(request.getWorkedTime())
            .reviewed(false)
            .approved(false)
            .contract(contract)
            .date(request.getDate())
            .desc(request.getDesc())
            .build();

        return checkAndSave(hourDeclaration);
    }

    /**
     * Allow a lecturer to approve declarations.
     * Approved declarations cannot be unapproved.
     *
     * @param id The ID of the hour declaration
     * @param status The status to set to the hours, false is ignored.
     * @throws NoSuchElementException Thrown if the hour declaration could not be found.
     * @throws IllegalArgumentException Thrown is hours were already approved or if going over budget.
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

        if (hourDeclaration.getWorkedTime() + totalHoursApproved(hourDeclaration.getContract())
            > hourDeclaration.getContract().getMaxHours()) {
            throw new IllegalArgumentException("Contract does not have enough hours left");
        }

        if (status) {
            hourDeclaration.setApproved(true);
        }
        hourDeclaration.setReviewed(true);
        hoursRepository.save(hourDeclaration);
    }

    /**
     * Saves an hourDeclaration if it is a valid declaration.
     * An invalid declaration occurs when submitting more hours than allowed,
     * or submitting to an unsigned contract.
     *
     * @param hourDeclaration The declaration to save.
     * @return Saved version of the declaration.
     * @throws IllegalArgumentException if declaration does not meet requirements.
     */
    public HourDeclaration checkAndSave(HourDeclaration hourDeclaration)
        throws IllegalArgumentException {
        if (hourDeclaration.getWorkedTime() < 0) {
            throw new IllegalArgumentException("Worked time cannot be negative.");
        }
        if (!hourDeclaration.getContract().getSigned()) {
            throw new IllegalArgumentException("Contract has not been signed by student.");
        }

        int totalWorkedTime = totalHoursApproved(hourDeclaration.getContract());

        if (totalWorkedTime + hourDeclaration.getWorkedTime()
            > hourDeclaration.getContract().getMaxHours()) {
            throw new IllegalArgumentException("Contract does not have enough hours remaining.");
        }

        return hoursRepository.save(hourDeclaration);
    }

    /**
     * Returns the total hours approved on this contract.
     *
     * @param contract contract to find the hours declared of.
     * @return Total hours approved on this contract.
     */
    public int totalHoursApproved(Contract contract) {
        List<HourDeclaration> hourDeclarations = findHoursOfContract(contract);

        return hourDeclarations
            .stream()
            .filter(o -> o.getReviewed() && o.getApproved())
            .mapToInt(HourDeclaration::getWorkedTime)
            .sum();
    }

    /**
     * Returns all hourDeclarations belonging to the passed contract.
     *
     * @param contract The contract whose declarations to fetch.
     * @return A list of hour declarations.
     */
    public List<HourDeclaration> findHoursOfContract(Contract contract) {
        ExampleMatcher ignoreAllFields = ExampleMatcher.matchingAll()
            .withIgnoreNullValues();

        Example<HourDeclaration> example = Example.of(
            HourDeclaration.builder()
                .contract(contract)
                .build(),
            ignoreAllFields);

        return hoursRepository.findAll(example);
    }

    /**
     * returns the contract associated with this hour UUID.
     *
     * @param uuid the id of the hour declaration.
     * @return The contract.
     * @throws NoSuchElementException Thrown if the contract or hour declaration do not exist.
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
    public List<HourDeclaration> getNonReviewedHoursByCourseIdAndNetId(
        String courseId, String netId)  {

        if (courseId == null) {
            throw new IllegalArgumentException("The courseId should be specified");
        }

        return netId == null
            ? hoursRepository.findNonReviewedHoursByCourseId(courseId)
            : hoursRepository.findNonReviewedHoursByCourseIdAndNetId(courseId, netId);
    }

}
