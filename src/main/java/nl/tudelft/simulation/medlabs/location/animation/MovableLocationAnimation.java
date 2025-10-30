package nl.tudelft.simulation.medlabs.location.animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import nl.tudelft.simulation.medlabs.animation.TinyRenderable2D;
import nl.tudelft.simulation.medlabs.location.Location;

/**
 * Movable location, e.g. transport.
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
public class MovableLocationAnimation extends TinyRenderable2D
{
    /** */
    private static final long serialVersionUID = 20200920L;

    /** the default font. */
    protected static final Font SMALLFONT = new Font("SansSerif", Font.PLAIN, 6);

    /**
     * @param location the location.
     */
    public MovableLocationAnimation(final Location location)
    {
        super(location);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        graphics.setColor(Color.BLACK);
        graphics.drawRect(-15, -2, 30, 4); // rectangle of 30x4 meters
    }

    /**
     * Draw the number of people in the location for a 40x4 icon.
     * @param graphics Graphics context
     * @param lineNumber String the transport line number or code
     */
    protected void drawNumber30x4(final Graphics2D graphics, final String lineNumber)
    {
        graphics.setFont(SMALLFONT);
        String number = "" + ((Location) getSource()).getAllPersonIds().size();
        graphics.drawString(number, 17, 3);
        graphics.drawString(lineNumber, -15, -3);
    }

}
