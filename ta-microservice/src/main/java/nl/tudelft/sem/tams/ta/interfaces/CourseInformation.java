package nl.tudelft.sem.tams.ta.interfaces;

import nl.tudelft.sem.tams.ta.services.communication.models.CourseInformationResponseModel;

public interface CourseInformation {
    boolean isResponsibleLecturer(String netId, String courseId);

    int getAmountOfStudents(String courseId);
}
