package nl.tudelft.sem.template.hiring.entities.compositeKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationKey implements Serializable {
    private String courseId;
    private String netId;

    public static final long serialVersionUID = 1;
}
