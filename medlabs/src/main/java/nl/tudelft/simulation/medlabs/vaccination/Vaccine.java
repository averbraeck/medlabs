package nl.tudelft.simulation.medlabs.vaccination;

import nl.tudelft.simulation.medlabs.AbstractModelIdNamed;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * The Vaccine class stores information about vaccines in the model. For now, the basic vaccine class does not store extra
 * information -- the vaccine is used in the Disease propagation model. Of course, extensions of the Vaccine class can contain
 * information about the efficacy and duration of effectiveness.
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
public class Vaccine extends AbstractModelIdNamed
{
    /** */
    private static final long serialVersionUID = 20220110L;

    /**
     * Create a named, identifiable, model aware vaccine object.
     * @param model MedlabsModelInterface; the reference to the model
     * @param id int; the unique id of the vaccine
     * @param name String; the name of the vaccine
     */
    public Vaccine(final MedlabsModelInterface model, final int id, final String name)
    {
        super(model, id, name);
    }

}
