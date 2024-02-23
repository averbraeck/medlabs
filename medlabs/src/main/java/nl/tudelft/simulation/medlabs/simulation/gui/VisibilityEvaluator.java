package nl.tudelft.simulation.medlabs.simulation.gui;

import java.util.Set;

import nl.tudelft.simulation.dsol.animation.Locatable;

/**
 * VisibilityEvaluator.java.
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
 * @param <T> the type for which this evaluation can be applied
 */
public interface VisibilityEvaluator<T extends Locatable>
{
    /**
     * Evaluate whether the object has to be shown on the screen.
     * @param visibleToggles Set&lt;String&gt;; the set f strings to be shown
     * @param object T; the object to be evaluated
     * @return whether the object has to be drawn on the screen, based on the toggle settings
     */
    boolean evaluate(Set<String> visibleToggles, T object);
}
