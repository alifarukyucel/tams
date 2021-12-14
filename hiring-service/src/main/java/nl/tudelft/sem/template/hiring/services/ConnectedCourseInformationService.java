package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import org.springframework.stereotype.Service;

@Service
public class ConnectedCourseInformationService implements CourseInformation {

    @Override
    public boolean isResponsibleLecturer(String netId, String courseId) {
        return false;
    }

}
