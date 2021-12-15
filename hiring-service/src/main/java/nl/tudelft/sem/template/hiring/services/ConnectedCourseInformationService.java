package nl.tudelft.sem.template.hiring.services;

import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ConnectedCourseInformationService implements CourseInformation {

    @Override
    public LocalDate startDate(String courseId) {
        return LocalDate.now().plusWeeks(4L);
    }
}
