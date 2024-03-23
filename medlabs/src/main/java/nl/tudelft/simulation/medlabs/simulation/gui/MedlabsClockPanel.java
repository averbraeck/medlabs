package nl.tudelft.simulation.medlabs.simulation.gui;

import nl.tudelft.simulation.dsol.swing.gui.control.ClockPanel;
import nl.tudelft.simulation.medlabs.simulation.SimpleDevsSimulatorInterface;

/**
 * ClockLabel for a double time. The formatter has been adjusted to display days and hours since the start of the simulation.
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
public class MedlabsClockPanel extends ClockPanel<Double>
{
    /** */
    private static final long serialVersionUID = 20201227L;

    /**
     * Construct a clock panel with a double time.
     * @param simulator SimulatorInterface&lt;A, R, T&gt;; the simulator
     */
    public MedlabsClockPanel(final SimpleDevsSimulatorInterface simulator)
    {
        super(simulator);
        setPrevSimTime(0.0);
    }

    /** {@inheritDoc} */
    @Override
    protected String formatSimulationTime(final Double simulationTime)
    {
        int days = (int) Math.floor(simulationTime.doubleValue() / 24);
        double hours = simulationTime.doubleValue() - 24 * days;
        return String.format(" t = %3d days, %4.1f hrs", days, hours);
    }
}
