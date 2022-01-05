package nl.tudelft.sem.template.ta.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.builders.ConcreteContractBuilder;
import nl.tudelft.sem.template.ta.entities.compositekeys.ContractId;
import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.interfaces.EmailSender;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import nl.tudelft.sem.template.ta.services.communication.models.CourseInformationResponseModel;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
@ActiveProfiles({"test", "mockCourseInformation", "mockEmailSender"})
class ContractServiceTest {

    @Autowired
    private transient ContractService contractService;

    @Autowired
    private transient ContractRepository contractRepository;

    @Autowired
    private transient CourseInformation mockCourseInformation;

    @Autowired
    private transient EmailSender mockEmailSender;

    @Test
    void signExistingContract() {
        // arrange
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(true)
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withRating(8.2)
            .withDuties("Work really hard")
            .withSigned(false)
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
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
        contractRepository.save(new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2550")
            .withMaxHours(5)
            .withRating(9.53)
            .withDuties("Work really hard")
            .withSigned(false)
            .build());

        contractRepository.save(new ConcreteContractBuilder()
            .withNetId("GerryEik")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withRating(7)
            .withDuties("Work really hard")
            .withSigned(false)
            .build());

        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withRating(6)
            .withDuties("Work really hard")
            .withSigned(false)
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
        Contract contract1 = new ConcreteContractBuilder()
                .withNetId("PVeldHuis")
                .withCourseId("CSE2550")
                .withMaxHours(5)
                .withDuties("Work really hard")
                .withSigned(false)
                .build();
        contractRepository.save(contract1);

        contractRepository.save(new ConcreteContractBuilder()
                .withNetId("GerryEik")
                .withCourseId("CSE2310")
                .withMaxHours(5)
                .withDuties("Work really hard")
                .withSigned(false)
                .build());

        Contract contract2 = new ConcreteContractBuilder()
                .withNetId("PVeldHuis")
                .withCourseId("CSE2310")
                .withMaxHours(5)
                .withDuties("Work really hard")
                .withSigned(false)
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("Gert")
            .withCourseId("CSE2310")
            .withSigned(false)
            .withMaxHours(20)
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
        contractRepository.save(new ConcreteContractBuilder()
            .withNetId("Martin")
            .withCourseId("CSE1105")
            .withSigned(false)
            .withMaxHours(20)
            .withDuties("Heel hard werken")
            .build()
        );

        contractRepository.save(new ConcreteContractBuilder()
            .withNetId("Martin")
            .withCourseId("CSE2310")
            .withSigned(false)
            .withMaxHours(20)
            .withDuties("Heel hard werken")
            .build()
        );

        Contract contract = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withSigned(false)
            .withMaxHours(20)
            .withDuties("Heel hard werken")
            .build();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(21)
                .build());

        String testContactEmail = "winstijn@tudelft.nl";

        // Act
        Contract saved = contractService.createUnsignedContract(
                contract.getNetId(), contract.getCourseId(), contract.getMaxHours(), contract.getDuties(),
                testContactEmail);

        // Assert
        assertThat(contractRepository.findAll().size()).isEqualTo(3);
        assertThat(contractRepository.getOne(new ContractId("WinstijnSmit", "CSE2310")))
            .isEqualTo(saved);

        verify(mockEmailSender).sendEmail(testContactEmail,
                "You have been offered a TA position for CSE2310",
                "Hi WinstijnSmit,\n\n"
                        + "The course staff of CSE2310 is offering you a TA position. Congratulations!\n"
                        + "Your duties are \"Heel hard werken\", and the maximum number of hours is 20.\n"
                        + "Please log into TAMS to review and sign the contract.\n\n"
                        + "Best regards,\nThe programme administration of your faculty");
        verifyNoMoreInteractions(mockEmailSender);
    }

    @Test
    void createUnsignedContractExceedingTaLimit() {
        contractRepository.save(new ConcreteContractBuilder()
            .withNetId("Martin")
            .withCourseId("CSE2310")
            .withSigned(false)
            .withMaxHours(20)
            .withDuties("Heel hard werken")
            .build()
        );

        Contract contract = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withSigned(false)
            .withMaxHours(20)
            .withDuties("Heel hard werken")
            .build();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(CourseInformationResponseModel.builder()
                .id("CSE2310")
                .description("Very cool course")
                .numberOfStudents(20)
                .build());


        // Act
        ThrowingCallable c = () -> contractService.createUnsignedContract(
                contract.getNetId(), contract.getCourseId(), contract.getMaxHours(), contract.getDuties());

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        assertThat(contractRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void createUnsignedContractInaccessibleCourseService() {
        Contract contract = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withSigned(false)
            .withMaxHours(20)
            .withDuties("Heel hard werken")
            .build();

        when(mockCourseInformation.getCourseById("CSE2310")).thenReturn(null);


        // Act
        ThrowingCallable c = () -> contractService.createUnsignedContract(
                contract.getNetId(), contract.getCourseId(), contract.getMaxHours(), contract.getDuties());

        // Assert
        assertThatIllegalArgumentException()
                .isThrownBy(c);

        assertThat(contractRepository.findAll().size()).isZero();
    }

    @Test
    void createUnsignedContract_illegalArguments() {
        // Arrange
        when(mockCourseInformation.getCourseById("CSE2525")).thenReturn(CourseInformationResponseModel.builder()
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2525")
            .withMaxHours(10)
            .withDuties("Duties")
            .withSigned(false)
            .build();
        contractRepository.save(contract);

        when(mockCourseInformation.getCourseById("CSE2525")).thenReturn(CourseInformationResponseModel.builder()
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
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
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
        Contract c1 = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CS2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
            .build();
        contractRepository.save(c1);
        Contract c2 = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2300")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false)
            .build();
        contractRepository.save(c2);

        // Act
        boolean exists = contractService.contractExists("WinstijnSmit", "CSE2310");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void rate() {
        // Arrange
        Contract contract = new ConcreteContractBuilder()
            .withNetId("PVeldHuis")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withRating(5)
            .withDuties("Work really hard")
            .withSigned(false)
            .build();
        contract = contractRepository.save(contract);

        // Act
        contractService.rate(contract.getNetId(), contract.getCourseId(), 9.67);

        // Assert
        Contract fetchedContract = contractRepository.getOne(
            new ContractId(contract.getNetId(), contract.getCourseId()));
        assertThat(fetchedContract.getRating()).isEqualTo(9.67);
    }

    @Test
    void rate_contractNotExists() {
        // Arrange
        Contract contract = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withRating(5)
            .withDuties("Work really hard")
            .withSigned(false)
            .build();
        contract = contractRepository.save(contract);

        // Act
        ThrowingCallable actionNotFound = () ->
            contractService.rate("SteveJobs", "CSE2525", 8);

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(actionNotFound);
    }

    @Test
    void rate_ratingInvalid() {
        // Arrange
        Contract contract = new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withRating(5)
            .withDuties("Work really hard")
            .withSigned(false)
            .build();
        contract = contractRepository.save(contract);

        // Act
        ThrowingCallable actionBelowZero = () ->
            contractService.rate("WinstijnSmit", "CSE2310", -1);
        ThrowingCallable actionAboveTen = () ->
            contractService.rate("WinstijnSmit", "CSE2310", 10.1);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionBelowZero);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionAboveTen);
        Contract fetchedContract = contractRepository.getOne(
            new ContractId(contract.getNetId(), contract.getCourseId()));
        assertThat(fetchedContract.getRating()).isEqualTo(5);
    }

    @Test
    void getAverageRatingOfNetIds_empty() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of();

        // Act
        ThrowingCallable actionNull = () ->
            contractService.getAverageRatingOfNetIds(null);
        ThrowingCallable actionEmpty = () ->
            contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionNull);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(actionEmpty);
    }

    @Test
    void getAverageRatingOfNetIds_nonExisting() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of("Stefan", "Elon");

        // Act
        var query = contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(2);
        assertThat(query.get("Stefan")).isEqualTo(-1);
        assertThat(query.get("Elon")).isEqualTo(-1);
    }

    @Test
    void getAverageRatingOfNetIds_oneNetId() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of("WinstijnSmit");

        // Act
        var query = contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(1);
        assertThat(query.get("WinstijnSmit")).isEqualTo(6.5);
    }

    @Test
    void getAverageRatingOfNetIds_oneNetId_2() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of("Maurits");

        // Act
        var query = contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(1);
        assertThat(query.get("Maurits")).isEqualTo(7);
    }

    @Test
    void getAverageRatingOfNetIds_nonExistingAlike() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of("Maurit", "maurits", "Mauritss", "mauritS", "Maurits");

        // Act
        var query = contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(5);
        assertThat(query.get("Maurit")).isEqualTo(-1);
        assertThat(query.get("maurits")).isEqualTo(-1);
        assertThat(query.get("Mauritss")).isEqualTo(-1);
        assertThat(query.get("mauritS")).isEqualTo(-1);
        assertThat(query.get("Maurits")).isEqualTo(7);
    }

    @Test
    void getAverageRatingOfNetIds_NetIdsandNonExisting() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of("WinstijnSmit", "ElonMusk", "Maurit", "Maurits");

        // Act
        var query = contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(4);
        assertThat(query.get("WinstijnSmit")).isEqualTo(6.5);
        assertThat(query.get("ElonMusk")).isEqualTo(-1);
        assertThat(query.get("Maurit")).isEqualTo(-1);
        assertThat(query.get("Maurits")).isEqualTo(7);
    }

    @Test
    void getAverageRatingOfNetIds_twoNetIds() {
        // Arrange
        prepareForAverageRatingTest();
        Collection<String> netIds = List.of("Maurits", "WinstijnSmit");

        // Act
        var query = contractService.getAverageRatingOfNetIds(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(2);
        assertThat(query.get("Maurits")).isEqualTo(7);
        assertThat(query.get("WinstijnSmit")).isEqualTo(6.5);
    }

    private void prepareForAverageRatingTest() {
        List<Contract> contracts = new ArrayList<>();

        // Correct Averages.
        // WinstijnSmit: 6.5
        // Maurits: 7

        contracts.add(new ConcreteContractBuilder()
            .withNetId("Maurits")
            .withCourseId("CSE2310")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(true)
            .withRating(7)
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withNetId("Maurits")
            .withCourseId("CSE1210")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(false) // not signed should not be included.
            .withRating(10)
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withNetId("Maurits")
            .withCourseId("CSE1210")
            .withMaxHours(5)
            .withDuties("Work really hard")
            .withSigned(true) // not signed should not be included.
            // .withRating(10) Rating has not been set!
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE2310")
            .withMaxHours(10)
            .withDuties("Work really hard")
            .withRating(8)
            .withSigned(true)
            .build()
        );

        contracts.add(new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE1250")
            .withMaxHours(2)
            .withDuties("No need to work hard")
            .withRating(9)
            .withSigned(false)  // not signed should not be included.
            .build());

        contracts.add(new ConcreteContractBuilder()
            .withNetId("WinstijnSmit")
            .withCourseId("CSE3200")
            .withMaxHours(2)
            .withDuties("Do very difficult stuff")
            .withRating(5)
            .withSigned(true)
            .build()
        );

        // Save them all to the database
        contractRepository.saveAll(contracts);
    }

}