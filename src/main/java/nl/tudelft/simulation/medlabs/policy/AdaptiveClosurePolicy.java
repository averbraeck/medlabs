package nl.tudelft.simulation.medlabs.policy;

import java.util.Collection;

import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * Adaptive closure policy class for the medlabs models. An adaptive policy shuts down certain location under a condition.
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

public abstract class AdaptiveClosurePolicy extends ClosurePolicy
{
    /**
     * @param model MedlabsModelInterface; the model
     * @param name String; the policy name
     * @param closureLocations the locations to close
     */
    public AdaptiveClosurePolicy(final MedlabsModelInterface model, final String name,
            final Collection<LocationType> closureLocations)
    {
        super(model, name, closureLocations);
        double nextMidnight = 24.0 * Math.ceil(this.model.getSimulator().getSimulatorTime() / 24.0);
        this.model.getSimulator().scheduleEventAbs(nextMidnight, this, "monitor", null);
    }

    /**
     * The monitoring method that is called every night at midnight.
     */
    protected void monitor()
    {
        monitorAndOpenOrClose();
        this.model.getSimulator().scheduleEventRel(24.0, this, "monitor", null);
    }

    /**
     * The monitoring method that is called every night at midnight, and that allows to check for certain conditions in the
     * model (number of people hospitalized, number of people in the ICU, etc., and can use the "open" and "close" methods of
     * the closureLocations of this policy to implement the policy measures.
     */
    protected abstract void monitorAndOpenOrClose();

}
