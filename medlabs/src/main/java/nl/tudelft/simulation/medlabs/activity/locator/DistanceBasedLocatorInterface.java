package nl.tudelft.simulation.medlabs.activity.locator;

import org.apache.poi.ss.formula.eval.NotImplementedException;

import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * DistanceBasedLocatorInterface.java.
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
public interface DistanceBasedLocatorInterface extends LocatorInterface
{

    /** {@inheritDoc} */
    @Override
    default Location getLocation(final Person person)
    {
        throw new NotImplementedException("DistanceBasedLocators do not implement getLocation(person)");
    }

    /**
     * Provide a location based on the distance between start and end, used for transport.
     * @param person Person; the person for whom the location is asked (e.g., with/without car, age, ...)
     * @param startLocation Location; the start location
     * @param endLocation Location; the end location
     * @return Location; the location, e.g. a means of transport
     */
    Location getLocation(Person person, Location startLocation, Location endLocation);
}
