package nl.tudelft.simulation.medlabs.activity.locator;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The RandomLocator returns a random location of a certain type within a certain distanc, e.g., a restaurant within a 2
 * kilometer radius.
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
public class RandomLocator implements LocatorInterface
{
    /** the locator where we start, e.g. work, school or home */
    private final LocatorInterface startLocator;

    /** the type of location to return, e.g. "playground" */
    private final LocationType activityLocationType;

    /** the maximum distance. */
    private final double maxDistanceM;

    /** whether the draw should be reproducible for the person or not. */
    private final boolean reproducible;

    /** local reproducible stream. */
    private StreamInterface stream = null;

    /** Experiment seed. */
    private long seed = 1L;

    /**
     * Construct a locator that draws a random Location of a certain type within a search radius.
     * @param startLocator LocationInterface&lt;T&gt; the starting position to which the other location needs to be found
     * @param activityLocationType the cumulative probabilities and corresponding location types to return
     * @param maxDistanceM double; the search radius in meters
     * @param reproducible boolean; whether the draw should be reproducible for the person or not
     */
    public RandomLocator(final LocatorInterface startLocator, final LocationType activityLocationType,
            final double maxDistanceM, final boolean reproducible)
    {
        Throw.whenNull(startLocator, "startLocator cannot be null");
        Throw.whenNull(activityLocationType, "activityLocationType cannot be null");
        this.startLocator = startLocator;
        this.activityLocationType = activityLocationType;
        this.maxDistanceM = maxDistanceM;
        this.reproducible = reproducible;
        this.seed = this.activityLocationType.getModel().getDefaultStream().getOriginalSeed() + "RandomLocator".hashCode();
        this.stream = new Java2Random(this.seed);
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation(final Person person)
    {
        // ResultWriter.logXls(person.getModel().getSimulator().getSimulatorTime() + "\t" + person.getClass().getSimpleName()
        // + "\t" + person.getCurrentActivity() + "\tRL\t" + person.getId());
        MedlabsModelInterface model = person.getModel();
        Location startLocation = this.startLocator.getLocation(person);

        Location[] locations = this.activityLocationType.getLocationArrayMaxDistanceM(startLocation, this.maxDistanceM);
        Location loc = null;
        if (locations.length == 0)
        {
            loc = this.activityLocationType.getNearestLocation(startLocation);
        }
        else
        {
            int index = this.reproducible ? model.getReproducibleJava2Random().nextInt(0, locations.length, (person.hashCode()))
                    : model.getRandomStream().nextInt(0, locations.length);
            if (index >= locations.length)
            {
                index = locations.length - 1;
            }
            loc = locations[index];
        }

        if (this.activityLocationType.getFractionActivities() < 1.0 || this.activityLocationType.getFractionOpen() < 1.0)
        {
            // person might be forced to go somewhere else or to stay at home
            if (this.activityLocationType.getFractionOpen() > 0.0)
            {
                this.stream.setSeed(this.seed + loc.getId()); // reproducible by location id
                if (this.stream.nextDouble() < this.activityLocationType.getFractionOpen())
                {
                    if (this.activityLocationType.getFractionActivities() > 0.0)
                    {
                        this.stream.setSeed(this.seed + person.getId()); // reproducible by person id
                        if (this.stream.nextDouble() < this.activityLocationType.getFractionActivities())
                        {
                            loc.addReservation(person);
                            return loc; // can still go to the chosen location
                        }
                    }
                }
            }
            
            LocationType alt = this.activityLocationType.getAlternativeLocationType();
            if (person.getModel().getLocationTypeHouse().getLocationTypeId() == alt.getLocationTypeId())
                return person.getHomeLocation();
            return new NearestLocator(new CurrentLocator(), alt).getLocation(person);
        }
        
        // location is open
        loc.addReservation(person);
        return loc;
    }

}
