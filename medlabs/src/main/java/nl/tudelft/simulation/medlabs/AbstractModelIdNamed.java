package nl.tudelft.simulation.medlabs;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * AsbtractModelNamed can be used as a superclass for classes with a name and a reference to the model.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractModelIdNamed implements ModelNamed, ModelIdentifiable
{
    /** */
    private static final long serialVersionUID = 20211229L;

    /** the reference to the model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected MedlabsModelInterface model;

    /** the unique id of the object. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected int id;

    /** the name of the object. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected String name;

    /**
     * Create a named, identifiable, model aware object.
     * @param model MedlabsModelInterface; the reference to the model
     * @param id int; the unique id of the object
     * @param name String; the name of the object
     */
    public AbstractModelIdNamed(final MedlabsModelInterface model, final int id, final String name)
    {
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(name, "name cannot be null");
        this.model = model;
        this.id = id;
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public MedlabsModelInterface getModel()
    {
        return this.model;
    }

    /** {@inheritDoc} */
    @Override
    public int getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
        AbstractModelIdNamed other = (AbstractModelIdNamed) obj;
        if (this.id != other.id)
            return false;
        if (this.name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals(other.name))
            return false;
        return true;
    }

}
