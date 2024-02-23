package nl.tudelft.simulation.medlabs.activity;

import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * Travel activity based on distance. The activity takes place in a large area where probability for transmission is zero. It
 * will either use the "walk", "bike" or "car" location, dependent on distance.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TravelActivityDistanceBased extends TravelActivity
{
    /** */
    private static final long serialVersionUID = 20140505L;

    /**
     * Create an Activity type for a single person using the bicycle to get from A to B.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param travelLocator
     * @param startLocator
     * @param endLocator
     */
    public TravelActivityDistanceBased(final MedlabsModelInterface model, final String name,
            final LocatorInterface travelLocator, final LocatorInterface startLocator, final LocatorInterface endLocator)
    {
        super(model, name, travelLocator, startLocator, endLocator);
    }

    /** {@inheritDoc} */
    @Override
    public double getDuration(final Person person)
    {
        return Double.NaN;
    }
    
    /** {@inheritDoc} */
    @Override
    protected double getDuration(final Person person, final Location startLocation, final Location endLocation)
    {
        double distanceM = startLocation.distanceM(endLocation);
        if (distanceM < 1000)
        {
            return TimeUnit.convert(distanceM, TimeUnit.SECOND); // 1 m/s
        }
        else if (distanceM < 5000)
        {
            return TimeUnit.convert(distanceM / 3.0, TimeUnit.SECOND); // 3 m/s
        }
        else
        {
            return TimeUnit.convert(distanceM / 11.1, TimeUnit.SECOND); // 11.1 m/s
        }
    }

}
