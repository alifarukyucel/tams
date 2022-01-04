package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingAssistantApplicationAcceptRequestModel {
    private String courseId;
    private String netId;
    private String duties;
    private int maxHours;

    public static ApplicationAcceptRequestModelBuilder builder() {
        return new ApplicationAcceptRequestModelBuilder();
    }

    /*
     * Builder for the ApplicationAcceptRequestModel.
     */
    public static class ApplicationAcceptRequestModelBuilder {
        private transient String courseId;
        private transient String netId;
        private transient String duties;
        private transient int maxHours;

        ApplicationAcceptRequestModelBuilder() {
        }

        public ApplicationAcceptRequestModelBuilder withCourseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public ApplicationAcceptRequestModelBuilder withNetId(String netId) {
            this.netId = netId;
            return this;
        }

        public ApplicationAcceptRequestModelBuilder withDuties(String duties) {
            this.duties = duties;
            return this;
        }

        public ApplicationAcceptRequestModelBuilder withMaxHours(int maxHours) {
            this.maxHours = maxHours;
            return this;
        }

        public TeachingAssistantApplicationAcceptRequestModel build() {
            return new TeachingAssistantApplicationAcceptRequestModel(courseId, netId, duties, maxHours);
        }
    }
}
