package nl.tudelft.sem.tams.ta.repositories;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.tams.ta.entities.HourDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HourDeclarationRepository extends JpaRepository<HourDeclaration, UUID> {


    /**
     * Queries for "open" hours in the database with a courseId.
     *
     * @param courseId courseId of the requested hour declarations
     * @return list of hour declarations
     */
    @Query("SELECT h FROM HourDeclaration h "
        + "JOIN h.contract c "
        + "WHERE h.reviewed = false AND c.courseId = :courseId")
    List<HourDeclaration> findNonReviewedHoursByCourseId(@Param("courseId") String courseId);

    /**
     * Queries for "open" hours in the database with a courseId and netId.
     *
     * @param courseId of requested hour declarations
     * @param netId of contract of requested hour declaration
     * @return list of hour declarations
     */
    @Query("SELECT h FROM HourDeclaration h "
        + "JOIN h.contract c "
        + "WHERE h.reviewed = false AND c.courseId = :courseId AND c.netId = :netId")
    List<HourDeclaration> findNonReviewedHoursByCourseIdAndNetId(
        @Param("courseId") String courseId,
        @Param("netId") String netId
    );


}