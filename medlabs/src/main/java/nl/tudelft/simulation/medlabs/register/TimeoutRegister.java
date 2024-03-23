package nl.tudelft.simulation.medlabs.register;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * The TimeoutDateRegister class contains the last date that a person has been registered, with a timeout on how long the record
 * is kept. An example is a self test that is valid for 2 days. After 2 days, the record is erased automatically.
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
public class TimeoutRegister extends LastDateRegister
{
    /** */
    private static final long serialVersionUID = 20220110L;

    /** map with the registered person ids and dates of registration. */
    private Map<Integer, Short> personMap = new LinkedHashMap<>();

    /** the timeout of the registration in days. */
    private final int timeoutDays;

    /**
     * Create a register for persons.
     * @param model MedlabsModelInterface; the model
     * @param name String; the name of the register
     * @param timeoutDays int; the timeout of the registration in days
     */
    public TimeoutRegister(final MedlabsModelInterface model, final String name, final int timeoutDays)
    {
        super(model, name);
        this.timeoutDays = timeoutDays;
        cleanup();
    }

    /**
     * Cleanup the registrations that are older than 'timeout' days, every 24 hours.
     */
    protected void cleanup()
    {
        int date = this.model.getSimulator().getSimulatorTime().intValue();
        Iterator<Integer> it = this.personMap.keySet().iterator();
        while (it.hasNext())
        {
            int personId = it.next();
            int registrationDate = this.personMap.get(personId);
            if (registrationDate + this.timeoutDays >= date)
            {
                it.remove();
            }
        }
        this.model.getSimulator().scheduleEventRel(24.0, this, "cleanup", new Object[] {});
    }

}
