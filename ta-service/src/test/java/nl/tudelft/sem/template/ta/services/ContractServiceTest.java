package nl.tudelft.sem.template.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.compositekeys.ContractId;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.services.communication.models.CourseInformationResponseModel;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional  // prevents lazy loading issues and will keep the connection open.
@ActiveProfiles({"test", "mockCourseInformation"})
class ContractServiceTest {

    @Autowired
    private transient ContractService contractService;

    @Autowired
    private transient ContractRepository contractRepository;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    @Test
    void signExistingContract() {
        // arrange
        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contract = contractRepository.save(contract);

        // act
        contractService.sign(contract.getNetId(), contract.getCourseId());

        // assert
        Contract fetchedContract = contractRepository.getOne(
            new ContractId(contract.getNetId(), contract.getCourseId()));
        assertThat(fetchedContract.getSigned()).isTrue();
    }

    @Test
    void signNonExistingContract() {
        // arrange
        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contract = contractRepository.save(contract);
        final Contract contract1 = contract;

        ThrowingCallable signNonExisting = () ->
            contractService.sign("GerryEik", contract1.getCourseId());

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(signNonExisting);
    }

    @Test
    void signAlreadySignedContract() {
        // arrange
        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(true)
            .build();
        contract = contractRepository.save(contract);
        final Contract contract1 = contract;

        ThrowingCallable signSigned = () ->
            contractService.sign(contract1.getNetId(), contract1.getCourseId());

        // assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(signSigned);
    }

    @Test
    void getContractSupplementingNullValues() {
        // arrange
        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contract = contractRepository.save(contract);
        final Contract contract1 = contract;

        // act
        ThrowingCallable actionDifferentCourse = () ->
            contractService.getContract(contract1.getNetId(), null);
        ThrowingCallable actionDifferentNetId  = () ->
            contractService.getContract(null, contract1.getCourseId());

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionDifferentCourse);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionDifferentNetId);
    }

    @Test
    void getNonExistingContract() {
        // arrange
        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contract = contractRepository.save(contract);
        final Contract contract1 = contract;

        // act
        ThrowingCallable actionDifferentCourse = () ->
            contractService.getContract(contract1.getNetId(), "CSE2550");
        ThrowingCallable actionDifferentNetId  = () ->
            contractService.getContract("GerryEiko", contract1.getCourseId());

        Contract foundContract = contractService
            .getContract(contract.getNetId(), contract.getCourseId());

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionDifferentCourse);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionDifferentNetId);
        assertThat(foundContract).isEqualTo(contract);
    }

    @Test
    void getContract() {
        // arrange
        contractRepository.save(Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2550")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build());

        contractRepository.save(Contract.builder()
            .netId("GerryEik")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build());

        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contract = contractRepository.save(contract);

        // act
        Contract foundContract = contractService
            .getContract(contract.getNetId(), contract.getCourseId());

        // assert
        assertThat(foundContract).isEqualTo(contract);
    }

    @Test
    void getAllContracts() {
        // Arrange
        Contract contract1 = Contract.builder()
                .netId("PVeldHuis")
                .courseId("CSE2550")
                .maxHours(5)
                .duties("Work really hard")
                .signed(false)
                .build();
        contractRepository.save(contract1);

        contractRepository.save(Contract.builder()
                .netId("GerryEik")
                .courseId("CSE2310")
                .maxHours(5)
                .duties("Work really hard")
                .signed(false)
                .build());

        Contract contract2 = Contract.builder()
                .netId("PVeldHuis")
                .courseId("CSE2310")
                .maxHours(5)
                .duties("Work really hard")
                .signed(false)
                .build();
        contractRepository.save(contract2);

        // Act
        List<Contract> contracts = contractService.getContractsBy("PVeldHuis");

        // Assert
        assertThat(contracts.size() == 2).isTrue();
        assertThat(contracts.contains(contract1)).isTrue();
        assertThat(contracts.contains(contract2)).isTrue();
        System.out.print((contracts));
    }

    @Test
    void getNonExistingContracts() {
        // Act
        ThrowingCallable actionNull = () -> contractService.getContractsBy(null);
        ThrowingCallable actionEmpty  = () -> contractService.getContractsBy("winstijnsmit");

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionNull);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionEmpty);
    }


    @Test
    void save() {
        // arrange
        Contract contract = Contract.builder()
            .netId("Gert")
            .courseId("CSE2310")
            .signed(false)
            .maxHours(20)
            .build();

        // act
        contract = contractService.save(contract);

        // assert
        Contract expected = contractRepository.getOne(
            new ContractId(contract.getNetId(), contract.getCourseId()));
        assertThat(contract).isEqualTo(expected);
    }

    @Test
    void createUnsignedContract() {
        // Arrange
        contractRepository.save(Contract.builder()
            .netId("Martin")
            .courseId("CSE1105")
            .signed(false)
            .maxHours(20)
            .duties("Heel hard werken")
            .build()
        );

        contractRepository.save(Contract.builder()
            .netId("Martin")
            .courseId("CSE2310")
            .signed(false)
            .maxHours(20)
            .duties("Heel hard werken")
            .build()
        );

        Contract contract = Contract.builder()
            .netId("WinstijnSmit")
            .courseId("CSE2310")
            .signed(false)
            .maxHours(20)
            .duties("Heel hard werken")
            .build();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(21)
                .build());


        // Act
        Contract saved = contractService.createUnsignedContract(
                contract.getNetId(), contract.getCourseId(), contract.getMaxHours(), contract.getDuties());

        // Assert
        assertThat(contractRepository.findAll().size()).isEqualTo(3);
        assertThat(contractRepository.getOne(new ContractId("WinstijnSmit", "CSE2310")))
            .isEqualTo(saved);
    }

    @Test
    void createUnsignedContract_illegalArguments() {
        // Arrange
        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2525")
                .description("Very cool course")
                .numberOfStudents(10000)
                .build());

        // Act
        ThrowingCallable actionNegativeMaxHours = () ->
            contractService.createUnsignedContract("WinstijnSmit", "CSE2525", -1, "Duties");
        ThrowingCallable actionCourseNull = () ->
            contractService.createUnsignedContract("WinstijnSmit", null, 10, "Duties");
        ThrowingCallable actionCourseEmpty = () ->
            contractService.createUnsignedContract("WinstijnSmit", "", 10, "Duties");
        ThrowingCallable actionNetIdNull = () ->
            contractService.createUnsignedContract(null, "", 10, "Duties");
        ThrowingCallable actionNetIdEmpty = () ->
            contractService.createUnsignedContract("", "CSE2525", 10, "Duties");

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionNegativeMaxHours);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionCourseNull);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionCourseEmpty);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionNetIdNull);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionNetIdEmpty);
        assertThat(contractRepository.findAll().size()).isEqualTo(0);
    }


    @Test
    void createUnsignedContract_createSame() {
        // Arrange
        Contract contract = Contract.builder()
            .netId("WinstijnSmit")
            .courseId("CSE2525")
            .maxHours(10)
            .duties("Duties")
            .signed(false)
            .build();
        contractRepository.save(contract);

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2525")
                .description("Very cool course")
                .numberOfStudents(10000)
                .build());

        // Act
        ThrowingCallable actionConflict = () ->
            contractService.createUnsignedContract("WinstijnSmit", "CSE2525", 5, "Duties");

        // There should be an error because there is a conflict.
        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionConflict);
        assertThat(contractRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void contractExists_true() {
        // Arrange
        Contract contract = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CSE2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contract = contractRepository.save(contract);

        // Act
        boolean exists = contractService.contractExists("PVeldHuis", "CSE2310");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void contractExists_false() {
        // Arrange
        Contract c1 = Contract.builder()
            .netId("PVeldHuis")
            .courseId("CS2310")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contractRepository.save(c1);
        Contract c2 = Contract.builder()
            .netId("WinstijnSmit")
            .courseId("CSE2300")
            .maxHours(5)
            .duties("Work really hard")
            .signed(false)
            .build();
        contractRepository.save(c2);

        // Act
        boolean exists = contractService.contractExists("WinstijnSmit", "CSE2310");

        // Assert
        assertThat(exists).isFalse();
    }
}