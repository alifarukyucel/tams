package nl.tudelft.sem.tams.ta.models;

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
        private transient String course;
        private transient String netId;

        HourRequestModelBuilder() {
        }

        public HourRequestModelBuilder withCourseId(String courseId) {
            this.course = courseId;
            return this;
        }

        public HourRequestModelBuilder withNetId(String netId) {
            this.netId = netId;
            return this;
        }

        public HourRequestModel build() {
            return new HourRequestModel(course, netId);
        }
    }
}
