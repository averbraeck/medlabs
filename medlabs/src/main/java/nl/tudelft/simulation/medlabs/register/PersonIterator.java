package nl.tudelft.simulation.medlabs.register;

import java.util.Iterator;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * PersonIterator iterates over Persons, where their ids have been stored.
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
public class PersonIterator implements Iterator<Person>
{
    /** the model to look up the persons. */
    private final MedlabsModelInterface model;

    /** the person ids for the iterator. */
    private final Iterator<Integer> personIds;

    /**
     * Construct an iterator for persons based on their ids.
     * @param model MedlabsModelInterface; the model
     * @param personIds Set&lt;Integer&gt;; the person ids
     */
    public PersonIterator(final MedlabsModelInterface model, final Iterator<Integer> personIds)
    {
        this.model = model;
        this.personIds = personIds;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return this.personIds.hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public Person next()
    {
        return this.model.getPersonMap().get(this.personIds.next());
    }

}
