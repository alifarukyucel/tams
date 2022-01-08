package nl.tudelft.sem.template.ta.entities.builders.directors;

import nl.tudelft.sem.template.ta.entities.builders.interfaces.HourDeclarationBuilder;

public class HourDeclarationDirector {
    /**
     * Creates an unreviewed hour declaration.
     *
     * @param builder the HourDeclarationBuilder to direct.
     */
    public void createUnsignedContract(HourDeclarationBuilder builder) {
        builder.withApproved(false).withReviewed(false);
    }
}
