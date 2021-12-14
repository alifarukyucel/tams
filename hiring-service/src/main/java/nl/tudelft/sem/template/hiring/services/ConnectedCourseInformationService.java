package nl.tudelft.sem.template.hiring.services;

import java.time.LocalDateTime;
import java.time.Month;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import org.springframework.stereotype.Service;


@Service
public class ConnectedCourseInformationService implements CourseInformation {

    @Override
    public boolean isResponsibleLecturer(String netId, String courseId) {
        return false;
    }

    @Override
    public LocalDateTime getStartDate(String courseId) {
        CourseInformationResponseModel model = new CourseInformationResponseModel();

        //Temporary example start date
        model.setStartDate(LocalDateTime.of(2022,
                Month.SEPTEMBER, 1, 9, 0, 0));
        return model.getStartDate();
    }
}
