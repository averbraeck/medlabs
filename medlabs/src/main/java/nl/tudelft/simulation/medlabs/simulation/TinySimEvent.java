package nl.tudelft.simulation.medlabs.simulation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.AbstractSimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;

/**
 * TinySimEvent is a low-memory footprint implementation of the SimEvent. For now, it delegates to the regular SimEvent, but it
 * can be changed to a smaller version when needed.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TinySimEvent extends AbstractSimEvent<Double>
{
    /** */
    private static final long serialVersionUID = 20200918L;

    /** the method cache. */
    private static Map<String, Method> methodCache = new HashMap<>();

    /** target the target on which a state change is scheduled. */
    private final Object target;

    /** method is the method which embodies the state change. */
    private final Method method;

    /** args are the arguments that are used to invoke the method with. */
    private final Object[] args;

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime Double; the absolute time the event has to be executed.
     * @param source Object; the source that created the method
     * @param target Object; the object on which the method must be invoked.
     * @param methodName String; the method to invoke
     * @param args Object[]; the arguments the method to invoke with
     */
    public TinySimEvent(final double executionTime, final Object source, final Object target, final String methodName,
            final Object[] args)
    {
        this(executionTime, SimEventInterface.NORMAL_PRIORITY, source, target, methodName, args);
    }

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime Double; the time the event has to be executed.
     * @param priority short; the priority of the event
     * @param source Object; the source that created the method
     * @param target Object; the object on which the method must be invoked.
     * @param methodName String; the method to invoke
     * @param args Object[]; the arguments the method to invoke with
     */
    public TinySimEvent(final double executionTime, final short priority, final Object source, final Object target,
            final String methodName, final Object[] args)
    {
        super(executionTime, priority);
        this.target = target;
        this.args = args;
        this.method = resolveMethod(methodName);
    }

    /**
     * Resolve the method from the cache or by reflection.
     * @param methodName String; the method to invoke
     * @return the method to call
     */
    private Method resolveMethod(final String methodName)
    {
        try
        {
            String key = this.target.getClass().getName() + "." + methodName;
            Method result = methodCache.get(key);
            if (result == null)
            {
                result = ClassUtil.resolveMethod(this.target, methodName, this.args);
                methodCache.put(key, result);
            }
            return result;
        }
        catch (Exception exception)
        {
            System.err.println(exception.toString() + " resolving method " + this.target + "." + this.method.getName()
                    + " with arguments " + Arrays.toString(this.getArgs()));
            throw new SimRuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void execute() throws SimRuntimeException
    {
        try
        {
            this.method.setAccessible(true);
            this.method.invoke(this.target, this.args);
        }
        catch (Exception exception)
        {
            System.err.println(exception.toString() + " calling " + this.target + "." + this.method.getName()
                    + " with arguments " + Arrays.toString(this.getArgs()));
            throw new SimRuntimeException(exception);
        }
    }

    /**
     * @return Returns the args.
     */
    public Object[] getArgs()
    {
        return this.args;
    }

    /**
     * @return Returns the method.
     */
    public String getMethodName()
    {
        return this.method.getName();
    }

    /**
     * @return Returns the target.
     */
    public Object getTarget()
    {
        return this.target;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public String toString()
    {
        return "SimEvent[time=" + this.absoluteExecutionTime + "; priority=" + this.priority + "; target=" + this.target
                + "; method=" + this.method.getName() + "; args=" + this.args + "]";
    }

}
