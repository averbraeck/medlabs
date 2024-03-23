package nl.tudelft.simulation.medlabs.simulation;

import java.io.Serializable;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;

/**
 * SimpleDevsSimulator is a simulator that is aimed at executing TinySimEvents where time is stored as a double, with a unit
 * value in hours.
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
public class SimpleDevsSimulator extends DevsSimulator<Double> implements SimpleDevsSimulatorInterface
{
    /** */
    private static final long serialVersionUID = 20200918L;

    /** Counter for replication. */
    private int lastReplication = 0;

    /**
     * Initialize the Simulator.
     * @param id Serializable; the id that is used in events and statistics
     */
    public SimpleDevsSimulator(final Serializable id)
    {
        super(id);
    }

    /** {@inheritDoc} */
    @Override
    public void endReplication()
    {
        super.endReplication();
        System.exit(0);
    }

    /** {@inheritDoc} */
    @Override
    public byte getWeekDay()
    {
        return (byte) (Math.floor(getSimulatorTime() / 24) % 7);
    }

    /** {@inheritDoc} */
    @Override
    public void scheduleEventRel(final double delay, final TimeUnit unit, final Object target, final String method,
            final Object[] args)
    {
        scheduleEventRel(TimeUnit.convert(delay, unit), target, method, args);
    }

    /** {@inheritDoc} */
    @Override
    public void scheduleEventAbs(final double time, final TimeUnit unit, final Object target, final String method,
            final Object[] args)
    {
        scheduleEventAbs(TimeUnit.convert(time, unit), target, method, args);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(final double startTime, final double warmupPeriod, final double runLength,
            final SimpleModelInterface model, final long seed) throws SimRuntimeException, NamingException
    {
        initialize(startTime, warmupPeriod, runLength, model, ++this.lastReplication, seed);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(final double startTime, final double warmupPeriod, final double runLength,
            final SimpleModelInterface model, final int replicationNr, final long seed)
            throws SimRuntimeException, NamingException
    {
        setErrorStrategy(ErrorStrategy.WARN_AND_PAUSE);
        SimpleReplication newReplication = new SimpleReplication("rep" + replicationNr, startTime, warmupPeriod, runLength);
        super.initialize(model, newReplication);
    }

    /** {@inheritDoc} */
    @Override
    public SimpleReplication getReplication()
    {
        return (SimpleReplication) super.getReplication();
    }

}
