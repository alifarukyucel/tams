package nl.tudelft.sem.template.ta.interfaces;

import nl.tudelft.sem.template.ta.services.models.CourseInformationResponseModel;

public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    CourseInformationResponseModel getCourseById(String id);
}
