package nl.tudelft.simulation.medlabs.animation;

import nl.tudelft.simulation.dsol.animation.D2.Renderable2D;
import nl.tudelft.simulation.medlabs.common.ModelLocatable;

/**
 * A small implementation of the Renderable2D, leaving out a few fields and activities when drawing.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class TinyRenderable2D extends Renderable2D<ModelLocatable>
{
    /** */
    private static final long serialVersionUID = 20200920L;

    /**
     * constructs a new TinyRenderable2D.
     * @param source ModelLocatable; the source with a location and orientation
     */
    public TinyRenderable2D(final ModelLocatable source)
    {
        super(source, source.getModel().getSimulator());
        setScaleObject(true);
        setScaleY(false);
    }
}
