package nl.tudelft.simulation.medlabs.simulation;

import java.io.Serializable;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;

public class SimpleAnimator extends DevsRealTimeAnimator<Double>
        implements SimpleDevsSimulatorInterface, SimpleAnimatorInterface
{
    /**  */
    private static final long serialVersionUID = 20200918L;

    /** Counter for replication. */
    private int lastReplication = 0;

    /**
     * the translation from a millisecond on the wall clock to '1.0' in the simulation time. This means that if the wall clock
     * runs in seconds, the factor should be 0.001 if we want a real-time model, and if the simulation time is in hours, the
     * factor should be 1.0 / (1000.0 * 3600.0).
     */
    private final double msecWallClockToSimTimeUnit;

    /**
     * The time unit of the simulator is in hours. The default setting for animation speed "1" is now set to 0.1 hour/sec, which
     * is 0.1 / 1000.0 hours / msec.
     * @param id Serializable; the id that is used in events and statistics
     */
    public SimpleAnimator(final Serializable id)
    {
        super(id);
        this.msecWallClockToSimTimeUnit = 0.1 / 1000.0;
    }

    /** {@inheritDoc} */
    @Override
    protected Double simulatorTimeForWallClockMillis(final double wallMilliseconds)
    {
        return this.msecWallClockToSimTimeUnit * wallMilliseconds;
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
