package nl.tudelft.sem.template.ta.services;

import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.repositories.ContractRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional  // prevents lazy loading issues and will keep the connection open.
class ContractServiceTest {

    @Autowired
    private transient ContractService contractService;

    @Autowired
    private transient ContractRepository contractRepository;

    @BeforeEach
    void setUp() {
        contractRepository.deleteAll();
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
        ThrowingCallable action_different_course = () -> contractService.getContract(contract1.getNetId(), null);
        ThrowingCallable action_different_netId  = () -> contractService.getContract(null, contract1.getCourseId());

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action_different_course);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action_different_netId);
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
        ThrowingCallable action_different_course = () -> contractService.getContract(contract1.getNetId(), "CSE2550");
        ThrowingCallable action_different_netId  = () -> contractService.getContract("GerryEiko", contract1.getCourseId());
        Contract foundContract = contractService.getContract(contract.getNetId(), contract.getCourseId());

        // assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action_different_course);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(action_different_netId);
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
        Contract foundContract = contractService.getContract(contract.getNetId(), contract.getCourseId());

        // assert
        assertThat(foundContract).isEqualTo(contract);
    }

    @Test
    void save() {
        // arrange
        Contract contract = Contract.builder().netId("Gert").courseId("CSE2310").build();

        // act
        contract = contractService.save(contract);

        // assert
        Contract expected = contractRepository.getOne(contract.getId());
        assertThat(contract.getId()).isNotNull();
        assertThat(contract).isEqualTo(expected);
    }
}