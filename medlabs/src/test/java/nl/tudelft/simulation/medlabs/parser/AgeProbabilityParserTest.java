package nl.tudelft.simulation.medlabs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.medlabs.common.MedlabsException;

/**
 * AgeProbabilityParserTest tests the age probabilities parser.
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
public class AgeProbabilityParserTest
{
    @Test
    public void testAgeProbabilityParser() throws MedlabsException
    {
        Map<Integer, Double> ageMap;
        String probStr =
                "age{0-19: 0.021, 20-29: 0.016, 30-39: 0.05, 40-49: 0.11, 50-59: 0.21, 60-69: 0.44, 70-79: 0.60, 80-100: 0.32}";
        ageMap = AgeProbabilityParser.parseAgeProbabilities(probStr);
        assertEquals(101, ageMap.size());
        assertEquals(0.021, ageMap.get(0), 1E-6);
        assertEquals(0.32, ageMap.get(100), 1E-6);
        for (int i = 30; i <= 39; i++)
            assertEquals(0.05, ageMap.get(i), 1E-6);

        probStr = "age{0-100: 0.5, 20-39: 0.75, 100: 0.8}";
        ageMap = AgeProbabilityParser.parseAgeProbabilities(probStr);
        assertEquals(101, ageMap.size());
        assertEquals(0.5, ageMap.get(0), 1E-6);
        assertEquals(0.75, ageMap.get(30), 1E-6);
        assertEquals(0.8, ageMap.get(100), 1E-6);
        for (int i = 0; i <= 19; i++)
            assertEquals(0.5, ageMap.get(i), 1E-6);
        for (int i = 20; i <= 39; i++)
            assertEquals(0.75, ageMap.get(i), 1E-6);
        for (int i = 40; i <= 99; i++)
            assertEquals(0.5, ageMap.get(i), 1E-6);

        // check for error handling
        for (String s : new String[] {"{0-100: 0.5, 20-39: 0.75}", "age{0}", "age(0-10, 1.0)"})
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    AgeProbabilityParser.parseAgeProbabilities(s);
                }
            }, MedlabsException.class);
        }
    }
}
