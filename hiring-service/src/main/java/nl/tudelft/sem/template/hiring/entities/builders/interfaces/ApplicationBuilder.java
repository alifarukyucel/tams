package nl.tudelft.sem.template.hiring.entities.builders.interfaces;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

public interface ApplicationBuilder {
    ApplicationBuilder withCourseId(String courseId);

    ApplicationBuilder withNetId(String netId);

    ApplicationBuilder withGrade(long grade);

    ApplicationBuilder withMotivation(String motivation);

    ApplicationBuilder withStatus(ApplicationStatus status);

    Application build();
}
