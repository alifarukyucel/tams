package nl.tudelft.sem.tams.hiring.repositories;

import java.util.List;
import nl.tudelft.sem.tams.hiring.entities.TeachingAssistantApplication;
import nl.tudelft.sem.tams.hiring.entities.compositekeys.TeachingAssistantApplicationKey;
import nl.tudelft.sem.tams.hiring.entities.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingAssistantApplicationRepository
        extends JpaRepository<TeachingAssistantApplication, TeachingAssistantApplicationKey> {
    List<TeachingAssistantApplication> findAllByStatus(ApplicationStatus status);

    List<TeachingAssistantApplication> findAllByCourseIdAndStatus(String courseId, ApplicationStatus status);

}
