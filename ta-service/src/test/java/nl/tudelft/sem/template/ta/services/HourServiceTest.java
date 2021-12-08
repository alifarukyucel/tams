package nl.tudelft.sem.template.ta.services;

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

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


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
    private List<WorkedHours> workedHoursList;
    private List<Contract> contractList;

    @BeforeEach
    void setUp() {
        hoursRepository.deleteAll();
        contractRepository.deleteAll();
        contractList = new ArrayList<Contract>();
        workedHoursList = new ArrayList<WorkedHours>();

        defaultContract = Contract.builder()
            .courseId("CSE2310")
            .netId("PvdBerg")
            .maxHours(20)
            .build();
        contractList.add(defaultContract);
        defaultContract = contractRepository.save(defaultContract);

        Contract c1  = Contract.builder()
                .courseId("CSE2500")
                .netId("WinstijnSmit")
                .maxHours(40)
                .build();
        c1 = contractRepository.save(c1);
        contractList.add(c1);

        Contract c2 = Contract.builder()
                .courseId("CSE2310")
                .netId("WinstijnSmit")
                .maxHours(40)
                .build();
        c2 = contractRepository.save(c2);
        contractList.add(c2);

        defaultWorkedHours = WorkedHours.builder().contract(defaultContract).approved(false).build();
        hoursRepository.save(defaultWorkedHours);

        // Add more workedHours to the list used for testing.
        workedHoursList.add(WorkedHours.builder().contract(defaultContract).build());
        workedHoursList.add(WorkedHours.builder().contract(c1).build());
        workedHoursList.add(WorkedHours.builder().contract(c1).build());
        workedHoursList.add(WorkedHours.builder().contract(c1).approved(true).reviewed(true).build());
        workedHoursList.add(WorkedHours.builder().contract(c2).build());
        workedHoursList.add(WorkedHours.builder().contract(c2).approved(true).reviewed(true).build());
        for ( var elm : workedHoursList) {
            hoursRepository.save(elm);
        }

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

    @Test
    void getOpenHoursBy_nullId() {
        // Act
        ThrowingCallable action = () -> hourService.getNonReviewedHoursBy(null, null);

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action);
    }

    @Test
    void getOpenHoursBy_course() {
        // Act
        List<WorkedHours> result = hourService.getNonReviewedHoursBy("CSE2310", null);

        // Assert
        assertThat( result.contains(workedHoursList.get(0)) ).isTrue();
        assertThat( result.contains(workedHoursList.get(1)) ).isFalse();
        assertThat( result.contains(workedHoursList.get(2)) ).isFalse();
        assertThat( result.contains(workedHoursList.get(3)) ).isFalse();
        assertThat( result.contains(workedHoursList.get(4)) ).isTrue();
        assertThat( result.contains(workedHoursList.get(5)) ).isFalse();
    }

    @Test
    void getOpenHoursBy_courseAndNetId() {
        // Act
        List<WorkedHours> result = hourService.getNonReviewedHoursBy("CSE2500", "WinstijnSmit");

        // Assert
        assertThat( result.contains(workedHoursList.get(0)) ).isFalse();
        assertThat( result.contains(workedHoursList.get(1)) ).isTrue();
        assertThat( result.contains(workedHoursList.get(2)) ).isTrue();
        assertThat( result.contains(workedHoursList.get(3)) ).isFalse();
        assertThat( result.contains(workedHoursList.get(4)) ).isFalse();
        assertThat( result.contains(workedHoursList.get(5)) ).isFalse();
    }

}