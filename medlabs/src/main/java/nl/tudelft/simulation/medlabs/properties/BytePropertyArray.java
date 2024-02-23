package nl.tudelft.simulation.medlabs.properties;

/**
 * IntegerPropertyArray is a property array for bytes.
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
public class BytePropertyArray extends PropertyArray<Byte>
{
    /** the array with the int values. */
    private final byte[] array;
    
    /**
     * Create a new byte property array.
     * @param name String; the name of the property
     * @param size int; the size of the array
     */
    public BytePropertyArray(final String name, final int size)
    {
        super(name, size);
        this.array = new byte[size];
    }

    /** {@inheritDoc} */
    @Override
    public Byte get(final int i)
    {
        return this.array[i];
    }

    /** {@inheritDoc} */
    @Override
    public void set(final int i, final Byte value)
    {
        this.array[i] = value;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getBoolean(final int i)
    {
        return this.array[i] != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setBoolean(final int i, final boolean value)
    {
        this.array[i] = value ? (byte) 1 : (byte) 0;
    }

    /** {@inheritDoc} */
    @Override
    public byte getByte(final int i)
    {
        return this.array[i];
    }

    /** {@inheritDoc} */
    @Override
    public void setByte(final int i, final byte value)
    {
        this.array[i] = value;
    }

    /** {@inheritDoc} */
    @Override
    public short getShort(final int i)
    {
        return (short) this.array[i];
    }

    /** {@inheritDoc} */
    @Override
    public void setShort(final int i, final short value)
    {
        this.array[i] = (byte) value;
    }

    /** {@inheritDoc} */
    @Override
    public int getInt(final int i)
    {
        return this.array[i];
    }

    /** {@inheritDoc} */
    @Override
    public void setInt(final int i, final int value)
    {
        this.array[i] = (byte) value;
    }

    /** {@inheritDoc} */
    @Override
    public float getFloat(final int i)
    {
        return this.array[i];
    }

    /** {@inheritDoc} */
    @Override
    public void setFloat(final int i, final float value)
    {
        this.array[i] = (byte) Math.round(value);
    }

}
