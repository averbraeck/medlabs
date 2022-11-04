package nl.tudelft.simulation.medlabs.activity.locator;

import java.util.Map;
import java.util.SortedMap;

import org.djunits.Throw;

import nl.tudelft.simulation.medlabs.common.MedlabsRuntimeException;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The RandomLocatorChoise locator draws a type of location to return with a probability, and then returns a random location of
 * that type within a certain distance, e.g., a restaurant within a 2 kilometer radius.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RandomLocatorChoice implements LocatorInterface
{
    /** the locator where we start, e.g. work, school or home */
    private final LocatorInterface startLocator;

    /** the cumulative probabilities and corresponding location types to return. */
    private final SortedMap<Double, LocationType> activityLocationTypeMap;

    /** the maximum distance in meters. */
    private final double maxDistanceM;

    /** whether the draw should be reproducible for the person or not. */
    private final boolean reproducible;

    /**
     * Construct a locator that draws a LocationType with a probability and returns a random location of that type within a
     * certain radius.
     * @param startLocator LocationInterface&lt;T&gt; the starting position to which the other location needs to be found
     * @param activityLocationTypeMap the cumulative probabilities and corresponding location types to return
     * @param maxDistanceM double the maximum distance in meters
     * @param reproducible boolean; whether the draw should be reproducible for the person or not
     */
    public RandomLocatorChoice(final LocatorInterface startLocator,
            final SortedMap<Double, LocationType> activityLocationTypeMap, final double maxDistanceM,
            final boolean reproducible)
    {
        Throw.whenNull(startLocator, "startLocator cannot be null");
        Throw.whenNull(activityLocationTypeMap, "activityLocationTypeMap cannot be null");
        Throw.when(activityLocationTypeMap.lastKey() != 1.0, MedlabsRuntimeException.class,
                "NearestLocatorChoice: last (cumulative) probability should be 1.0");
        this.startLocator = startLocator;
        this.activityLocationTypeMap = activityLocationTypeMap;
        this.maxDistanceM = maxDistanceM;
        this.reproducible = reproducible;
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation(final Person person)
    {
        MedlabsModelInterface model = person.getModel();
        Location startLocation = this.startLocator.getLocation(person);
        double prob = this.reproducible ? person.getModel().getReproducibleJava2Random().nextDouble(person.hashCode() + 1)
                : person.getModel().getU01().draw();
        for (Map.Entry<Double, LocationType> entry : this.activityLocationTypeMap.entrySet())
        {
            if (prob < entry.getKey())
            {
                LocationType lt = entry.getValue();
                if (lt.getLocationTypeId() == person.getModel().getLocationTypeHouse().getLocationTypeId())
                {
                    return person.getHomeLocation();
                }

                if (lt.getFractionActivities() < 1.0 || lt.getFractionOpen() < 1.0)
                {
                    LocationType alt = lt.getAlternativeLocationType();
                    if (person.getModel().getLocationTypeHouse().getLocationTypeId() == alt.getLocationTypeId())
                        return person.getHomeLocation();
                    return new NearestLocator(new CurrentLocator(), alt).getLocation(person);
                }
                
                Location[] locations = lt.getLocationArrayMaxDistanceM(startLocation, this.maxDistanceM);
                if (locations.length == 0)
                {
                    return lt.getNearestLocation(startLocation);
                }
                else
                {
                    // return locations[MedlabsModel.randomUniform(locations.length)];
                    int index = this.reproducible
                            ? model.getReproducibleJava2Random().nextInt(0, locations.length, (person.hashCode() + 2))
                            : model.getRandomStream().nextInt(0, locations.length);
                    if (index >= locations.length)
                    {
                        index = locations.length - 1;
                    }
                    return locations[index];
                }

            }
        }
        throw new MedlabsRuntimeException("RandomLocatorChoice.getLocation -- did not find a LocationType");
    }

}
