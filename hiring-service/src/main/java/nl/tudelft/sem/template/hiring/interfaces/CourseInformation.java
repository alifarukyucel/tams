package nl.tudelft.sem.template.hiring.interfaces;

import java.time.LocalDateTime;

public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    LocalDateTime getStartDate(String courseId);
}
