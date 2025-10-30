package nl.tudelft.simulation.medlabs.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of a number of Properties arrays, to which the model can delegate the implementation for instance for
 * Persons or Locations. The Properties classs is therefore a delegated class from the Model. You can then ask, e.g.,
 * <code>model.getPersonProperties().getIntValue(personId, "age");</code><br>
 * Storage for the types of property arrays is optimized for speed and not for memory-- one map exists for each type to avoid
 * casting for every addition and every retrieval. Since an empty map is not very big, this is not considered to be a problem.
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
public class Properties
{
    /** the class of the object for which the arrays are maintained. */
    private final Class<?> objectClass;

    /** the name of the object class for which the arrays are maintained. */
    private final String objectClassName;

    /** the number of elements in the array. */
    private final int size;

    /** the map of named boolean property arrays. */
    private Map<String, PropertyArray<?>> propertyArrays = new HashMap<>();

    /**
     * Create a map of properties for a certain object, where all properties have the same size.
     * @param objectClass Class&lt;?&gt;; the object class for which this is the property
     * @param size int; the size of the properties arrays for this class.
     */
    public Properties(final Class<?> objectClass, final int size)
    {
        this.objectClass = objectClass;
        this.size = size;
        this.objectClassName = objectClass.getSimpleName();
    }

    /**
     * Return the object class for which the arrays are maintained.
     * @return String; the object class for which the arrays are maintained
     */
    public Class<?> getObjectClass()
    {
        return this.objectClass;
    }

    /**
     * Return the name of the object class for which the arrays are maintained.
     * @return String; the name of the object class for which the arrays are maintained
     */
    public String getObjectClassName()
    {
        return this.objectClassName;
    }

    /**
     * Return the size of the property arrays.
     * @return int; the size of the property arrays for the object class
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * Add a typed property array to the Properties.
     * @param propertyType PropertyType; the type of property array to add
     * @param name String; the name of the property array to add
     */
    public void addPropertyArray(final PropertyType propertyType, final String name)
    {
        switch (propertyType)
        {
            case BOOLEAN:
                addBooleanPropertyArray(name);
                break;
            case BYTE:
                addBytePropertyArray(name);
                break;
            case SHORT:
                addShortPropertyArray(name);
                break;
            case INT:
                addIntPropertyArray(name);
                break;
            case FLOAT:
                addFloatPropertyArray(name);
                break;
            default:
                break;
        }
    }

    /**
     * Add a boolean property array to the Properties.
     * @param name String; the name of the property array to add
     */
    public void addBooleanPropertyArray(final String name)
    {
        this.propertyArrays.put(name, new BooleanPropertyArray(name, this.size));
    }

    /**
     * Add a byte property array to the Properties.
     * @param name String; the name of the property array to add
     */
    public void addBytePropertyArray(final String name)
    {
        this.propertyArrays.put(name, new BytePropertyArray(name, this.size));
    }

    /**
     * Add a short property array to the Properties.
     * @param name String; the name of the property array to add
     */
    public void addShortPropertyArray(final String name)
    {
        this.propertyArrays.put(name, new ShortPropertyArray(name, this.size));
    }

    /**
     * Add an int property array to the Properties.
     * @param name String; the name of the property array to add
     */
    public void addIntPropertyArray(final String name)
    {
        this.propertyArrays.put(name, new IntPropertyArray(name, this.size));
    }

    /**
     * Add a float property array to the Properties.
     * @param name String; the name of the property array to add
     */
    public void addFloatPropertyArray(final String name)
    {
        this.propertyArrays.put(name, new FloatPropertyArray(name, this.size));
    }

    /**
     * Return a boolean property array from the Properties.
     * @param name String; the name of the property array to retrieve
     * @return BooleanPropertyArray; the array to retrieve
     */
    public PropertyArray<?> getPropertyArray(final String name)
    {
        return this.propertyArrays.get(name);
    }

    /**
     * Retrieve an element from a property array as a boolean from position i.
     * @param name String; the name of the property array
     * @param i int; the position in the array
     * @return boolean; the value at position i
     */
    public boolean getBoolean(final String name, final int i)
    {
        return this.propertyArrays.get(name).getBoolean(i);
    }

    /**
     * Set a boolean value in the array at position i.
     * @param name String; the name of the property array
     * @param i int; the position
     * @param value boolean; the value to set at position i
     */
    public void setBoolean(final String name, final int i, final boolean value)
    {
        this.propertyArrays.get(name).setBoolean(i, value);
    }

    /**
     * Retrieve an element from a property array as a byte from position i.
     * @param name String; the name of the property array
     * @param i int; the position in the array
     * @return byte; the value at position i
     */
    public byte getByte(final String name, final int i)
    {
        return this.propertyArrays.get(name).getByte(i);
    }

    /**
     * Set a byte value in the array at position i.
     * @param name String; the name of the property array
     * @param i int; the position
     * @param value byte; the value to set at position i
     */
    public void setByte(final String name, final int i, final byte value)
    {
        this.propertyArrays.get(name).setByte(i, value);
    }

    /**
     * Retrieve an element from a property array as a short from position i.
     * @param name String; the name of the property array
     * @param i int; the position in the array
     * @return short; the value at position i
     */
    public short getShort(final String name, final int i)
    {
        return this.propertyArrays.get(name).getShort(i);
    }

    /**
     * Set a short value in the array at position i.
     * @param name String; the name of the property array
     * @param i int; the position
     * @param value short; the value to set at position i
     */
    public void setShort(final String name, final int i, final short value)
    {
        this.propertyArrays.get(name).setShort(i, value);
    }

    /**
     * Retrieve an element from a property array as an int from position i.
     * @param name String; the name of the property array
     * @param i int; the position in the array
     * @return int; the value at position i
     */
    public int getInteger(final String name, final int i)
    {
        return this.propertyArrays.get(name).getInt(i);
    }

    /**
     * Set an int value in the array at position i.
     * @param name String; the name of the property array
     * @param i int; the position
     * @param value int; the value to set at position i
     */
    public void setInteger(final String name, final int i, final int value)
    {
        this.propertyArrays.get(name).setInt(i, value);
    }

    /**
     * Retrieve an element from a property array as a float from position i.
     * @param name String; the name of the property array
     * @param i int; the position in the array
     * @return float; the value at position i
     */
    public float getFloat(final String name, final int i)
    {
        return this.propertyArrays.get(name).getFloat(i);
    }

    /**
     * Set a float value in the array at position i.
     * @param name String; the name of the property array
     * @param i int; the position
     * @param value float; the value to set at position i
     */
    public void setFloat(final String name, final int i, final float value)
    {
        this.propertyArrays.get(name).setFloat(i, value);
    }

}
