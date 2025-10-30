package nl.tudelft.simulation.medlabs.parser;

import java.util.HashMap;
import java.util.Map;

import nl.tudelft.simulation.medlabs.common.MedlabsException;

/**
 * The AgeProbabilityParser utility class can parse a string with probabilities for age groups. The format is, e.g.,
 * 
 * <pre>
 *   age{0-19: 0.021, 20-29: 0.016, 30-39: 0.05, 40-49: 0.11, 50-59: 0.21, 60-69: 0.44, 70-79: 0.60, 80-100: 0.32}
 * </pre>
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class AgeProbabilityParser
{
    /**
     * Utility class, no constructor.
     */
    private AgeProbabilityParser()
    {
        // Utility class, no constructor
    }

    /**
     * Parse a string with an age-dependent probability. Intervals that have not been specified will have a probability of 0.0.
     * The string is parsed left-to-right. Overlaps do not matter -- so you can specify: age{0-100: 0.5, 20-39: 0.75} to
     * indicate a 0.5 probability for all ages except 20-39 (inclusive) that have a 0.75 probability.
     * @param probStr String; the textual representation of the probabilities
     * @return Map&lt;Integer, Double&gt;; a complete map from age to probability for 0-100 years.
     * @throws MedlabsException when the string could not be parsed
     */
    public static Map<Integer, Double> parseAgeProbabilities(final String probStr) throws MedlabsException
    {
        if (!probStr.startsWith("age{"))
            throw new MedlabsException("parsing age-probabilities '" + probStr + "': string does not start with 'age{'");
        if (!probStr.trim().endsWith("}"))
            throw new MedlabsException("parsing age-probabilities '" + probStr + "': does not end with '}'");
        Map<Integer, Double> ageProbMap = new HashMap<>();
        for (int age = 0; age <= 100; age++)
            ageProbMap.put(age, 0.0);
        String args = probStr.substring(probStr.indexOf('{') + 1, probStr.indexOf('}')).trim();
        String[] argArray = args.split(",");
        for (String arg : argArray)
        {
            if (arg.indexOf(":") == -1)
                throw new MedlabsException("parsing age-probabilities '" + probStr + "': has entry " + arg + " without ':'");
            String[] arg2 = arg.split(":");
            if (arg2[0].indexOf("-") == -1)
            {
                // single age
                int age = argInt(probStr, arg2[0]);
                double prob = argDouble(probStr, arg2[1]);
                ageProbMap.put(age, prob);
            }
            else
            {
                // age interval (inclusive)
                String[] arg22 = arg2[0].split("-");
                int ageMin = argInt(probStr, arg22[0]);
                int ageMax = argInt(probStr, arg22[1]);
                double prob = argDouble(probStr, arg2[1]);
                for (int age = ageMin; age <= ageMax; age++)
                    ageProbMap.put(age, prob);
            }
        }
        return ageProbMap;
    }

    private static double argDouble(final String probStr, final String arg) throws MedlabsException
    {
        try
        {
            return Double.parseDouble(arg.trim());
        }
        catch (Exception exception)
        {
            throw new MedlabsException("parsing age-probabilities '" + probStr + "': entry " + arg + " is not a double");
        }
    }

    private static int argInt(final String probStr, final String arg) throws MedlabsException
    {
        try
        {
            return Integer.parseInt(arg.trim());
        }
        catch (Exception exception)
        {
            throw new MedlabsException("parsing age-probabilities '" + probStr + "': entry " + arg + " is not an int");
        }
    }

}
