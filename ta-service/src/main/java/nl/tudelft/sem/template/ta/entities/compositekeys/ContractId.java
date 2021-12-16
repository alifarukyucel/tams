package nl.tudelft.sem.template.ta.entities.compositekeys;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
public class ContractId implements Serializable {
    private String netId;
    private String courseId;

    public static final long serialVersionUID = "ContractId".hashCode();

    public static ContractIdBuilder builder() {
        return new ContractIdBuilder();
    }

    /*
     * Builder for ContractId.
     */
    public static class ContractIdBuilder {
        private String netId;
        private String courseId;

        ContractIdBuilder() {
        }

        public ContractIdBuilder netId(String netId) {
            this.netId = netId;
            return this;
        }

        public ContractIdBuilder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public ContractId build() {
            return new ContractId(netId, courseId);
        }
    }
}
