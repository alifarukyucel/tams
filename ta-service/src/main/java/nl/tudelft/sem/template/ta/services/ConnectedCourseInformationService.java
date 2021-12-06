package nl.tudelft.sem.template.ta.services;

import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("online")
public class ConnectedCourseInformationService implements CourseInformation {

    @Override
    public boolean isResponsibleLecturer(String netId, String courseId) {

        return false;
    }





}
