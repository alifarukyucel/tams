package nl.tudelft.sem.template.ta.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.builders.ConcreteContractBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class ContractRepositoryTests {

    @Autowired
    private ContractRepository contractRepository;

    private List<Contract> contracts;

    // Manually computed averages of the two people in our test databases.
    // Need to be changed if the test data suite is changed in "prepare".
    private final double winstijnAverage = 6.5;
    private final double mauritsAverage = 7;

    @BeforeEach
    void prepare() {
        contracts = new ArrayList<Contract>();

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

    // TEST: Get average of an empty collection
    @Test
    void queryAverageRatingOfNetIds_empty() {
        // Arrange
        Collection<String> netIds = List.of();

        // Act
        var nullQuery = queryAndParse(null);
        var emptyQuery = queryAndParse(netIds);

        // Assert
        assertThat(nullQuery.keySet().isEmpty()).isTrue();
        assertThat(emptyQuery.keySet().isEmpty()).isTrue();

        // Confirm the reason we are not able to find anything
        // is not because of the database is empty.
        assertThat(contractRepository.findAll().size()).isGreaterThan(0);
    }

    // TEST: Get average of non-existing netIds.
    @Test
    void queryAverageRatingOfNetIds_nonExisting() {
        // Arrange
        Collection<String> netIds = List.of("Stefan", "Elon");

        // Act
        var emptyQuery = queryAndParse(netIds);

        // Assert
        assertThat(emptyQuery.keySet().isEmpty()).isTrue();

        // Confirm the reason we are not able to find anything
        // is not because of the database is empty.
        assertThat(contractRepository.findAll().size()).isGreaterThan(0);
    }

    // TEST: Get average of only WinstijnSmit
    @Test
    void queryAverageRatingOfNetIds_oneNetId() {
        // Arrange
        Collection<String> netIds = List.of("WinstijnSmit");

        // Act
        var query = queryAndParse(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(1);
        assertThat(query.get("WinstijnSmit")).isEqualTo(winstijnAverage);
    }

    // Get average of only Maurits
    @Test
    void queryAverageRatingOfNetIds_oneNetId_2() {
        // Arrange
        Collection<String> netIds = List.of("Maurits");

        // Act
        var query = queryAndParse(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(1);
        assertThat(query.get("Maurits")).isEqualTo(mauritsAverage);
    }

    // TEST: Get average of existing and non-existing netIds.
    @Test
    void queryAverageRatingOfNetIds_oneNetIdandNonExisting() {
        // Arrange
        Collection<String> netIds = List.of("WinstijnSmit", "ElonMusk");

        // Act
        var query = queryAndParse(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(1);
        assertThat(query.get("WinstijnSmit")).isEqualTo(winstijnAverage);
    }

    // TEST: Get average of both.
    @Test
    void queryAverageRatingOfNetIds_twoNetIds() {
        // Arrange
        Collection<String> netIds = List.of("Maurits", "WinstijnSmit");

        // Act
        var query = queryAndParse(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(2);
        assertThat(query.get("Maurits")).isEqualTo(mauritsAverage);
        assertThat(query.get("WinstijnSmit")).isEqualTo(winstijnAverage);
    }

    @Test
    void getAverageRatingOfNetIds_nonExistingAlike() {
        // Arrange
        Collection<String> netIds = List.of("winstijnSmit", "WinstijnSmitt", "winstijnsmit", "winstijn", "smit",
                                            "Maurit", "Mauritss", "maurits", "mAURITS");

        // Act
        var query = queryAndParse(netIds);

        // Assert
        assertThat(query.keySet().size()).isEqualTo(0);

        // Confirm the reason we are not able to find anything
        // is not because of the database is empty.
        assertThat(contractRepository.findAll().size()).isGreaterThan(0);
    }

    /**
     * Helper method to parse the object list returned.
     * Note: should only be used in the act stage of a test.
     *
     * @param netIds list of netIds
     * @return map of netIds with their respective average rating
     */
    private Map<String, Double> queryAndParse(Collection<String> netIds) {
        Map<String, Double> result = new HashMap<>();
        List<Object[]> queryResult = contractRepository.queryAverageRatingOfNetIds(netIds);
        for (Object[] data : queryResult) {
            String netId = (String) data[0];
            Double rating = (Double) data[1];
            result.put(netId, rating);
        }
        return result;
    }

}
