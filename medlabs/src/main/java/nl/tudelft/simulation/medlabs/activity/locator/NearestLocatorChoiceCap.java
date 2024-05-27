package nl.tudelft.simulation.medlabs.activity.locator;

import java.util.Map;
import java.util.SortedMap;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.common.MedlabsRuntimeException;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The NearestLocatorChoise locator draws a type of location to return with a probability, and then returns the closest location
 * of that type relative to the current location of the Person. This version of the locator uses a capacity constraint on the
 * location, where persons will avoid the location when it is full.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class NearestLocatorChoiceCap implements LocatorInterface
{
    /** the locator where we start, e.g. work, school or home. */
    private final LocatorInterface startLocator;

    /** the cumulative probabilities and corresponding location types to return. */
    private final SortedMap<Double, LocationType> activityLocationTypeMap;

    /** whether the draw should be reproducible for the person or not. */
    private final boolean reproducible;

    /** local reproducible stream. */
    private StreamInterface stream = null;

    /** Experiment seed. */
    private long seed = 1L;

    /**
     * Construct a locator that draws a LocationType with a probability and returns the nearest location of that type.
     * @param startLocator LocationInterface&lt;T&gt; the starting position to which the other location needs to be found
     * @param activityLocationTypeMap the cumulative probabilities and corresponding location types to return
     * @param reproducible boolean; whether the draw should be reproducible for the person or not
     */
    public NearestLocatorChoiceCap(final LocatorInterface startLocator,
            final SortedMap<Double, LocationType> activityLocationTypeMap, final boolean reproducible)
    {
        Throw.whenNull(startLocator, "startLocator cannot be null");
        Throw.whenNull(activityLocationTypeMap, "activityLocationTypeMap cannot be null");
        Throw.when(activityLocationTypeMap.lastKey() != 1.0, MedlabsRuntimeException.class,
                "NearestLocatorChoice: last (cumulative) probability should be 1.0");
        this.startLocator = startLocator;
        this.activityLocationTypeMap = activityLocationTypeMap;
        this.reproducible = reproducible;
        this.seed = this.activityLocationTypeMap.values().iterator().next().getModel().getDefaultStream().getOriginalSeed()
                + "NearestLocatorChoiceCap".hashCode();
        this.stream = new Java2Random(this.seed);
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation(final Person person)
    {
        Location startLocation = this.startLocator.getLocation(person);
        double prob = this.reproducible ? person.getModel().getReproducibleJava2Random().nextDouble(person.hashCode())
                : person.getModel().getU01().draw();
        for (Map.Entry<Double, LocationType> entry : this.activityLocationTypeMap.entrySet())
        {
            if (prob < entry.getKey())
            {
                if (entry.getValue().getLocationTypeId() == person.getModel().getLocationTypeHouse().getLocationTypeId())
                {
                    return person.getHomeLocation();
                }
                Location loc = entry.getValue().getNearestLocationCap(startLocation);
                if (loc == null)
                    loc = person.getHomeLocation();
                LocationType lt = loc.getLocationType();

                if (lt.getFractionActivities() < 1.0 || lt.getFractionOpen() < 1.0)
                {
                    // person might be forced to go somewhere else or to stay at home
                    if (lt.getFractionOpen() > 0.0)
                    {
                        this.stream.setSeed(this.seed + loc.getId()); // reproducible by nearest location id
                        if (this.stream.nextDouble() < lt.getFractionOpen())
                        {
                            if (lt.getFractionActivities() > 0.0)
                            {
                                this.stream.setSeed(this.seed + person.getId()); // reproducible by person id
                                if (this.stream.nextDouble() < lt.getFractionActivities())
                                {
                                    loc.addReservation(person);
                                    return loc; // can still go to the nearest location
                                }
                            }
                        }
                    }

                    LocationType alt = lt.getAlternativeLocationType();
                    if (person.getModel().getLocationTypeHouse().getLocationTypeId() == alt.getLocationTypeId())
                        return person.getHomeLocation();
                    loc = new NearestLocatorCap(new CurrentLocator(), alt).getLocation(person);
                    if (loc == null)
                        loc = person.getHomeLocation();
                }

                // location is open
                loc.addReservation(person);
                return loc;
            }
        }
        throw new MedlabsRuntimeException("NearestLocatorChoice.getLocation -- did not find a LocationType");
    }

}
