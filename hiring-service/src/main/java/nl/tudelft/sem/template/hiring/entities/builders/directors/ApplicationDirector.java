package nl.tudelft.sem.template.hiring.entities.builders.directors;

import nl.tudelft.sem.template.hiring.entities.builders.interfaces.ApplicationBuilder;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

public class ApplicationDirector {

    /**
     * Creates an open application with default status PENDING.
     *
     * @param applicationBuilder the contract Builder to direct.
     */
    public void createOpenApplication(ApplicationBuilder applicationBuilder) {
        applicationBuilder.withStatus(ApplicationStatus.PENDING);
    }
}
