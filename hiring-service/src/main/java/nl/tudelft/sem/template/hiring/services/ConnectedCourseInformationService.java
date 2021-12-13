package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;

@Service
public class ConnectedCourseInformationService implements CourseInformation {

    @Override
    public LocalDate startDate(String courseId) {
        return LocalDate.now();
    }
}
