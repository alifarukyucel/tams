package nl.tudelft.sem.template.ta.services;

import nl.tudelft.sem.template.ta.interfaces.CourseInformation;
import nl.tudelft.sem.template.ta.services.models.CourseInformationResponseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConnectedCourseInformationService implements CourseInformation {
    @Value("${microservice.course.base_url}")
    private transient String baseUrl;

    private MicroserviceCommunicationHelper comm;

    public ConnectedCourseInformationService(MicroserviceCommunicationHelper comm) {
        this.comm = comm;
    }

    @Override
    public boolean isResponsibleLecturer(String netId, String courseId) {

        return false;
    }

    @Override
    public CourseInformationResponseModel getCourseById(String id) {
        if (id == null) {
            return null;
        }

        ResponseEntity<CourseInformationResponseModel> response;

        try {
            response = comm.get(baseUrl + "/{id}", CourseInformationResponseModel.class, id);
        } catch (Exception ex) {
            return null;
        }

        return response.getBody();
    }
}
