package nl.tudelft.sem.template.hiring.interfaces;

import java.time.LocalDateTime;
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;

public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    CourseInformationResponseModel getCourseById(String id);

    LocalDateTime getStartDate(String courseId);
}
