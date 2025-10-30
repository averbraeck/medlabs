package nl.tudelft.simulation.medlabs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.djutils.draw.bounds.Bounds2d;
import org.junit.jupiter.api.Test;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.statistics.SimulationStatistic;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.activity.ActivityMonitor;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekPattern;
import nl.tudelft.simulation.medlabs.common.ReproducibleRandomGenerator;
import nl.tudelft.simulation.medlabs.disease.DiseaseMonitor;
import nl.tudelft.simulation.medlabs.disease.DiseaseProgression;
import nl.tudelft.simulation.medlabs.disease.DiseaseTransmission;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.PersonMonitor;
import nl.tudelft.simulation.medlabs.person.PersonType;
import nl.tudelft.simulation.medlabs.policy.Policy;
import nl.tudelft.simulation.medlabs.properties.Properties;
import nl.tudelft.simulation.medlabs.simulation.SimpleDevsSimulatorInterface;

/**
 * GridTest tests the grid functions and lat/lon functions of the ModelInterface.
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
public class GridTest
{
    @Test
    public void testLatLon()
    {
        TestModel model = new TestModel();
        assertEquals(3600.0, model.lonToM(4.35210f), 500.0, "lonToM(4.35210) should be about +3600");
        assertEquals(-3000.0, model.latToM(52.0321f), 500.0, "latToM(52.0321) should be about -3000");

        TestModel eqModel = new TestModel()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public float getGridSizeM()
            {
                return 1000f;
            }

            @Override
            public float getLonCenter()
            {
                return 0.0f;
            }

            @Override
            public float getLatCenter()
            {
                return 0.0f;
            }

        };
        assertEquals(111000.0, eqModel.lonToM(1.0f), 1000.0, "lonToM(1.0) on (0,0) should be about 111 km");
        assertEquals(111000.0, eqModel.latToM(1.0f), 1000.0, "latToM(1.0) on (0,0) should be about 111 km");
    }

    @Test
    public void testGrid()
    {
        TestModel model = new TestModel();
        assertEquals(36, model.lonToGridX(4.35210f), "lonToGridX(4.35210) should be about +36");
        assertEquals(-31, model.latToGridY(52.0321f), "latToGridY(52.0321) should be about -31");

        TestModel eqModel = new TestModel()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public float getGridSizeM()
            {
                return 1000f;
            }

            @Override
            public float getLonCenter()
            {
                return 0.0f;
            }

            @Override
            public float getLatCenter()
            {
                return 0.0f;
            }

        };
        assertEquals(111, eqModel.lonToGridX(1.0f), "lonToGridX(1.0) on (0,0) should be about 111 km");
        assertEquals(111, eqModel.latToGridY(1.0f), "latToGridY(1.0) on (0,0) should be about 111 km");
    }

    /** TestModel class. */
    private static class TestModel implements MedlabsModelInterface
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** {@inheritDoc} */
        @Override
        public Bounds2d getExtent()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
        }

        /** {@inheritDoc} */
        @Override
        public List<SimulationStatistic<Double>> getOutputStatistics()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void setStreamInformation(final StreamInformation streamInformation)
        {
        }

        /** {@inheritDoc} */
        @Override
        public StreamInformation getStreamInformation()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public SimpleDevsSimulatorInterface getSimulator()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isInteractive()
        {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public void setInteractive(final boolean interactive)
        {
        }

        /** {@inheritDoc} */
        @Override
        public InputParameterMap getInputParameterMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public StreamInterface getRandomStream()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public DistUniform getU01()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public ReproducibleRandomGenerator getReproducibleJava2Random()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public TIntObjectMap<Person> getPersonMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public List<PersonType> getPersonTypeList()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Map<Class<? extends Person>, PersonType> getPersonTypeClassMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public TIntObjectMap<TIntSet> getFamilyMembersByHomeLocation()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Properties getPersonProperties()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, LocationType> getLocationTypeNameMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Map<Byte, LocationType> getLocationTypeIndexMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public List<LocationType> getLocationTypeList()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public TIntObjectMap<Location> getLocationMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, WeekPattern> getWeekPatternMap()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public List<WeekPattern> getWeekPatternList()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void checkChangeActivityPattern(final Person person)
        {
        }

        /** {@inheritDoc} */
        @Override
        public void setActivityMonitor(final ActivityMonitor activityMonitor)
        {
        }

        /** {@inheritDoc} */
        @Override
        public PersonMonitor getPersonMonitor()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void setPersonMonitor(final PersonMonitor personMonitor)
        {
        }

        /** {@inheritDoc} */
        @Override
        public ActivityMonitor getActivityMonitor()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void setDiseaseProgression(final DiseaseProgression diseaseProgression)
        {
        }

        /** {@inheritDoc} */
        @Override
        public DiseaseProgression getDiseaseProgression()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void setDiseaseTransmission(final DiseaseTransmission diseaseTransmission)
        {
        }

        /** {@inheritDoc} */
        @Override
        public DiseaseTransmission getDiseaseTransmission()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void setDiseaseMonitor(final DiseaseMonitor diseaseMonitor)
        {
        }

        /** {@inheritDoc} */
        @Override
        public DiseaseMonitor getDiseaseMonitor()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, Policy> getAllPolicies()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, Policy> getActivePolicies()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public LocationType getLocationTypeHouse()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Location getLocationWalk()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Location getLocationBike()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Location getLocationCar()
        {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public String getPropertyFilename()
        {
            return null;
        }

    }
}
