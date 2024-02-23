package nl.tudelft.simulation.medlabs.location.animation;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import nl.tudelft.simulation.medlabs.location.Location;

/**
 * LocationAnimationPlus implements a plus location animation.
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
public class LocationAnimationPlus extends LocationAnimation
{
    /** */
    private static final long serialVersionUID = 20210102L;

    /**
     * Plus animation for a location. The half short size is the half of the vertical / horizontal plus. The entire symbol is
     * (6*half x 6*half) in size.
     * @param location Location; the location to be animated
     * @param template LocationAnimationTemplate; the template to use
     */
    public LocationAnimationPlus(final Location location, final LocationAnimationTemplate template)
    {
        super(location, template);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        int half = this.template.getHalfShortSize();
        int x2 = 2 * half;
        int x3 = 3 * half;
        int x6 = 6 * half;
        graphics.setColor(this.template.getLineColor());
        graphics.drawRect(-half, -x3, x2, x6);
        graphics.drawRect(-x3, -half, x6, x2);
        if (this.template.getFillColor() != null)
        {
            graphics.setColor(this.template.getFillColor());
            graphics.fillRect(-half, -x3, x2, x6);
            graphics.fillRect(-x3, -half, x6, x2);
        }
        if (this.template.getCharacter().length() > 0)
        {
            graphics.setFont(this.template.getCharacterFont());
            graphics.setColor(this.template.getCharacterColor());
            graphics.drawString(this.template.getCharacter(), 3 - half, half - 2);
        }
        graphics.setFont(this.template.getNumberFont());
        graphics.setColor(this.template.getNumberColor());
        String number = "" + ((Location) getSource()).getAllPersonIds().size();
        graphics.drawString(number, x3 + 2, x3);
    }

}
