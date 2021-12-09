package nl.tudelft.sem.template.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;
import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.WorkedHours;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.repositories.WorkedHoursRepository;
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
    private transient WorkedHoursRepository hoursRepository;

    @Autowired
    private transient ContractRepository contractRepository;

    private Contract defaultContract;
    private WorkedHours defaultWorkedHours;

    @BeforeEach
    void setUp() {
        hoursRepository.deleteAll();
        contractRepository.deleteAll();

        defaultContract = Contract.builder()
            .courseId("CSE2310")
            .netId("PvdBerg")
            .maxHours(20)
            .build();

        defaultContract = contractRepository.save(defaultContract);

        defaultWorkedHours = WorkedHours.builder()
            .contract(defaultContract)
            .approved(false)
            .build();

        hoursRepository.save(defaultWorkedHours);

    }

    @Test
    void approveHoursExistingHours() {
        // pre-condition
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isFalse();

        // act
        hourService.approveHours(defaultWorkedHours.getId(), true);

        // assert
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isTrue();
    }

    @Test
    void unApproveApprovedExistingHours() {
        // arrange
        defaultWorkedHours.setApproved(true);
        hoursRepository.save(defaultWorkedHours);

        // act
        hourService.approveHours(defaultWorkedHours.getId(), false);

        // assert
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isTrue();
    }

    @Test
    void unApproveExistingHours() {
        // act
        hourService.approveHours(defaultWorkedHours.getId(), false);

        // assert
        assertThat(hoursRepository.findById(defaultWorkedHours.getId()).isEmpty()).isTrue();
    }

    @Test
    void approveNonExistingHours() {
        // precondition
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isFalse();

        // act
        ThrowingCallable action = () -> hourService.approveHours(UUID.randomUUID(), true);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isFalse();
    }

    @Test
    void approveNullHours() {
        // precondition
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isFalse();

        // act
        ThrowingCallable action = () -> hourService.approveHours(null, true);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
        assertThat(hoursRepository.getOne(defaultWorkedHours.getId()).isApproved()).isFalse();
    }

    @Test
    void getAssociatedContract() {
        // act
        Contract found = hourService.getAssociatedContract(defaultWorkedHours.getId());

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
        // act
        ThrowingCallable action = () -> hourService.getAssociatedContract(UUID.randomUUID());

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
    }
}