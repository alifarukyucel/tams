package nl.tudelft.sem.template.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;
import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.HourDeclaration;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.repositories.HourDeclarationRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional  // prevents lazy loading issues and will keep the connection open.
class HourServiceTest {

    @Autowired
    private transient HourService hourService;

    @Autowired
    private transient HourDeclarationRepository hoursRepository;

    @Autowired
    private transient ContractRepository contractRepository;

    private Contract defaultContract;
    private HourDeclaration defaultHourDeclaration;

    @BeforeEach
    void setUp() {
        hoursRepository.deleteAll();
        contractRepository.deleteAll();

        defaultContract = Contract.builder()
            .courseId("CSE2310")
            .netId("PvdBerg")
            .maxHours(20)
            .signed(true)
            .build();

        defaultContract = contractRepository.save(defaultContract);

        defaultHourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(5)
            .approved(false)
            .reviewed(false)
            .build();

        hoursRepository.save(defaultHourDeclaration);

    }

    @Test
    void blockSubmittingNegativeHours() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(-5)
            .approved(false)
            .reviewed(false)
            .build();

        //act
        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void submitHoursUnsignedContract() {
        //arrange
        defaultContract.setSigned(false);
        contractRepository.save(defaultContract);

        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(5)
            .approved(false)
            .reviewed(false)
            .build();

        //act
        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void findAllHours() {
        // arrange
        Contract contract = Contract.builder()
            .courseId("CSE2550")
            .netId("PvdBerg")
            .maxHours(20)
            .build();

        contract = contractRepository.save(contract);

        HourDeclaration hourDeclaration = hoursRepository.save(HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(5)
            .approved(false)
            .reviewed(false)
            .build());

        hoursRepository.save(HourDeclaration.builder()
            .contract(contract)
            .workedTime(5)
            .approved(false)
            .reviewed(false)
            .build());

        // act
        var hours = hourService.findHoursOfContract(defaultContract);

        // assert
        assertThat(hours).containsExactlyInAnyOrder(defaultHourDeclaration, hourDeclaration);
    }

    @Test
    void checkAndSave() {
        // arrange
        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .approved(true)
            .reviewed(true)
            .workedTime(5)
            .desc("This is a test.")
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    @Test
    void submitHoursCloseToMax() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(defaultContract.getMaxHours() - 1)
            .approved(false)
            .reviewed(false)
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    @Test
    void submitHoursEqualToMax() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(defaultContract.getMaxHours())
            .approved(false)
            .reviewed(false)
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    @Test
    void submitHoursOverMax() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(defaultContract.getMaxHours() + 1)
            .approved(false)
            .reviewed(false)
            .build();

        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void approveHoursExistingHours() {
        // pre-condition
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isFalse();

        // act
        hourService.approveHours(defaultHourDeclaration.getId(), true);

        // assert
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isTrue();
    }

    @Test
    void unApproveApprovedExistingHours() {
        // arrange
        defaultHourDeclaration.setApproved(true);
        defaultHourDeclaration.setReviewed(true);
        hoursRepository.save(defaultHourDeclaration);

        // act
        ThrowingCallable action = () -> hourService.approveHours(
            defaultHourDeclaration.getId(), false);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isTrue();
    }

    @Test
    void reApproveApprovedExistingHours() {
        // arrange
        defaultHourDeclaration.setApproved(true);
        defaultHourDeclaration.setReviewed(true);
        hoursRepository.save(defaultHourDeclaration);

        // act
        ThrowingCallable action = () -> hourService.approveHours(
            defaultHourDeclaration.getId(), true);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
    }

    @Test
    void unApproveExistingHours() {
        // act
        hourService.approveHours(defaultHourDeclaration.getId(), false);

        // assert
        var optionalWorkedHours = hoursRepository.findById(defaultHourDeclaration.getId());
        assertThat(optionalWorkedHours.isPresent()).isTrue();
        HourDeclaration hourDeclaration = optionalWorkedHours.get();
        assertThat(hourDeclaration.getReviewed()).isTrue();
        assertThat(hourDeclaration.getApproved()).isFalse();
    }

    @Test
    void unApproveApprovedHours() {
        // arrange
        defaultHourDeclaration.setApproved(true);
        defaultHourDeclaration.setReviewed(true);
        hoursRepository.save(defaultHourDeclaration);

        // act
        ThrowingCallable action = () ->
            hourService.approveHours(defaultHourDeclaration.getId(), false);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);

        var optionalWorkedHours = hoursRepository.findById(defaultHourDeclaration.getId());
        assertThat(optionalWorkedHours.isPresent()).isTrue();
        HourDeclaration hourDeclaration = optionalWorkedHours.get();

        assertThat(hourDeclaration.getReviewed()).isTrue();
        assertThat(hourDeclaration.getApproved()).isTrue();
    }

    @Test
    void approveNonExistingHours() {
        // precondition
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isFalse();
        UUID id = UUID.randomUUID();

        // act
        ThrowingCallable action = () -> hourService.approveHours(id, true);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isFalse();
    }

    @Test
    void approveNullHours() {
        // precondition
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isFalse();

        // act
        ThrowingCallable action = () -> hourService.approveHours(null, true);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isFalse();
    }

    @Test
    void getAssociatedContract() {
        // act
        Contract found = hourService.getAssociatedContract(defaultHourDeclaration.getId());

        // assert
        assertThat(found).isEqualTo(defaultContract);
    }

    @Test
    void getAssociatedContractNullId() {
        // act
        ThrowingCallable action = () -> hourService.getAssociatedContract(null);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
    }

    @Test
    void getAssociatedContractNonExistingHours() {
        // arrange
        UUID id = UUID.randomUUID();

        // act
        ThrowingCallable action = () -> hourService.getAssociatedContract(id);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
    }
}