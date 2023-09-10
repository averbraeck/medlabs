package nl.tudelft.simulation.medlabs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.medlabs.common.MedlabsException;

/**
 * GenderProbabilityParserTest tests the gender probabilities parser.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class GenderProbabilityParserTest
{
    @Test
    public void testgenderProbabilityParser() throws MedlabsException
    {
        double[] probs;
        String probStr = "gender{F: 0.2, M:0.1}";
        probs = GenderProbabilityParser.parseGenderProbabilities(probStr);
        assertEquals(2, probs.length);
        assertEquals(0.2, probs[0], 1E-6);
        assertEquals(0.1, probs[1], 1E-6);

        probStr = "gender{M:  0.5,  F:0.9 }";
        probs = GenderProbabilityParser.parseGenderProbabilities(probStr);
        assertEquals(2, probs.length);
        assertEquals(0.9, probs[0], 1E-6);
        assertEquals(0.5, probs[1], 1E-6);

        // check for error handling
        for (String s : new String[] {"{M:0.1,F:0.5}", "gender{M}", "gender(M:0.5,F:0.1)", "gender{F:0.2, F:0.1}",
                "gender{X:0.2, M:0.1}", "gender{F:0.2, M: 0.5, F:0.1}"})
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    GenderProbabilityParser.parseGenderProbabilities(s);
                }
            }, "failed to throw error on " + s, MedlabsException.class);
        }
    }
}
