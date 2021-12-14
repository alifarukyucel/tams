package nl.tudelft.sem.template.hiring.interfaces;

import nl.tudelft.sem.template.hiring.services.models.CourseInformationResponseModel;

public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    CourseInformationResponseModel getCourseById(String id);
}
