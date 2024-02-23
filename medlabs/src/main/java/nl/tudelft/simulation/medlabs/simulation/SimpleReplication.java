package nl.tudelft.simulation.medlabs.simulation;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;

/**
 * SimpleReplication is a base implementation of a replication for the MEDLABS simulation.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SimpleReplication extends SingleReplication<Double>
{
    /** */
    private static final long serialVersionUID = 20140815L;

    /**
     * Create a new SimpleReplication.
     * @param id String; id of the new SimpleReplication
     * @param startTime double; the start time of the new SimpleReplication in hours
     * @param warmupPeriod double; the warmup period of the new SimpleReplication in hours
     * @param runLength double; the run length of the new SimpleReplication in hours
     * @throws NamingException when the context for the replication cannot be created
     */
    public SimpleReplication(final String id, final double startTime, final double warmupPeriod, final double runLength)
            throws NamingException
    {
        super(id, startTime, warmupPeriod, runLength);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "SimpleReplication []";
    }
}
