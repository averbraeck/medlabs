package nl.tudelft.simulation.medlabs.register;

import java.util.Iterator;

import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.Identifiable;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * DateTypeRegister.java.
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
 * @param <T> the type of information that is registered (e.g., about a Vaccine)
 */
public class LastDateTypeRegister<T extends Identifiable> extends AbstractModelNamed implements TypeRegisterInterface<T>
{
    /** */
    private static final long serialVersionUID = 20220110L;

    /**
     * Create a register with a date and a registration type for persons.
     * @param model MedlabsModelInterface; the model
     * @param name String; the name of the register
     */
    public LastDateTypeRegister(final MedlabsModelInterface model, final String name)
    {
        super(model, name);
    }

    /** {@inheritDoc} */
    @Override
    public void register(final Person person, final int type)
    {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRegistered(final Person person)
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRegistered(final Person person, final int type)
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Person> iterator()
    {
        return null; 
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfRegisteredPersons()
    {
        return 0;
    }


}
