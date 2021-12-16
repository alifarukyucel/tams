package nl.tudelft.sem.template.hiring.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAcceptRequestModel {
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
        private String courseId;
        private String netId;
        private String duties;
        private int maxHours;

        ApplicationAcceptRequestModelBuilder() {
        }

        public ApplicationAcceptRequestModelBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public ApplicationAcceptRequestModelBuilder netId(String netId) {
            this.netId = netId;
            return this;
        }

        public ApplicationAcceptRequestModelBuilder duties(String duties) {
            this.duties = duties;
            return this;
        }

        public ApplicationAcceptRequestModelBuilder maxHours(int maxHours) {
            this.maxHours = maxHours;
            return this;
        }

        public ApplicationAcceptRequestModel build() {
            return new ApplicationAcceptRequestModel(courseId, netId, duties, maxHours);
        }
    }
}
