package nl.tudelft.sem.template.ta.services;

import java.util.NoSuchElementException;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.models.SubmitHoursRequestModel;
import nl.tudelft.sem.template.ta.repositories.HourDeclarationRepository;
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
     * returns the contract associated with this declaration UUID.
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


}
