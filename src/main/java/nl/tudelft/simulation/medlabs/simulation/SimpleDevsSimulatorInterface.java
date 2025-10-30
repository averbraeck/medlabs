package nl.tudelft.simulation.medlabs.simulation;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * SimpleDevsSimulatorInterface is a simulator that accepts double time for relative and absolute simulation events. The time is
 * given in hours, to make scheduling during the day as natural as possible.
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
public interface SimpleDevsSimulatorInterface extends DevsSimulatorInterface<Double>
{
    /**
     * Return the weekday in the current simulation; 0 is Monday, 6 = Sunday.
     * @return byte; the weekday in the current simulation; 0 is Monday, 6 = Sunday
     */
    byte getWeekDay();

    /**
     * Schedule a simulation event with a relative time delay.
     * @param delay double; the delay in the given time unit
     * @param unit TimeUnit; the unit in which the delay is specified
     * @param target Object; the target object on which the method execution is scheduled
     * @param method String; the name of the method to execute within the target object
     * @param args Object[]; the arguments for the method call
     */
    void scheduleEventRel(double delay, TimeUnit unit, Object target, String method, Object[] args);

    /**
     * Schedule a simulation event on an absolute time.
     * @param time double; the scheduled time in the given time unit
     * @param unit TimeUnit; the unit in which the time is specified
     * @param target Object; the target object on which the method execution is scheduled
     * @param method String; the name of the method to execute within the target object
     * @param args Object[]; the arguments for the method call
     */
    void scheduleEventAbs(double time, TimeUnit unit, Object target, String method, Object[] args);

    /**
     * Initialize a simulation engine without animation; the easy way. PauseOnError is set to true;
     * @param startTime double; the start time of the simulation in hours
     * @param warmupPeriod double; the warm up period of the simulation in hours
     * @param runLength double; the duration of the simulation in hours
     * @param model SimpleModelInterface; the simulation to execute
     * @param seed long; seed for the standard Random Number Generator
     * @throws SimRuntimeException when e.g., warmupPeriod is larger than runLength
     * @throws NamingException when the context for the replication cannot be created
     */
    void initialize(double startTime, double warmupPeriod, double runLength, SimpleModelInterface model, long seed)
            throws SimRuntimeException, NamingException;

    /**
     * Initialize a simulation engine without animation and prescribed replication number; the easy way. PauseOnError is set to
     * true;
     * @param startTime double; the start time of the simulation in hours
     * @param warmupPeriod double; the warm up period of the simulation in hours
     * @param runLength double; the duration of the simulation in hours
     * @param model SimpleModelInterface; the simulation to execute
     * @param replicationNr int; the replication number
     * @param seed long; seed for the standard Random Number Generator
     * @throws SimRuntimeException when e.g., warmupPeriod is larger than runLength
     * @throws NamingException when context for the animation cannot be created
     */
    void initialize(double startTime, double warmupPeriod, double runLength, SimpleModelInterface model, int replicationNr,
            long seed) throws SimRuntimeException, NamingException;

    /** {@inheritDoc} */
    @Override
    SimpleReplication getReplication();

    /**
     * Return the simulator time in hours since the start of the simulation.
     * @return double; the simulator time in hours since the start of the simulation.
     */
    @Override
    Double getSimulatorTime();

}
