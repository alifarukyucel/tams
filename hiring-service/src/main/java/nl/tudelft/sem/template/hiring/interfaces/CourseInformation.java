package nl.tudelft.sem.template.hiring.interfaces;

import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;

import java.time.LocalDate;

public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    CourseInformationResponseModel getCourseById(String id);
    LocalDate startDate(String courseId);
}
