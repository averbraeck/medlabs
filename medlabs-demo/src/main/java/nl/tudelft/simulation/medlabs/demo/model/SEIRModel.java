package nl.tudelft.simulation.medlabs.demo.model;

import java.util.HashMap;
import java.util.Map;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.medlabs.demo.person.Worker;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.location.animation.defaults.HouseAnimation;
import nl.tudelft.simulation.medlabs.model.AbstractMedlabsModel;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.index.IdxPerson;
import nl.tudelft.simulation.medlabs.properties.Properties;
import nl.tudelft.simulation.medlabs.simulation.SimpleDevsSimulatorInterface;

/**
 * SEIRModel.java.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * code is part of the SEIR project (Health Emergency Response in Interconnected Systems), which builds on the MEDLABS project.
 * The simulation tools are aimed at providing policy analysis tools to predict and help contain the spread of epidemics. They
 * make use of the DSOL simulation engine and the agent-based modeling formalism. This software is licensed under the BSD
 * license. See license.txt in the main project.
 * </p>
 * @author <a href="https://www.linkedin.com/in/mikhailsirenko">Mikhail Sirenko</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SEIRModel extends AbstractMedlabsModel
{
    /** */
    private static final long serialVersionUID = 20200919L;

    /** the base path for the input files. */
    private String basePath;

    /** the extra person properties. */
    private Properties properties;

    /** the types of persons for the week pattern change. */
    private Map<Class<? extends Person>, String> personTypes = new HashMap<>();

    /** the cached extent. */
    private Bounds2d extent = null;

    /** The location type for a house where persons are generated. */
    private final LocationType locationTypeHouse;

    /**
     * Construct the model.
     * @param simulator SimpleDevsSimulatorInterface; the simulator
     * @param propertyFilename String; the path of the property file name to use
     */
    public SEIRModel(final SimpleDevsSimulatorInterface simulator, final String propertyFilename)
    {
        super(simulator, propertyFilename);
        this.locationTypeHouse =
                new LocationType(this, (byte) 0, "house", Location.class, HouseAnimation.class, true, true, 1.0, false, 0.25, 1.0);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        super.constructModel();
        makePersonTypes();

        if (!isInteractive())
        {
            getSimulator().scheduleEventNow(this, "hourTick", null);
        }
    }

    protected void hourTick()
    {
        int hour = (int) Math.round(getSimulator().getSimulatorTime());
        if (hour % 24 == 0)
        {
            System.out.print("\nDay " + (hour / 24) + "  ");
        }
        else
        {
            System.out.print(".");
        }
        getSimulator().scheduleEventRel(1.0, this, "hourTick", null);
    }

    /** {@inheritDoc} */
    @Override
    protected void constructModelFromSource()
    {
        new ConstructSEIRModel(this);
        // XXX: hack -- this is not how we should do this...
        this.properties = new Properties(IdxPerson.class, getPersonMap().size());
    }

    @Override
    public void checkChangeActivityPattern(final Person person)
    {
        // nothing to do
    }

    /**
     * Make the person types for the week pattern names.
     */
    private void makePersonTypes()
    {
        this.personTypes.put(Worker.class, "worker");
    }

    @Override
    public Properties getPersonProperties()
    {
        return this.properties;
    }

    /** {@inheritDoc} */
    @Override
    protected void extendInputParameterMap() throws InputParameterException
    {
        InputParameterMap root = this.inputParameterMap;

        InputParameterMap settingsMap = new InputParameterMap("settings", "Model Settings", "Model parameters", 1.2);
        settingsMap.add(new InputParameterInteger("NumberPersons", "number of persons in the model", "(between 1 and 50000)",
                1000, 1, 50000, "%d", 1.0));
        settingsMap.add(new InputParameterInteger("NumberWorkplaces", "number of workplaces in the model",
                "(between 1 and 50000", 10, 1, 50000, "%d", 2.0));
        settingsMap.add(new InputParameterInteger("NumberSublocations", "number of sublocations in each workplace",
                "(between 1 and 10000)", 10, 1, 10000, "%d", 3.0));
        settingsMap.add(new InputParameterDouble("WorkplaceSize", "total workplace size in m2", "(m2, between 1 and 1,000,000)",
                100.0, 1.0, 1E6, true, true, "%f", 4.0));
        settingsMap.add(new InputParameterInteger("NumberInfected", "number of people infected at t=0",
                "(between 0 and 10000, can be 0)", 0, 0, 10000, "%d", 5.0));
        root.add(settingsMap);

        InputParameterMap seirTransmissionMap =
                new InputParameterMap("SEIR", "SEIR Transmission", "SEIR Transmission parameters", 1.5);

        seirTransmissionMap.add(new InputParameterDouble("contagiousness", "contagiousness as calculated from exp-formula",
                "value between 0.0 and 1.0", 0.5, 0.0, 1.0, true, true, "%f", 1.0));
        seirTransmissionMap.add(new InputParameterDouble("beta", "initial personal protection factor (masks, other protection)",
                "value between 0.0 and 1.0", 1.0, 0.0, 1.0, true, true, "%f", 1.5));

        seirTransmissionMap.add(new InputParameterDouble("t_e_min", "first day of contagiousness of an exposed person (days)",
                "Triangular.min, time in days", 3.0, 0.0, 60.0, true, true, "%f", 2.0));
        seirTransmissionMap.add(new InputParameterDouble("t_e_mode", "peak day of contagiousness of an exposed person (days)",
                "Triangular.mode, time in days", 7.0, 0.0, 60.0, true, true, "%f", 3.0));
        seirTransmissionMap.add(new InputParameterDouble("t_e_max", "last day of contagiousness of an exposed person (days)",
                "Triangular.max, time in days", 14.0, 0.0, 60.0, true, true, "%f", 4.0));

        seirTransmissionMap.add(new InputParameterDouble("calculation_threshold",
                "threshold for the transmission contact calculation (sec)",
                "Below this contact duration, no infections will be calculated", 60, 0.0, 3600.0, true, true, "%f", 5.0));

        root.add(seirTransmissionMap);
    }

    /** {@inheritDoc} */
    @Override
    public LocationType getLocationTypeHouse()
    {
        return this.locationTypeHouse;
    }

    /**
     * @return the basePath
     */
    public String getBasePath()
    {
        return this.basePath;
    }

    /**
     * @param basePath the basePath to set
     */
    public void setBasePath(final String basePath)
    {
        this.basePath = basePath;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds2d getExtent()
    {
        if (this.extent == null)
        {
            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;
            for (Location location : this.getLocationMap().valueCollection())
            {
                double x = location.getLongitude();
                double y = location.getLatitude();
                if (x == 0.0 || y == 0.0)
                    continue;
                if (x < minX)
                    minX = x;
                if (x > maxX)
                    maxX = x;
                if (y < minY)
                    minY = y;
                if (y > maxY)
                    maxY = y;
            }
            this.extent = new Bounds2d(minX, maxX, minY, maxY);
        }
        return this.extent;
    }
}
