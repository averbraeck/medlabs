package nl.tudelft.simulation.medlabs.register;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The PersonRegister class persistently contains that a person has been registered. Registry is once. An example is a model
 * with a single illness and lifelong immunity, where registration of having had the disease is sufficient.
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
public class PersonRegister extends AbstractModelNamed implements RegisterInterface
{
    /** */
    private static final long serialVersionUID = 20220110L;

    /** set with the registered person ids. */
    private Set<Integer> personSet = new LinkedHashSet<>();

    /**
     * Create a register for persons.
     * @param model MedlabsModelInterface; the model
     * @param name String; the name of the register
     */
    public PersonRegister(final MedlabsModelInterface model, final String name)
    {
        super(model, name);
    }

    /** {@inheritDoc} */
    @Override
    public void register(final int personId)
    {
        this.personSet.add(personId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRegistered(final int personId)
    {
        return this.personSet.contains(personId);
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfRegisteredPersons()
    {
        return this.personSet.size();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Person> iterator()
    {
        return new PersonIterator(this.model, this.personSet.iterator());
    }

}
