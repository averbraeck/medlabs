package nl.tudelft.simulation.medlabs.location.animation.defaults;

import java.awt.Color;

import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.animation.LocationAnimationRect2x1;
import nl.tudelft.simulation.medlabs.location.animation.LocationAnimationTemplate;

/**
 * Animation of a retail location.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
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
public class RetailAnimation extends LocationAnimationRect2x1
{
    /** */
    private static final long serialVersionUID = 20200919L;

    /** default template. */
    public static final LocationAnimationTemplate RETAIL_TEMPLATE =
            new LocationAnimationTemplate("Retail").setLineColor(Color.BLUE).setFillColor(null).setCharacter("R")
                    .setCharacterColor(Color.BLUE).setCharacterSize(8).setHalfShortSize(4).setNumberColor(Color.BLUE);

    /**
     * Create the animation for this location object.
     * @param location the location belonging to this animation
     */
    public RetailAnimation(final Location location)
    {
        super(location, RETAIL_TEMPLATE);
    }

}
