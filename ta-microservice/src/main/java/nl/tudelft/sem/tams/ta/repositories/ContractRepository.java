package nl.tudelft.sem.tams.ta.repositories;

import java.util.Collection;
import java.util.List;
import nl.tudelft.sem.tams.ta.entities.Contract;
import nl.tudelft.sem.tams.ta.entities.compositekeys.ContractId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, ContractId> {


    /**
     * Queries and aggregates the rating into an average for a list of netIds.
     * It queries for all contracts that are signed and have a rating.
     * Then these will be aggregated and the average of the rating is computed.
     * netIds that are not found will simple not be in the return list.
     *
     * @param netIds a list of netIds to retrieve the average rating for.
     * @return List of tuples containing the netId and average rating.
     */
    @Query("SELECT c.netId, AVG(c.rating) FROM Contract c "
        + "WHERE c.netId IN :netIds AND c.rating > 0 AND c.signed = true "
        + "GROUP BY c.netId")
    List<Object[]> queryAverageRatingOfNetIds(
        @Param("netIds") Collection<String> netIds
    );

}
