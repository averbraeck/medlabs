package nl.tudelft.simulation.medlabs.activity;

import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.TinySimEvent;

/**
 * The TravelActivity is an activity that has a location through a Locator (walk, bike, car, bus, metro, etc.) where the move
 * takes place, and a startLocator and endLocator. E.g., get from the house to the closest supermarket by bike.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
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
public abstract class TravelActivity extends Activity
{
    /** */
    private static final long serialVersionUID = 20211230L;

    /**
     * Create a travel activity that has a start locator (almost always the CurrentLocator), a locator to determine the location
     * where movement takes place (a car, bike, public space for on foot, or a public transport means), and a locator to
     * calculate the arrival location.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param travelLocator LocatorInterface; the locator to determine the location in which travel takes place
     * @param startLocator LocatorInterface; the locator to determine the start location (usually the CurrentLocator)
     * @param endLocator LocatorInterface; the locator to determine the arrival location
     */
    public TravelActivity(final MedlabsModelInterface model, final String name, final LocatorInterface travelLocator,
            final LocatorInterface startLocator, final LocatorInterface endLocator)
    {
        super(model, name, travelLocator, startLocator, endLocator);
    }

    /**
     * Calculate the travel time based on given locations.
     * @param person Person; the person to calculate the duration for
     * @param startLocation Location; the origin location
     * @param endLocation Location; the destination location
     * @return double; the travel time in hours
     */
    protected abstract double getDuration(Person person, Location startLocation, Location endLocation);

    /** {@inheritDoc} */
    @Override
    public void startActivity(final Person person)
    {
        Location travelLocation = this.getActivityLocation(person);
        Location oldLocation = person.getCurrentLocation();
        Location toLocation = getEndLocation(person);
        double activityDuration = this.getDuration(person, oldLocation, toLocation);

        if (Double.isNaN(activityDuration) || activityDuration <= 0)
        {
            if (oldLocation.getId() != toLocation.getId())
            {
                oldLocation.removePerson(person);
                toLocation.addPerson(person);
                person.setCurrentLocation(toLocation);
            }
            person.endActivity(); // current activity is a void or instantaneous activity
            return;
        }

        if (activityDuration > 24.0)
        {
            System.err.println("duration > 24.0 -- Person: " + person.toString() + ", day=" + this.model.getWeekday()
                    + ", activity: " + format(travelLocation, activityDuration));
            System.err.println("Activity location: " + travelLocation.toString() + "\n");
        }

        if (oldLocation.getId() != travelLocation.getId())
        {
            oldLocation.removePerson(person);
            travelLocation.addPerson(person);
            person.setCurrentLocation(travelLocation);
        }

        // we have to report the time here since we don't know anymore when the activity started when it ends
        travelLocation.getLocationType().reportActivityDuration(activityDuration);

        this.model.getSimulator()
                .scheduleEvent(new TinySimEvent(this.model.getSimulator().getSimulatorTime() + activityDuration, this, this,
                        "finishTravelActivity", new Object[] { person, travelLocation, toLocation }));
    }

    /** {@inheritDoc} */
    @Override
    public void finishActivity(final Person person)
    {
        person.endActivity();
    }

    /**
     * @param person Person; the person who traveled
     * @param travelLocation Location; the current location of the person (bus, metro, walk, bike, car, etc.)
     * @param toLocation Location; the location at the end of the travel activity
     */
    protected void finishTravelActivity(final Person person, final Location travelLocation, final Location toLocation)
    {
        travelLocation.removePerson(person);
        toLocation.addPerson(person);
        person.setCurrentLocation(toLocation);
        finishActivity(person);
    }

}
