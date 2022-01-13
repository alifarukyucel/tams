package nl.tudelft.sem.tams.ta.services.communication;

import java.util.Optional;
import nl.tudelft.sem.tams.ta.interfaces.CourseInformation;
import nl.tudelft.sem.tams.ta.services.communication.models.CourseInformationResponseModel;
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

    /**
     * Returns the course information response model.
     *
     * @param id id of the course.
     * @return all information found on the course.
     */
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

    /**
     * Returns an amount of students for a course.
     *
     * @param courseId the course id to check for student
     * @return amount of students
     */
    @Override
    public int getAmountOfStudents(String courseId) {
        CourseInformationResponseModel model = getCourseById(courseId);
        if (model == null) {
            throw new IllegalArgumentException("Could not retrieve course");
        }
        return model.getNumberOfStudents();
    }

}
