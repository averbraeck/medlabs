package nl.tudelft.simulation.medlabs;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * Contract for an object with a name and a reference to the model.
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
public interface ModelNamed extends Named
{
    /**
     * Return the model to which this object belongs.
     * @return MedlabsModelInterface; the model
     */
    MedlabsModelInterface getModel();
}
