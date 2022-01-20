package nl.tudelft.sem.tams.ta.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class HourRequestModelTest {

    @Test
    void testBuilderCourseId() {
        // arrange
        String course = "TI3105TU";
        var builder = HourRequestModel.builder();

        // act
        builder.withCourseId(course);
        HourRequestModel model = builder.build();

        // assert
        assertEquals(model.getCourse(), course);
        assertNull(model.getNetId());

    }

    @Test
    void testBuilderNetId() {
        // arrange
        String netId = "S.TUDents";
        var builder = HourRequestModel.builder();

        // act
        builder.withNetId(netId);
        HourRequestModel model = builder.build();

        // assert
        assertEquals(model.getNetId(), netId);
        assertNull(model.getCourse());

    }


    @Test
    void testBuilderCourseAndNetId() {
        // arrange
        String course = "TI3105TU";
        String netId = "S.TUDents";
        var builder = HourRequestModel.builder();

        // act
        builder.withCourseId(course);
        builder.withNetId(netId);
        HourRequestModel model = builder.build();

        // assert
        assertEquals(model.getNetId(), netId);
        assertEquals(model.getCourse(), course);
    }


    @Test
    void testBuilderCourseAndNetIdWithChaining() {
        // arrange
        String course = "TI3105TU";
        String netId = "S.TUDents";
        var builder = HourRequestModel.builder();

        // act
        HourRequestModel model = builder
            .withCourseId(course)
            .withNetId(netId)
            .build();


        // assert
        assertEquals(model.getNetId(), netId);
        assertEquals(model.getCourse(), course);
    }
}