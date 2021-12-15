package nl.tudelft.sem.template.hiring.services;

import java.time.LocalDate;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import org.springframework.stereotype.Service;

@Service
public class ConnectedCourseInformationService implements CourseInformation {

    @Override
    public LocalDate startDate(String courseId) {
        return LocalDate.now().plusWeeks(4L);
    }
}
