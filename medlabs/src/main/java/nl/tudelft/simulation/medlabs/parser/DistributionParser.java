package nl.tudelft.simulation.medlabs.parser;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.DistBernoulli;
import nl.tudelft.simulation.jstats.distributions.DistBeta;
import nl.tudelft.simulation.jstats.distributions.DistBinomial;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteUniform;
import nl.tudelft.simulation.jstats.distributions.DistErlang;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistGamma;
import nl.tudelft.simulation.jstats.distributions.DistGeometric;
import nl.tudelft.simulation.jstats.distributions.DistLogNormal;
import nl.tudelft.simulation.jstats.distributions.DistNegBinomial;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.DistNormalTrunc;
import nl.tudelft.simulation.jstats.distributions.DistPearson5;
import nl.tudelft.simulation.jstats.distributions.DistPearson6;
import nl.tudelft.simulation.jstats.distributions.DistPoisson;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.common.MedlabsException;

/**
 * The DistributionParser utility class can parse a string with a specified distribution, such as Triangular(1, 2, 3) or
 * Uniform(5, 10).
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
public final class DistributionParser
{
    /**
     * Utility class, no constructor.
     */
    private DistributionParser()
    {
        // Utility class, no constructor
    }

    /**
     * Parse a string with a specified continuous distribution, such as Triangular(1, 2, 3) or Uniform(5, 10).
     * @param distStr String; the textual representation of the distribution
     * @param stream StreamInterface; the random stream to use for the instantiated distribution
     * @return DistCOntinuous; the continuous distribution belonging to the string
     * @throws MedlabsException when the string could not be parsed
     */
    public static DistContinuous parseDistContinuous(final String distStr, final StreamInterface stream) throws MedlabsException
    {
        if (distStr.indexOf('(') == -1 || distStr.indexOf(')') == -1)
            throw new MedlabsException("parsing distribution " + distStr + ": no '(' or ')' in the string");
        if (!distStr.trim().endsWith(")"))
            throw new MedlabsException("parsing distribution " + distStr + ": does not end with ')'");
        String distName = distStr.substring(0, distStr.indexOf('(')).trim().toLowerCase();
        String args = distStr.substring(distStr.indexOf('(') + 1, distStr.indexOf(')')).trim();
        String[] argArray = args.split(",");
        switch (distName)
        {
            case "constant":
            case "const":
            {
                checkArgs(distStr, argArray, 1);
                double c = argDouble(distStr, argArray, 0);
                return new DistConstant(stream, c);
            }

            case "exponential":
            case "expo":
            {
                checkArgs(distStr, argArray, 1);
                double lambda = argDouble(distStr, argArray, 0);
                return new DistExponential(stream, lambda);
            }

            case "triangular":
            case "tria":
            {
                checkArgs(distStr, argArray, 3);
                double min = argDouble(distStr, argArray, 0);
                double mode = argDouble(distStr, argArray, 1);
                double max = argDouble(distStr, argArray, 2);
                return new DistTriangular(stream, min, mode, max);
            }

            case "normal":
            case "norm":
            {
                checkArgs(distStr, argArray, 2);
                double mean = argDouble(distStr, argArray, 0);
                double stdev = argDouble(distStr, argArray, 1);
                return new DistNormal(stream, mean, stdev);
            }

            case "truncatednormal":
            case "normaltrunc":
            {
                checkArgs(distStr, argArray, 4);
                double mean = argDouble(distStr, argArray, 0);
                double stdev = argDouble(distStr, argArray, 1);
                double min = argDouble(distStr, argArray, 2);
                double max = argDouble(distStr, argArray, 3);
                return new DistNormalTrunc(stream, mean, stdev, min, max);
            }

            case "beta":
            {
                checkArgs(distStr, argArray, 2);
                double alpha1 = argDouble(distStr, argArray, 0);
                double alpha2 = argDouble(distStr, argArray, 1);
                return new DistBeta(stream, alpha1, alpha2);
            }

            case "erlang":
            {
                checkArgs(distStr, argArray, 2);
                double scale = argDouble(distStr, argArray, 0);
                int k = argInt(distStr, argArray, 1);
                return new DistErlang(stream, scale, k);
            }

            case "gammma":
            {
                checkArgs(distStr, argArray, 2);
                double shape = argDouble(distStr, argArray, 0);
                double scale = argDouble(distStr, argArray, 1);
                return new DistGamma(stream, shape, scale);
            }

            case "lognormal":
            case "logn":
            {
                checkArgs(distStr, argArray, 2);
                double mean = argDouble(distStr, argArray, 0);
                double stdev = argDouble(distStr, argArray, 1);
                return new DistLogNormal(stream, mean, stdev);
            }

            case "pearsoo5":
            {
                checkArgs(distStr, argArray, 2);
                double alpha = argDouble(distStr, argArray, 0);
                double beta = argDouble(distStr, argArray, 1);
                return new DistPearson5(stream, alpha, beta);
            }

            case "pearson6":
            {
                checkArgs(distStr, argArray, 3);
                double alpha1 = argDouble(distStr, argArray, 0);
                double alpha2 = argDouble(distStr, argArray, 1);
                double beta = argDouble(distStr, argArray, 2);
                return new DistPearson6(stream, alpha1, alpha2, beta);
            }

            case "uniform":
            case "unif":
            {
                checkArgs(distStr, argArray, 2);
                double min = argDouble(distStr, argArray, 0);
                double max = argDouble(distStr, argArray, 1);
                return new DistUniform(stream, min, max);
            }

            default:
                throw new MedlabsException("parsing distribution " + distStr + ": distribution " + distName + " unknown");
        }
    }

    /**
     * Parse a string with a specified discrete distribution, such as Bernoulli(0.25) or .
     * @param distStr String; the textual representation of the distribution
     * @param stream StreamInterface; the random stream to use for the instantiated distribution
     * @return DistDiscrete; the discrete distribution belonging to the string
     * @throws MedlabsException when the string could not be parsed
     */
    public static DistDiscrete parseDistDiscrete(final String distStr, final StreamInterface stream) throws MedlabsException
    {
        if (distStr.indexOf('(') == -1 || distStr.indexOf(')') == -1)
            throw new MedlabsException("parsing distribution " + distStr + ": no '(' or ')' in the string");
        if (!distStr.trim().endsWith(")"))
            throw new MedlabsException("parsing distribution " + distStr + ": does not end with ')'");
        String distName = distStr.substring(0, distStr.indexOf('(') - 1).trim().toLowerCase();
        String args = distStr.substring(distStr.indexOf('(') + 1, distStr.indexOf(')' - 1)).trim();
        String[] argArray = args.split(",");
        switch (distName)
        {
            case "bernoulli":
            {
                checkArgs(distStr, argArray, 1);
                double p = argDouble(distStr, argArray, 0);
                return new DistBernoulli(stream, p);
            }

            case "binomial":
            {
                checkArgs(distStr, argArray, 2);
                int n = argInt(distStr, argArray, 0);
                double p = argDouble(distStr, argArray, 1);
                return new DistBinomial(stream, n, p);
            }

            case "discreteconstant":
            {
                checkArgs(distStr, argArray, 1);
                int c = argInt(distStr, argArray, 0);
                return new DistDiscreteConstant(stream, c);
            }

            case "discreteuniform":
            {
                checkArgs(distStr, argArray, 2);
                int min = argInt(distStr, argArray, 0);
                int max = argInt(distStr, argArray, 1);
                return new DistDiscreteUniform(stream, min, max);
            }

            case "geometric":
            {
                checkArgs(distStr, argArray, 1);
                double p = argDouble(distStr, argArray, 0);
                return new DistGeometric(stream, p);
            }

            case "negbinomial":
            {
                checkArgs(distStr, argArray, 2);
                int s = argInt(distStr, argArray, 0);
                double p = argDouble(distStr, argArray, 1);
                return new DistNegBinomial(stream, s, p);
            }

            case "poisson":
            case "pois":
            {
                checkArgs(distStr, argArray, 1);
                double lambda = argDouble(distStr, argArray, 0);
                return new DistPoisson(stream, lambda);
            }

            default:
                throw new MedlabsException("parsing distribution " + distStr + ": distribution " + distName + " unknown");
        }
    }

    private static void checkArgs(final String distStr, final String[] argArray, final int expectedNr) throws MedlabsException
    {
        Throw.when(argArray.length != expectedNr, MedlabsException.class,
                "parsing distribution " + distStr + ": distribution needs " + expectedNr + " argument(s)");
    }

    private static double argDouble(final String distStr, final String[] argArray, final int argNr) throws MedlabsException
    {
        try
        {
            return Double.parseDouble(argArray[argNr].trim());
        }
        catch (Exception exception)
        {
            throw new MedlabsException("parsing distribution " + distStr + ": argument " + argNr + " is not a double value");
        }
    }

    private static int argInt(final String distStr, final String[] argArray, final int argNr) throws MedlabsException
    {
        try
        {
            return Integer.parseInt(argArray[argNr].trim());
        }
        catch (Exception exception)
        {
            throw new MedlabsException("parsing distribution " + distStr + ": argument " + argNr + " is not an int value");
        }
    }
}
