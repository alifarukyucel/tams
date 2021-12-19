package nl.tudelft.sem.template.hiring.repositories;

import java.util.List;
import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, ApplicationKey> {
    List<Application> findAllByStatus(ApplicationStatus status);

    List<Application> findAllByCourseIdAndStatus(String courseId, ApplicationStatus status);
}
