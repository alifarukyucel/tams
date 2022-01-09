package nl.tudelft.sem.tams.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.HourDeclaration;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteContractBuilder;
import nl.tudelft.sem.tams.ta.entities.builders.ConcreteHourDeclarationBuilder;
import nl.tudelft.sem.tams.ta.models.SubmitHoursRequestModel;
import nl.tudelft.sem.tams.ta.repositories.ContractRepository;
import nl.tudelft.sem.tams.ta.repositories.HourDeclarationRepository;
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
    //Arbitrary time to use for the hour declarations
    private static final LocalDateTime arbitraryTime = LocalDateTime.of(2000, 1, 1, 0, 0);

    @Autowired
    private transient HourService hourService;

    @Autowired
    private transient HourDeclarationRepository hoursRepository;

    @Autowired
    private transient ContractRepository contractRepository;

    private Contract defaultContract;
    private List<HourDeclaration> hourDeclarations;
    private List<Contract> contracts;
    private HourDeclaration defaultHourDeclaration;

    @BeforeEach
    void setUp() {
        hoursRepository.deleteAll();
        contractRepository.deleteAll();
        contracts = new ArrayList<Contract>();
        hourDeclarations = new ArrayList<HourDeclaration>();

        defaultContract = new ConcreteContractBuilder()
            .withCourseId("CSE2310")
            .withNetId("PvdBerg")
            .withSigned(true)
            .withMaxHours(20)
            .withSigned(true)
            .build();
        defaultContract = contractRepository.save(defaultContract);
        contracts.add(defaultContract);

        defaultHourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(5)
            .withApproved(false)
            .withReviewed(false)
            .build();
        defaultHourDeclaration = hoursRepository.save(defaultHourDeclaration);
        hourDeclarations.add(defaultHourDeclaration);

        setupContracts();
        setupHourDeclarations();
    }

    void setupContracts() {

        contracts.add(new ConcreteContractBuilder()
            .withCourseId("CSE2500")
            .withNetId("Maurits")
            .withMaxHours(40)
            .withSigned(true)
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withCourseId("CSE2310")
            .withNetId("WinstijnSmit")
            .withMaxHours(40)
            .withSigned(true)
            .build()
        );

        for (int i = 1; i < contracts.size(); i++) {
            contracts.set(i, contractRepository.save(contracts.get(i)));
        }
    }

    void setupHourDeclarations() {
        Contract c1 = contracts.get(1);
        Contract c2 = contracts.get(2);

        // Add more workedHours to the list used for testing.
        hourDeclarations.add(new ConcreteHourDeclarationBuilder()
                            .withWorkedTime(2).withContractId(c1).withApproved(true).withReviewed(true).build());
        hourDeclarations.add(new ConcreteHourDeclarationBuilder()
                            .withWorkedTime(7).withContractId(c1).withApproved(false).withReviewed(false).build());
        hourDeclarations.add(new ConcreteHourDeclarationBuilder()
                            .withWorkedTime(6).withContractId(c1).withApproved(true).withReviewed(true).build());

        hourDeclarations.add(new ConcreteHourDeclarationBuilder()
                            .withWorkedTime(3).withContractId(c2).withApproved(false).withReviewed(false).build());
        hourDeclarations.add(new ConcreteHourDeclarationBuilder()
                            .withWorkedTime(1).withContractId(c2).withApproved(true).withReviewed(true).build());

        for (int i = 1; i < hourDeclarations.size(); i++) {
            hourDeclarations.set(i, hoursRepository.save(hourDeclarations.get(i)));
        }
    }

    /**
     * Boundary test, making sure non-positive hours cannot be submitted.
     */
    @Test
    void blockSubmittingNonPositiveHours() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(0)
            .withApproved(false)
            .withReviewed(false)
            .build();

        //act
        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Boundary test, making sure positive hours can be submitted.
     */
    @Test
    void allowSubmittingPositiveHours() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(1)
            .withApproved(false)
            .withReviewed(false)
            .build();

        //act
        hourService.checkAndSave(hourDeclaration);

        // assert
        assertThat(hoursRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void submitHoursUnsignedContract() {
        //arrange
        defaultContract.setSigned(false);
        contractRepository.save(defaultContract);

        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(5)
            .withApproved(false)
            .withReviewed(false)
            .build();

        //act
        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void totalHoursApprovedTest() {
        // arrange
        // populate default contract.
        HourDeclaration hourDeclaration = hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(5)
            .withApproved(true)
            .withReviewed(true)
            .build());

        HourDeclaration hourDeclaration2 = hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(11)
            .withApproved(true)
            .withReviewed(true)
            .build());

        hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(3)
            .withApproved(false)
            .withReviewed(true)
            .build());

        hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(1)
            .withApproved(true)
            .withReviewed(false)
            .build());

        // act
        int totalTime = hourService.totalHoursApproved(defaultContract);

        // assert
        assertThat(hourDeclaration.getWorkedTime() + hourDeclaration2.getWorkedTime()).isEqualTo(
            totalTime);
    }

    @Test
    void findAllHours() {
        // arrange
        Contract contract = new ConcreteContractBuilder()
            .withCourseId("CSE2550")
            .withNetId("PvdBerg")
            .withSigned(true)
            .withMaxHours(20)
            .build();

        contract = contractRepository.save(contract);

        HourDeclaration hourDeclaration = hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(5)
            .withApproved(false)
            .withReviewed(false)
            .build());

        hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(contract)
            .withWorkedTime(5)
            .withApproved(false)
            .withReviewed(false)
            .build());

        // act
        var hours = hourService.findHoursOfContract(defaultContract);

        // assert
        assertThat(hours).containsExactlyInAnyOrder(defaultHourDeclaration, hourDeclaration);
    }

    @Test
    void createAndSave() {
        // arrange
        SubmitHoursRequestModel submitHoursRequestModel = SubmitHoursRequestModel.builder()
            .desc("hello")
            .workedTime(5)
            .date(arbitraryTime)
            .course(defaultContract.getCourseId())
            .build();

        HourDeclaration expected = new ConcreteHourDeclarationBuilder()
            .withWorkedTime(submitHoursRequestModel.getWorkedTime())
            .withReviewed(false)
            .withApproved(false)
            .withContractId(defaultContract)
            .withDate(submitHoursRequestModel.getDate())
            .withDescription(submitHoursRequestModel.getDesc())
            .build();

        // act
        HourDeclaration hourDeclaration = hourService.createAndSaveDeclaration(
            defaultContract.getNetId(), submitHoursRequestModel);

        // assert
        HourDeclaration actual = hoursRepository.findById(hourDeclaration.getId()).orElseThrow();
        assertThat(actual.getId()).isNotNull();

        actual.setId(null);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkAndSave() {
        // arrange
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withApproved(true)
            .withReviewed(true)
            .withWorkedTime(5)
            .withDescription("This is a test.")
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    /**
     * Boundary test for submitting hours.
     * on point.
     */
    @Test
    void submitMaxRemainingHoursPopulatedDatabase() {
        // arrange
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(contracts.get(1))
            .withWorkedTime(32)  // hardcoded from default contract c1. via setupContracts()
            .withApproved(false) // contract has 32 hours remaining
            .withReviewed(false)
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    /**
     * Boundary test for submitting hours.
     * off point.
     */
    @Test
    void submitMoreThanMaxRemainingHoursPopulatedDatabase() {
        // arrange
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(contracts.get(1))
            .withWorkedTime(33)  // hardcoded from default contract c1. via setupContracts()
            .withApproved(false) // contract has 32 hours remaining
            .withReviewed(false)
            .build();

        // act
        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(hourDeclarations.size());
    }

    /**
     * Boundary test.
     */
    @Test
    void submitHoursCloseToMax() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(defaultContract.getMaxHours() - 1)
            .withApproved(false)
            .withReviewed(false)
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    /**
     * Boundary test.
     */
    @Test
    void submitHoursEqualToMax() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(defaultContract.getMaxHours())
            .withApproved(false)
            .withReviewed(false)
            .build();

        // act
        hourDeclaration = hourService.checkAndSave(hourDeclaration);

        // assert
        var optionalFound = hoursRepository.findById(hourDeclaration.getId());

        assertThat(optionalFound.isPresent()).isTrue();
        assertThat(hourDeclaration).isEqualTo(optionalFound.get());
    }

    /**
     * Boundary test.
     */
    @Test
    void submitHoursOverMax() {
        //arrange
        hoursRepository.deleteAll();
        HourDeclaration hourDeclaration = new ConcreteHourDeclarationBuilder()
            .withContractId(defaultContract)
            .withWorkedTime(defaultContract.getMaxHours() + 1)
            .withApproved(false)
            .withReviewed(false)
            .build();

        ThrowingCallable action = () -> hourService.checkAndSave(hourDeclaration);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Boundary test for approving hours.
     * off point.
     */
    @Test
    void approveHoursOverContract() {
        // arrange

        HourDeclaration hourDeclaration = hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(contracts.get(1))
            .withWorkedTime(33)  // hardcoded from default contract c1. via setupContracts()
            .withApproved(false) // contract has 32 hours remaining
            .withReviewed(false)
            .build());

        // act
        ThrowingCallable action = () -> hourService.approveHours(hourDeclaration.getId(), true);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
        assertThat(hoursRepository.getOne(hourDeclaration.getId()).getApproved()).isFalse();
        assertThat(hoursRepository.getOne(hourDeclaration.getId()).getReviewed()).isFalse();
    }

    @Test
    void rejectHoursOverContract() {
        // arrange

        HourDeclaration hourDeclaration = hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(contracts.get(1))
            .withWorkedTime(33)  // hardcoded from default contract c1. via setupContracts()
            .withApproved(false) // contract has 32 hours remaining
            .withReviewed(false)
            .build());

        // act
        hourService.approveHours(hourDeclaration.getId(), false);

        // assert
        assertThat(hoursRepository.getOne(hourDeclaration.getId()).getApproved()).isFalse();
        assertThat(hoursRepository.getOne(hourDeclaration.getId()).getReviewed()).isTrue();
    }

    /**
     * Boundary test for approving hours.
     * on point.
     */
    @Test
    void approveHoursExactMaxContract() {
        // arrange
        HourDeclaration hourDeclaration = hoursRepository.save(new ConcreteHourDeclarationBuilder()
            .withContractId(contracts.get(1))
            .withWorkedTime(32)  // hardcoded from default contract c1. via setupContracts()
            .withApproved(false) // contract has 32 hours remaining
            .withReviewed(false)
            .build());

        // act
        hourService.approveHours(hourDeclaration.getId(), true);

        // assert
        assertThat(hoursRepository.getOne(hourDeclaration.getId()).getApproved()).isTrue();
        assertThat(hoursRepository.getOne(hourDeclaration.getId()).getReviewed()).isTrue();
    }

    @Test
    void approveHoursExistingHours() {
        // pre-condition
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isFalse();

        // act
        hourService.approveHours(defaultHourDeclaration.getId(), true);

        // assert
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getApproved()).isTrue();
        assertThat(hoursRepository.getOne(defaultHourDeclaration.getId()).getReviewed()).isTrue();
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

    @Test
    void getOpenHoursBy_null() {
        // Act
        ThrowingCallable action = () -> hourService
                                        .getNonReviewedHoursByCourseIdAndNetId(null, null);

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(action);
    }

    @Test
    void getOpenHoursBy_course() {
        // Act
        List<HourDeclaration> result = hourService
                                        .getNonReviewedHoursByCourseIdAndNetId("CSE2310", null);

        // Assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(hourDeclarations.get(0))).isTrue();
        assertThat(result.contains(hourDeclarations.get(5))).isFalse();
        assertThat(result.contains(hourDeclarations.get(4))).isTrue();
    }

    @Test
    void getOpenHoursBy_courseAndNetId_1() {
        // Act
        List<HourDeclaration> result = hourService
                                        .getNonReviewedHoursByCourseIdAndNetId(
                                            "CSE2500", "Maurits");

        // Assert
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(hourDeclarations.get(2))).isTrue();
    }

    @Test
    void getOpenHoursBy_courseAndNetId_2() {
        // Act
        List<HourDeclaration> result = hourService
                                        .getNonReviewedHoursByCourseIdAndNetId(
                                            "CSE2310", "WinstijnSmit");

        // Assert
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(hourDeclarations.get(4))).isTrue();
    }

    @Test
    void getOpenHoursBy_noResult() {
        // Act
        List<HourDeclaration> result1 = hourService
                                        .getNonReviewedHoursByCourseIdAndNetId(
                                            "CSE2500", "WinstijnSmit");
        List<HourDeclaration> result2 = hourService
                                        .getNonReviewedHoursByCourseIdAndNetId(
                                            "CSE3500", "");

        // Assert
        assertThat(result1.size()).isEqualTo(0);
        assertThat(result2.size()).isEqualTo(0);
    }


}