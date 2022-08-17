package nl.tudelft.simulation.medlabs.location.animation;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import nl.tudelft.simulation.medlabs.animation.TinyRenderable2D;
import nl.tudelft.simulation.medlabs.location.Location;

/**
 * Animation for a generic location. Can be subclassed for specific locations, enabling them to be turned on or off in the
 * AnimationPanel.
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
public abstract class LocationAnimation extends TinyRenderable2D
{
    /** */
    private static final long serialVersionUID = 20200920L;

    /** the template to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final LocationAnimationTemplate template;

    /**
     * Generic animation for a location.
     * @param location Location; the location to be animated
     * @param template LocationAnimationTemplate; the template to use
     */
    public LocationAnimation(final Location location, final LocationAnimationTemplate template)
    {
        super(location);
        this.template = template;
    }

    /** {@inheritDoc} */
    @Override
    public abstract void paint(Graphics2D graphics, ImageObserver observer);

    /**
     * Create a generic animation for a location. Choices for the symbol are: square, rectangle1x2, rectangle2x1, triangle,
     * circle, plus, cross1x2
     * @param location Location; the location to be animated
     * @param template LocationAnimationTemplate; the template to use
     * @param symbol String; the symbol to use.
     * @return LocationAnimation; the animation class for the symbol
     */
    public static LocationAnimation create(final Location location, final LocationAnimationTemplate template,
            final String symbol)
    {
        switch (symbol.toLowerCase())
        {
            case "square":
                return new LocationAnimationSquare(location, template);

            default:
                break;
        }
        throw new IllegalArgumentException("Unknown LocationAnimation symbol: " + symbol);
    }
    // {
    // graphics.setColor(Color.ORANGE);
    // graphics.drawRect(-10, -10, 20, 20); // rectangle of 20x20 meters
    // drawNumber20(graphics);
    // }
    //
    // /**
    // * Draw the number of people in the location for a 30x30 icon.
    // * @param graphics Graphics context
    // */
    // protected void drawNumber30(final Graphics2D graphics)
    // {
    // graphics.setFont(FONT);
    // String number = "" + ((Location) getSource()).getAllPersonIds().size();
    // graphics.drawString(number, 17, 6);
    // }
    //
    // /**
    // * Draw the number of people in the location for a 20x20 icon.
    // * @param graphics Graphics context
    // */
    // protected void drawNumber20(final Graphics2D graphics)
    // {
    // graphics.setFont(FONT);
    // String number = "" + ((Location) getSource()).getAllPersonIds().size();
    // graphics.drawString(number, 12, 6);
    // }
    //
    // /**
    // * Draw the number of people in the location for a 10x10 icon.
    // * @param graphics Graphics context
    // */
    // protected void drawNumber10(final Graphics2D graphics)
    // {
    // graphics.setFont(SMALL_FONT);
    // String number = "" + ((Location) getSource()).getAllPersonIds().size();
    // graphics.drawString(number, 7, 6);
    // }
    //
    // /**
    // * Draw the number of people in the location for a 6x6 icon.
    // * @param graphics Graphics context
    // */
    // protected void drawNumber6(final Graphics2D graphics)
    // {
    // graphics.setFont(SMALL_FONT);
    // String number = "" + ((Location) getSource()).getAllPersonIds().size();
    // graphics.drawString(number, 5, 3);
    // }

}
