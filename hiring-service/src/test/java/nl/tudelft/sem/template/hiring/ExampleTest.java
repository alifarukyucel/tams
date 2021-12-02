package nl.tudelft.sem.template.hiring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.hiring.Example;
import org.junit.jupiter.api.Test;

public class ExampleTest {

    @Test
    public void testGettersSetters() {
        Example exampleTest = new Example("name");
        assertEquals("name", exampleTest.getName());
        exampleTest.setName("newName");
        assertEquals("newName", exampleTest.getName());
    }
}
