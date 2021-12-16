package nl.tudelft.sem.template.hiring.services.communication.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Data
public class CreateContractRequestModel {
    private String courseId;
    private String netId;
    private String duties; 
    private int maxHours;

    public static CreateContractRequestModelBuilder builder() {
        return new CreateContractRequestModelBuilder();
    }

    /*
     * Builder for the CreateContractRequestModel.
     */
    public static class CreateContractRequestModelBuilder {
        private transient String courseId;
        private transient String netId;
        private transient String duties;
        private transient int maxHours;

        CreateContractRequestModelBuilder() {
        }

        public CreateContractRequestModelBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public CreateContractRequestModelBuilder netId(String netId) {
            this.netId = netId;
            return this;
        }

        public CreateContractRequestModelBuilder duties(String duties) {
            this.duties = duties;
            return this;
        }

        public CreateContractRequestModelBuilder maxHours(int maxHours) {
            this.maxHours = maxHours;
            return this;
        }

        public CreateContractRequestModel build() {
            return new CreateContractRequestModel(courseId, netId, duties, maxHours);
        }
    }
}