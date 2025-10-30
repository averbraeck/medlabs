package nl.tudelft.simulation.medlabs.person;

import nl.tudelft.simulation.medlabs.AbstractModelIdNamed;
import nl.tudelft.simulation.medlabs.ModelIdentifiable;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * PersonType contains information about types of persons (social roles).
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
public class PersonType extends AbstractModelIdNamed implements ModelIdentifiable
{
    /** */
    private static final long serialVersionUID = 1L;
    
    /** the number of persons of this type. */
    private int numberPersons;

    /**
     * Generate a PersonType and register it in the model.
     * @param model MedlabsModelInterface; the model
     * @param id int; the id number of the person type
     * @param personClass Class&lt;? extends Person&gt;; the person class, to retrieve the simple class name
     */
    public PersonType(final MedlabsModelInterface model, final int id, final Class<? extends Person> personClass)
    {
        super(model, id, personClass.getSimpleName());
    }

    /**
     * Return the number of persons of this type.
     * @return int; the number of persons of this type
     */
    public int getNumberPersons()
    {
        return this.numberPersons;
    }

    /**
     * Increase the number of persons of this type. 
     */
    public void incNumberPersons()
    {
        this.numberPersons++;
    }

    /**
     * Decrease the number of persons of this type. 
     */
    public void decNumberPersons()
    {
        this.numberPersons++;
    }

}
