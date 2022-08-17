package nl.tudelft.simulation.medlabs.activity;

import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The FlexibleDurationActivity is an Activity for a person that takes a flexible amount of time. An example extension of this
 * abstract class is the StochasticDurationActivity where the flexible duration is provided as a distribution function.
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
public abstract class FlexibleDurationActivity extends AbstractDurationActivity
{
    /** */
    private static final long serialVersionUID = 20140505L;

    /** the estimated duration of the activity to take into account for planning purposes. */
    private final double estimatedDuration;

    /**
     * Create an activity with a fixed duration. Duration can be NaN which will have the person skip this activity. Create an
     * Activity type.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param locator LocatorInterface&lt;T&gt;; the locator that returns where the activity takes place
     * @param estimatedDuration double; the estimated duration of the activity (in hours), e.g. for planning purposes
     */
    public FlexibleDurationActivity(final MedlabsModelInterface model, final String name, final LocatorInterface locator,
            final double estimatedDuration)
    {
        super(model, name, locator);
        this.estimatedDuration = estimatedDuration;
    }

    /**
     * @param person the person for whom the estimated time should be calculated
     * @return the estimated duration
     */
    public double getEstimatedDuration(final Person person)
    {
        return this.estimatedDuration;
    }

}
