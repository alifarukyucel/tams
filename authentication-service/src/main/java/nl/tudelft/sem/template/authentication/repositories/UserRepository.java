package nl.tudelft.sem.template.authentication.repositories;

import nl.tudelft.sem.template.authentication.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
}
