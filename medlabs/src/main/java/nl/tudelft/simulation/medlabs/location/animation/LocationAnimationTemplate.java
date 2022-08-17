package nl.tudelft.simulation.medlabs.location.animation;

import java.awt.Color;
import java.awt.Font;

import nl.tudelft.simulation.medlabs.Named;

/**
 * LocationAnimationTemplate contains a template for the animation for a set of locations.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
@SuppressWarnings("checkstyle:hiddenfield")
public class LocationAnimationTemplate implements Named
{
    /** */
    private static final long serialVersionUID = 20210102L;

    /** default font. */
    private static final Font FONT12 = new Font("SansSerif", Font.PLAIN, 12);

    /** the template name. */
    private final String name;

    /**
     * Half size of the short side of the symbol -- symbol will be two times this size at least. Note that the half size is NOT
     * the half diameter of the symbol by default: for a plus, for instance it is the half side of each of the short sides of
     * the strokes of the plus, making the size of the plus symbol (6*halfSize x 6*halfSize).
     */
    private int halfShortSize = 6;

    /** line color. */
    private Color lineColor = Color.ORANGE;

    /** fill color. */
    private Color fillColor = Color.ORANGE;

    /** character in the middle of the symbol. */
    private String character = "";

    /** character size of the character in the middle of the symbol. */
    private int characterSize = 12;

    /** font for the character in the middle of the symbol. */
    private Font characterFont = FONT12;

    /** color of the character in the middle of the symbol. */
    private Color characterColor = Color.BLACK;

    /** character size of the number next to the symbol. */
    private int numberSize = 12;

    /** font of the number next to the symbol. */
    private Font numberFont = FONT12;

    /** color of the number next to the symbol. */
    private Color numberColor = Color.BLACK;

    /**
     * @param name String; the template name
     */
    public LocationAnimationTemplate(final String name)
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * Return the half size of the short side of the symbol -- symbol will be two times this size at least.
     * @return the halfShortSize
     */
    public int getHalfShortSize()
    {
        return this.halfShortSize;
    }

    /**
     * Set the half size of the short side of the symbol -- symbol will be two times this size by default, unless the size of
     * the symbol has already been changed. The half size is NOT the half diameter of the symbol by default: for a plus, for
     * instance it is the half side of each of the short sides of the strokes of the plus, making the size of the plus symbol
     * (6*halfSize x 6*halfSize).
     * @param halfShortSize the halfShortSize to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setHalfShortSize(final int halfShortSize)
    {
        this.halfShortSize = halfShortSize;
        if (this.characterFont != FONT12) // == and not equals() as we want to know whether it has been changed
        {
            setCharacterSize(2 * halfShortSize);
        }
        return this;
    }

    /**
     * Return the line color of the symbol.
     * @return the lineColor
     */
    public Color getLineColor()
    {
        return this.lineColor;
    }

    /**
     * Set the line color of the symbol.
     * @param lineColor the lineColor to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setLineColor(final Color lineColor)
    {
        this.lineColor = lineColor;
        return this;
    }

    /**
     * Return the fill color of the symbol.
     * @return the fillColor
     */
    public Color getFillColor()
    {
        return this.fillColor;
    }

    /**
     * Set the fill color of the symbol.
     * @param fillColor the fillColor to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setFillColor(final Color fillColor)
    {
        this.fillColor = fillColor;
        return this;
    }

    /**
     * Return the character in the middle of the symbol (can be "").
     * @return the character
     */
    public String getCharacter()
    {
        return this.character;
    }

    /**
     * Set the character in the middle of the symbol (can be "").
     * @param character the character to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setCharacter(final String character)
    {
        this.character = character;
        return this;
    }

    /**
     * Return the size of the character in the middle of the symbol.
     * @return the characterSize
     */
    public int getCharacterSize()
    {
        return this.characterSize;
    }

    /**
     * Set the size of the character in the middle of the symbol.
     * @param characterSize the characterSize to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setCharacterSize(final int characterSize)
    {
        this.characterSize = characterSize;
        this.characterFont = new Font("SansSerif", Font.PLAIN, characterSize);
        return this;
    }

    /**
     * Return the font of the character in the middle of the symbol.
     * @return the characterFont
     */
    public Font getCharacterFont()
    {
        return this.characterFont;
    }

    /**
     * Return the color of the character in the middle of the symbol.
     * @return the characterColor
     */
    public Color getCharacterColor()
    {
        return this.characterColor;
    }

    /**
     * Set the color of the character in the middle of the symbol.
     * @param characterColor the characterColor to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setCharacterColor(final Color characterColor)
    {
        this.characterColor = characterColor;
        return this;
    }

    /**
     * Return the size of the number next to the symbol.
     * @return the numberSize
     */
    public int getNumberSize()
    {
        return this.numberSize;
    }

    /**
     * Set the size of the number next to the symbol.
     * @param numberSize the numberSize to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setNumberSize(final int numberSize)
    {
        this.numberSize = numberSize;
        this.numberFont = new Font("SansSerif", Font.PLAIN, numberSize);
        return this;
    }

    /**
     * Return the font of the number next to the symbol.
     * @return the numberFont
     */
    public Font getNumberFont()
    {
        return this.numberFont;
    }

    /**
     * Return the color of the number next to the symbol.
     * @return the numberColor
     */
    public Color getNumberColor()
    {
        return this.numberColor;
    }

    /**
     * Set the color of the number next to the symbol.
     * @param numberColor the numberColor to set
     * @return the current object for method chaining
     */
    public LocationAnimationTemplate setNumberColor(final Color numberColor)
    {
        this.numberColor = numberColor;
        return this;
    }

}
