package nl.tudelft.sem.template.ta.repositories;

import nl.tudelft.sem.template.ta.entities.Contract;
import nl.tudelft.sem.template.ta.entities.compositekeys.ContractId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, ContractId> {
}
