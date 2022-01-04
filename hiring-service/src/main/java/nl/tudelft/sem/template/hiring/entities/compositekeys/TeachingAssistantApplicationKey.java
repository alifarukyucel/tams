package nl.tudelft.sem.template.hiring.entities.compositekeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingAssistantApplicationKey implements Serializable {
    private String courseId;
    private String netId;

    public static final long serialVersionUID = 1;
}
