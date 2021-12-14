package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.models.external.CourseResponseModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class CourseInformationService implements CourseInformation {
    @Override
    public boolean isResponsibleLecturer(String netId, String courseId) {
        return false;
    }

    @Override
    public LocalDateTime getStartDate(String courseId) {
        CourseResponseModel model = new CourseResponseModel();

        //Temporary example start date
        model.setStartDate(LocalDateTime.of(2022,
                Month.SEPTEMBER, 1, 9, 0, 0));
        return model.getStartDate();
    }
}

