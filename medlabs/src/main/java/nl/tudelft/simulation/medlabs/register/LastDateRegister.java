package nl.tudelft.simulation.medlabs.register;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The LastDateRegister class persistently contains the date that a person has been registered. Only the last date is kept, and
 * it is not an error to register a person multiple times. An example is an official test where the last day of the test is
 * kept.
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
public class LastDateRegister extends AbstractModelNamed implements RegisterInterface
{
    /** */
    private static final long serialVersionUID = 20220110L;

    /** map with the registered person ids and dates of registration. */
    private Map<Integer, Short> personMap = new LinkedHashMap<>();

    /**
     * Create a register for persons.
     * @param model MedlabsModelInterface; the model
     * @param name String; the name of the register
     */
    public LastDateRegister(final MedlabsModelInterface model, final String name)
    {
        super(model, name);
    }

    /** {@inheritDoc} */
    @Override
    public void register(final int personId)
    {
        this.personMap.put(personId, this.model.getSimulator().getSimulatorTime().shortValue());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRegistered(final int personId)
    {
        return this.personMap.containsKey(personId);
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfRegisteredPersons()
    {
        return this.personMap.size();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Person> iterator()
    {
        return new PersonIterator(this.model, this.personMap.keySet().iterator());
    }

    /**
     * Return the last registration date for this person, measured in days since the start of the simulation, or -1 if the
     * person has not yet been registered.
     * @param person Person; the person to look up
     * @return the last registration date for this person or -1 if the person is not registered
     */
    public int lastRegistrationDate(final Person person)
    {
        return lastRegistrationDate(person.getId());
    }

    /**
     * Return the last registration date for the person with this id, measured in days since the start of the simulation, or -1
     * if the person has not yet been registered.
     * @param personId int; the id of the person to look up
     * @return the last registration date for this person or -1 if the person is not registered
     */
    public int lastRegistrationDate(final int personId)
    {
        Short day = this.personMap.get(personId);
        return day == null ? -1 : day.intValue();
    }

}
