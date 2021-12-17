package nl.tudelft.sem.template.hiring.entities.builders;

import nl.tudelft.sem.template.hiring.entities.Application;
import nl.tudelft.sem.template.hiring.entities.builders.interfaces.ApplicationBuilder;
import nl.tudelft.sem.template.hiring.entities.enums.ApplicationStatus;

public class ConcreteApplicationBuilder implements ApplicationBuilder {
    private transient String courseId;
    private transient String netId;
    private transient float grade;
    private transient String motivation;
    private transient ApplicationStatus status;

    public ConcreteApplicationBuilder() {
    }

    public ConcreteApplicationBuilder withCourseId(String courseId) {
        this.courseId = courseId;
        return this;
    }

    public ConcreteApplicationBuilder withNetId(String netId) {
        this.netId = netId;
        return this;
    }

    public ConcreteApplicationBuilder withGrade(long grade) {
        this.grade = grade;
        return this;
    }

    public ConcreteApplicationBuilder withMotivation(String motivation) {
        this.motivation = motivation;
        return this;
    }

    public ConcreteApplicationBuilder withStatus(ApplicationStatus status) {
        this.status = status;
        return this;
    }

    public Application build() { return new Application(courseId, netId, grade, motivation, status); }

}
