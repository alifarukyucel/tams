package nl.tudelft.sem.template.hiring.repositories;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.compositekeys.ApplicationKey;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, ApplicationKey> {
    public List<Application> findAllByStatus(ApplicationStatus status);

    public List<Application> findAllByCourseIdAndStatus(String courseId, ApplicationStatus status);
}
