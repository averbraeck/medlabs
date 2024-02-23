package nl.tudelft.simulation.medlabs.policy;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * Policy class for the medlabs models. Policy has for instance to do with shutting down certain location, and with adapting the
 * activity schedules of persons. The activity schedule changes are induced at midnight of every day by checking the policy.
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

public class Policy
{
    /** the name of the policy to identify it. */
    private final String name;

    /** the model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected MedlabsModelInterface model;

    /**
     * @param model MedlabsModelInterface; the model
     * @param name String; the policy name
     */
    public Policy(final MedlabsModelInterface model, final String name)
    {
        this.model = model;
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

}
