package nl.tudelft.sem.template.hiring.repository;

import javax.transaction.Transactional;
import nl.tudelft.sem.template.hiring.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    Application getApplication(int id);

    Application setApplication(Application application);

    void deleteApplication(int id);



}
