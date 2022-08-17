package nl.tudelft.simulation.medlabs.disease;

/**
 * DiseaseState contains a few important states: susceptible, ill, recovered, immune, or dead. Hereby, a rough classification
 * for important decisions by the Person can be made such as removal from the model. The reason for having these states is that
 * the DiseasePhase can be coded by the modeler in any way, using any name, so the important characteristics of the DiseasePhase
 * cannot be determined.
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
public enum DiseaseState
{
    /** Healthy state. */
    SUSCEPTIBLE,

    /** Ill state -- covers exposed, symptomatic and asymptomatic stages. */
    ILL,

    /** Recovered state. */
    RECOVERED,

    /** Dead state. */
    DEAD,

    /** Immune state. */
    IMMUNE;

}
