package nl.tudelft.sem.template.hiring.entities.compositekeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationKey implements Serializable {
    private String courseId;
    private String netId;

    public static final long serialVersionUID = 1;
}
