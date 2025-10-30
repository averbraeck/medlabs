package nl.tudelft.simulation.medlabs.activity;

import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.TinySimEvent;

/**
 * The AbstractDurationActivity contains the logic for an Activity that takes a predictable amount of time at a location.
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
public abstract class AbstractDurationActivity extends Activity
{
    /** */
    private static final long serialVersionUID = 20140505L;

    /**
     * Create an activity with a fixed duration. Duration can be NaN which will have the person skip this activity.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param locator LocatorInterface&lt;T&gt;; the locator that returns where the activity takes place
     */
    public AbstractDurationActivity(final MedlabsModelInterface model, final String name, final LocatorInterface locator)
    {
        super(model, name, locator);
    }

    /** {@inheritDoc} */
    @Override
    public void startActivity(final Person person)
    {
        Location activityLocation = this.getActivityLocation(person);
        Location oldLocation = person.getCurrentLocation();
        double activityDuration = this.getDuration(person);

        if (Double.isNaN(activityDuration) || activityDuration <= 0)
        {
            // should we remove the person from the old location?
            oldLocation.removePerson(person);
            person.endActivity(); // current activity is a void activity
            return;
        }

        if (activityDuration > 24.0)
        {
            System.err.println("duration > 24.0 -- Person: " + person.toString() + ", day=" + this.model.getWeekday()
                    + ", activity: " + format(activityLocation, activityDuration));
            System.err.println("Activity location: " + activityLocation.toString() + "\n");
        }

        if (oldLocation.getId() != activityLocation.getId())
        {
            oldLocation.removePerson(person);
            activityLocation.addPerson(person);
            person.setCurrentLocation(activityLocation);
        }
        
        // we have to report the time here since we don't know anymore when the activity started when it ends 
        activityLocation.getLocationType().reportActivityDuration(activityDuration);
        
        this.model.getSimulator()
                .scheduleEvent(new TinySimEvent(this.model.getSimulator().getSimulatorTime() + activityDuration, this,
                        "finishActivity", new Object[] { person }));
    }

    /** {@inheritDoc} */
    @Override
    public void finishActivity(final Person person)
    {
        // no change of location
        person.endActivity();
    }

}
