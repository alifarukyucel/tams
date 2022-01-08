package nl.tudelft.sem.tams.hiring.interfaces;

import java.time.LocalDateTime;
import nl.tudelft.sem.tams.hiring.services.communication.models.CourseInformationResponseModel;


public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    CourseInformationResponseModel getCourseById(String id);

    LocalDateTime startDate(String courseId);
}
