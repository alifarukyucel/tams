package nl.tudelft.sem.template.ta.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class HourRequestModel {
    private String course;
    private String netId;

    public static HourRequestModelBuilder builder() {
        return new HourRequestModelBuilder();
    }

    /*
     * Builder for the HourRequestModel.
     */
    public static class HourRequestModelBuilder {
        private String course;
        private String netId;

        HourRequestModelBuilder() {
        }

        public HourRequestModelBuilder course(String course) {
            this.course = course;
            return this;
        }

        public HourRequestModelBuilder netId(String netId) {
            this.netId = netId;
            return this;
        }

        public HourRequestModel build() {
            return new HourRequestModel(course, netId);
        }
    }
}
