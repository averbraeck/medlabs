package nl.tudelft.simulation.medlabs.person;

import nl.tudelft.simulation.medlabs.ModelIdentifiable;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * The Agent is the base entity in the simulation model. To avoid static variables, the Agent does have a reference to the
 * model, so it can look up activities, week patterns, diseases, and locations. An agent is identified by a unique id (int). Ids
 * are used in arrays, so sequential numbering of the id from 0 onward is preferred.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * EPILABS project (Epidemic disease modeling with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Agent implements ModelIdentifiable
{
    /** the model to look up the map, locations, etc. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected MedlabsModelInterface model;

    /** unique id of this agent. */
    private final int id;

    /**
     * Create a new Agent.
     * @param model MedlabsModelInterface; the model
     * @param id int; the id of this agent
     */
    public Agent(final MedlabsModelInterface model, final int id)
    {
        this.model = model;
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public int getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public MedlabsModelInterface getModel()
    {
        return this.model;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Agent other = (Agent) obj;
        if (this.id != other.id)
            return false;
        return true;
    }

}
