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
        defaultContract = Contract.builder()
            .courseId("CSE2310")
            .netId("PvdBerg")
            .signed(true)
            .maxHours(20)
            .build();

        defaultContract = contractRepository.save(defaultContract);

        defaultHourDeclaration = HourDeclaration.builder()
            .contract(defaultContract)
            .workedTime(0)
            .approved(false)
            .reviewed(false)
            .build();

        hoursRepository.save(defaultHourDeclaration);

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