package nl.tudelft.simulation.medlabs.activity;

import org.djunits.Throw;

import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.common.MedlabsRuntimeException;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The FixedDurationActivity is an Activity for a person that takes a fixed amount of time.
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
public class FixedDurationActivity extends AbstractDurationActivity
{
    /** */
    private static final long serialVersionUID = 20140505L;

    /** the duration of the activity. */
    private final double duration;

    /**
     * Create an activity with a fixed duration. Duration can be NaN which will have the person skip this activity.
     * @param model MedlabsModelInterface; pointer to the model for retrieving simulator and other relevant information
     * @param name String; the name of the activity
     * @param locator LocatorInterface&lt;T&gt;; the locator that returns where the activity takes place
     * @param duration double; the duration of the activity (in hours)
     */
    public FixedDurationActivity(final MedlabsModelInterface model, final String name, final LocatorInterface locator,
            final double duration)
    {
        super(model, name, locator);
        Throw.when(Double.isNaN(duration), MedlabsRuntimeException.class, "duration cannot be NaN");
        this.duration = duration;
    }

    /** {@inheritDoc} */
    @Override
    public double getDuration(final Person person)
    {
        return this.duration;
    }

}
