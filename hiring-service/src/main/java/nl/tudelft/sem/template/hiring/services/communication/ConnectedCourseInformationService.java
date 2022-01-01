package nl.tudelft.sem.template.hiring.services.communication;

import java.time.LocalDateTime;
import java.util.Optional;
import nl.tudelft.sem.template.hiring.interfaces.CourseInformation;
import nl.tudelft.sem.template.hiring.services.communication.models.CourseInformationResponseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConnectedCourseInformationService implements CourseInformation {
    // Microservice URL from application.properties
    @Value("${microservice.course.base_url}")
    private transient String baseUrl;

    private transient MicroserviceCommunicationHelper comm;

    public ConnectedCourseInformationService(MicroserviceCommunicationHelper comm) {
        this.comm = comm;
    }

    @Override
    public LocalDateTime startDate(String courseId) {
        return LocalDateTime.now().plusWeeks(4L);
    }

    @Override
    public boolean isResponsibleLecturer(String netId, String courseId) {
        if (netId == null || courseId == null) {
            return false;
        }

        try {
            var response = comm.get(baseUrl + "/{courseId}/lecturer/{netId}",
                    Boolean.class, courseId, netId);
            return Optional.ofNullable(response.getBody()).orElse(false);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public CourseInformationResponseModel getCourseById(String id) {
        if (id == null) {
            return null;
        }

        try {
            var response = comm.get(baseUrl + "/{id}", CourseInformationResponseModel.class, id);
            return response.getBody();
        } catch (Exception ex) {
            return null;
        }
    }
}
