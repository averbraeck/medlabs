package nl.tudelft.simulation.medlabs.parser;

import static org.junit.Assert.assertEquals;

import org.djutils.exceptions.Try;
import org.junit.Test;

import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekPattern;
import nl.tudelft.simulation.medlabs.common.MedlabsException;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * ConditionalProbabilityTest checks the correct execution of the ConditionalProbability class.
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
public class ConditionalProbabilityTest
{
    @Test
    public void testConditionalProbability() throws MedlabsException
    {
        ConditionalProbability cp;
        cp = new ConditionalProbability("0.4");
        Person personF50 = new MockPerson(50, true);
        assertEquals(0.4, cp.probability(personF50), 1E-6);

        cp = new ConditionalProbability("gender{F: 0.2, M:0.1}");
        assertEquals(0.2, cp.probability(personF50), 1E-6);
        Person personM50 = new MockPerson(50, false);
        assertEquals(0.1, cp.probability(personM50), 1E-6);

        String s =
                "age{0-19: 0.021, 20-29: 0.016, 30-39: 0.05, 40-49: 0.11, 50-59: 0.21, 60-69: 0.44, 70-79: 0.60, 80-100: 0.32}";
        cp = new ConditionalProbability(s);
        assertEquals(0.21, cp.probability(personF50), 1E-6);
        assertEquals(0.21, cp.probability(personM50), 1E-6);
        Person personF110 = new MockPerson(110, true);
        assertEquals(0.32, cp.probability(personF110), 1E-6);

        // check errors
        for (String e : new String[] {"ag{M:0.1,F:0.2}", "0.55x", "x", ""})
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    new ConditionalProbability(e);
                }
            }, "failed to throw error", MedlabsException.class);
        }
    }

    /** Mock person. */
    class MockPerson implements Person
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** age. */
        private int age;

        /** gender. */
        private boolean genderFemale;

        MockPerson(final int age, final boolean genderFemale)
        {
            this.age = age;
            this.genderFemale = genderFemale;
        }

        @Override
        public double getFloatProperty(final String name)
        {
            return 0;
        }

        @Override
        public MedlabsModelInterface getModel()
        {
            return null;
        }

        @Override
        public int getId()
        {
            return 0;
        }

        @Override
        public void init()
        {
        }

        @Override
        public void executeStartOfActivity()
        {
        }

        @Override
        public void endActivity()
        {
        }

        @Override
        public void changePhase(final DiseasePhase nextPhase)
        {
        }

        @Override
        public int getAge()
        {
            return this.age;
        }

        @Override
        public boolean getGenderFemale()
        {
            return this.genderFemale;
        }

        @Override
        public DiseasePhase getDiseasePhase()
        {
            return null;
        }

        @Override
        public void setDiseasePhase(final DiseasePhase diseasePhase)
        {
        }

        @Override
        public Location getHomeLocation()
        {
            return null;
        }

        @Override
        public short getHomeSubLocationIndex()
        {
            return 0;
        }

        @Override
        public void setHomeSubLocationIndex(final short homeSubLocationIndex)
        {
        }

        @Override
        public Location getCurrentLocation()
        {
            return null;
        }

        @Override
        public void setCurrentLocation(final Location currentLocation)
        {
        }

        @Override
        public short getCurrentSubLocationIndex()
        {
            return 0;
        }

        @Override
        public void setCurrentSubLocationIndex(final short currentSubLocationIndex)
        {
        }

        @Override
        public Activity getCurrentActivity()
        {
            return null;
        }

        @Override
        public WeekPattern getStandardWeekPattern()
        {
            return null;
        }

        @Override
        public void setStandardWeekPattern(final WeekPattern standardWeekPattern)
        {
        }

        @Override
        public WeekPattern getCurrentWeekPattern()
        {
            return null;
        }

        @Override
        public void setCurrentWeekPattern(final WeekPattern currentWeekPattern)
        {
        }

        @Override
        public void setExposureTime(final float exposureTime)
        {
        }

        @Override
        public float getExposureTime()
        {
            return 0;
        }

    }
}
