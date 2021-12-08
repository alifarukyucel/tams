package nl.tudelft.sem.template.ta.repositories;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.ta.entities.WorkedHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkedHoursRepository extends JpaRepository<WorkedHours, UUID> {


    /**
     * Queries for "open" hours in the database with a courseId.
     * @param courseId
     * @return
     */
    @Query("SELECT w FROM WorkedHours w " +
            "JOIN w.contract c " +
            "WHERE w.reviewed = false AND c.courseId = :courseId")
    List<WorkedHours> findNonReviewedHoursBy(@Param("courseId") String courseId);

    /**
     * Queries for "open" hours in the database with a courseId and netId.
     * @param courseId
     * @param netId
     * @return
     */
    @Query("SELECT w FROM WorkedHours w " +
            "JOIN w.contract c " +
            "WHERE w.reviewed = false AND c.courseId = :courseId AND c.netId = :netId")
    List<WorkedHours> findNonReviewedHoursBy(
            @Param("courseId") String courseId,
            @Param("netId") String netId
    );

}

