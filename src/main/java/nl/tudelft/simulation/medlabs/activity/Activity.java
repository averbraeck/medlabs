package nl.tudelft.simulation.medlabs.activity;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * The abstract Activity is the root class to indicate activities of persons in a day pattern.
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
public abstract class Activity extends AbstractModelNamed
{
    /** */
    private static final long serialVersionUID = 20211230L;

    /** location where the activity takes place (e.g., to calculate statistics). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected LocatorInterface activityLocator;

    /** from location. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected LocatorInterface startLocator;

    /** to location. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected LocatorInterface endLocator;

    /** indicate that this is an activity that should be started even when it is after midnight (default false). */
    private boolean startAfterMidnight = false;

    /**
     * Create an Activity that takes place at one location.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param activityLocator LocatorInterface; the object that can determine the location where the activity takes place (e.g.,
     *            to calculate statistics), usually this is the CurrentLocator
     */
    public Activity(final MedlabsModelInterface model, final String name, final LocatorInterface activityLocator)
    {
        super(model, name);
        Throw.whenNull(activityLocator, "activityLocator cannot be null");
        this.activityLocator = activityLocator;
        this.startLocator = activityLocator;
        this.endLocator = activityLocator;
    }

    /**
     * Create an Activity that has a start locator (almost always the CurrentLocator), a locator to determine the location where
     * movement takes place (a car, bike, public space for on foot, or public transport), and a locator to calculate the arrival
     * location.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param activityLocator LocatorInterface; the locator to determine the activity location
     * @param startLocator LocatorInterface; the locator to determine the start location (usually the CurrentLocator)
     * @param endLocator LocatorInterface; the locator to determine the arrival location
     */
    public Activity(final MedlabsModelInterface model, final String name, final LocatorInterface activityLocator,
            final LocatorInterface startLocator, final LocatorInterface endLocator)
    {
        super(model, name);
        Throw.whenNull(activityLocator, "activityLocator cannot be null");
        Throw.whenNull(startLocator, "startLocator cannot be null");
        Throw.whenNull(endLocator, "endLocator cannot be null");
        this.activityLocator = activityLocator;
        this.startLocator = startLocator;
        this.endLocator = endLocator;
    }

    /**
     * Start the activity for a given person.
     * @param person Person; the person to start the activity for
     */
    public abstract void startActivity(Person person);

    /**
     * Finish the activity for a given person. Gather statistics on the location where the person has been. Start the next
     * activity of the person.
     * @param person Person; the person to execute the activity for
     */
    public abstract void finishActivity(Person person);

    /**
     * Return the activityLocator.
     * @return LocatorInterface; the activityLocator
     */
    public LocatorInterface getActivityLocator()
    {
        return this.activityLocator;
    }
    
    /**
     * Return the activity location for this activity for a given person.
     * @param person Person; the person to return the activity location for
     * @return Location; the location given by the locator specific for this person
     */
    public Location getActivityLocation(final Person person)
    {
        return this.activityLocator.getLocation(person);
    }

    /**
     * Return the start location for this activity for a given person.
     * @param person Person; the person to return the start location for
     * @return Location; the location given by the locator specific for this person
     */
    public Location getStartLocation(final Person person)
    {
        return this.startLocator.getLocation(person);
    }
    /**
     * Return the end location for this activity for a given person.
     * @param person Person; the person to return the end location for
     * @return Location; the location given by the locator specific for this person
     */
    public Location getEndLocation(final Person person)
    {
        return this.endLocator.getLocation(person);
    }

    /**
     * Return the activity duration in hours. When the duration is not fixed, and e.g. dependent on transport activities or
     * social activities, this method should return Double.NaN.
     * @param person Person; the person for whom the activity duration has to be calculated
     * @return double; the duration of the activity, in hours
     */
    public abstract double getDuration(Person person);

    /**
     * Return whether the activity should start after midnight, or be skipped.
     * @return boolean; whether the activity should still start after midnight
     */
    public boolean isStartAfterMidnight()
    {
        return this.startAfterMidnight;
    }

    /**
     * Set whether the activity should start after midnight, or be skipped.
     * @param startAfterMidnight boolean; whether the activity should start after midnight
     */
    public void setStartAfterMidnight(final boolean startAfterMidnight)
    {
        this.startAfterMidnight = startAfterMidnight;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Act[" + getName() + "]";
    }

    /**
     * Provide more information about an activity of a person as a String.
     * @param activityLocation Location; the location of the activity
     * @param duration double; the duration of the activity
     * @return formatted string about the activity
     */
    public String format(final Location activityLocation, final double duration)
    {
        return toString() + "(Loc=" + activityLocation + ", Dur=" + TimeUnit.formatHHHMSS(duration) + ")";
    }

    /**
     * Provide more information about the activity of a person as a String.
     * @param person Person; the person for whom the extended information has to be returned
     * @return formatted string about the activity instance for the person
     */
    public String format(final Person person)
    {
        return format(getActivityLocation(person), getDuration(person));
    }

}
