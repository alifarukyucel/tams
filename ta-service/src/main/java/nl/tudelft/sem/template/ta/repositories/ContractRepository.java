package nl.tudelft.sem.template.ta.repositories;

import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
}
