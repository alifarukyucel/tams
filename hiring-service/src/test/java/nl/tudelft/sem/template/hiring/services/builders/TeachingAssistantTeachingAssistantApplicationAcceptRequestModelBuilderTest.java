package nl.tudelft.sem.template.hiring.services.builders;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.hiring.models.TeachingAssistantApplicationAcceptRequestModel;
import org.junit.jupiter.api.Test;

public class TeachingAssistantTeachingAssistantApplicationAcceptRequestModelBuilderTest {

    private TeachingAssistantApplicationAcceptRequestModel model;

    @Test
    public void testWithCourseId() {
        //arrange
        String courseId = "CSE1300";

        //act
        model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(courseId)
                .build();

        //assert
        assertThat(model.getCourseId()).isEqualTo(courseId);
        assertThat(model.getNetId()).isNull();
        assertThat(model.getDuties()).isNull();
        assertThat(model.getMaxHours()).isZero();
    }

    @Test
    public void testWithNetId() {
        //arrange
        String netId = "kverhoef";

        //act
        model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withNetId(netId)
                .build();

        //assert
        assertThat(model.getNetId()).isEqualTo(netId);
        assertThat(model.getCourseId()).isNull();
        assertThat(model.getDuties()).isNull();
        assertThat(model.getMaxHours()).isZero();
    }

    @Test
    public void testWithDuties() {
        //arrange
        String duties = "DUTIES";

        //act
        model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withDuties(duties)
                .build();

        //assert
        assertThat(model.getDuties()).isEqualTo(duties);
        assertThat(model.getCourseId()).isNull();
        assertThat(model.getNetId()).isNull();
        assertThat(model.getMaxHours()).isZero();
    }

    @Test
    public void testWithMaxHours() {
        //arrange
        int maxHours = 8;

        //act
        model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withMaxHours(maxHours)
                .build();

        //assert
        assertThat(model.getMaxHours()).isEqualTo(maxHours);
        assertThat(model.getCourseId()).isNull();
        assertThat(model.getNetId()).isNull();
        assertThat(model.getDuties()).isNull();
    }

    @Test
    public void testAllTogether() {
        //arrange
        String courseId = "CSE1300";
        String netId = "kverhoef";
        String duties = "DUTIES";
        int maxHours = 8;

        //act
        model = TeachingAssistantApplicationAcceptRequestModel.builder()
                .withCourseId(courseId)
                .withNetId(netId)
                .withDuties(duties)
                .withMaxHours(maxHours)
                .build();

        //assert
        assertThat(model.getCourseId()).isEqualTo(courseId);
        assertThat(model.getNetId()).isEqualTo(netId);
        assertThat(model.getDuties()).isEqualTo(duties);
        assertThat(model.getMaxHours()).isEqualTo(maxHours);
    }



}
