package nl.tudelft.sem.tams.authentication.repositories;

import nl.tudelft.sem.tams.authentication.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
}
