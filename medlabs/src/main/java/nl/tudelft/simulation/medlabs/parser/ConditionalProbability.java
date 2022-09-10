package nl.tudelft.simulation.medlabs.parser;

import java.util.Map;

import nl.tudelft.simulation.medlabs.common.MedlabsException;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * ConditionalProbability contains a probability that is fixed, age-dependent or gender-dependent. It can return the correct
 * probability for a person.
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
public class ConditionalProbability
{
    /** fixed probability as a double between 0 and 1. */
    private double fixedprobability = Double.NaN;

    /** age-based probability map, mapping age onto probability. */
    private Map<Integer, Double> ageBasedProbabilities = null;

    /** gender-based probabilities; [0] = female, [1] = male. */
    private double[] genderBasedProbabilities = null;

    /**
     * Initialize the conditional probability.
     * @param probStr String; The string that is either a double between 0 and 1, or an age-dependent probability string, or a
     *            gender dependent probability string
     * @throws MedlabsException when string cannot be parsed properly
     */
    public ConditionalProbability(final String probStr) throws MedlabsException
    {
        if (probStr.startsWith("age{"))
            this.ageBasedProbabilities = AgeProbabilityParser.parseAgeProbabilities(probStr);
        else if (probStr.startsWith("gender{"))
            this.genderBasedProbabilities = GenderProbabilityParser.parseGenderProbabilities(probStr);
        else
        {
            try
            {
                this.fixedprobability = Double.parseDouble(probStr);
            }
            catch (Exception exception)
            {
                throw new MedlabsException("could not parse fixed probability string " + probStr);
            }
        }
    }

    /**
     * Return the (conditional or fixed) probability for this person.
     * @param person Person; the person to return the probability for
     * @return double; the probability for this person
     */
    public double probability(final Person person)
    {
        if (!Double.isNaN(this.fixedprobability))
            return this.fixedprobability;
        else if (this.genderBasedProbabilities != null)
            return this.genderBasedProbabilities[person.getGenderFemale() ? 0 : 1];
        if (person.getAge() <= 100)
            return this.ageBasedProbabilities.get(person.getAge());
        return this.ageBasedProbabilities.get(100);
    }
}
