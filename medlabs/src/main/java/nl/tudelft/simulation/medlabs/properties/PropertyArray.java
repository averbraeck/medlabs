package nl.tudelft.simulation.medlabs.properties;

/**
 * Property is the abstract parent of the typed properties. Properties are
 * descriptors of a stored variable that can be retrieved with a 'get()' method.
 * For Persons, property values are stored in an array, e.g., whether persons
 * have been vaccinated, and the property class serves as a descriptor.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved. The MEDLABS project (Modeling
 * Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at
 * providing policy analysis tools to predict and help contain the spread of
 * epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information
 * <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research
 * of Mingxin Zhang at TU Delft and is described in the PhD thesis "Large-Scale
 * Agent-Based Social Simulation" (2016). This software is licensed under the
 * BSD license. See license.txt in the main project.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the property type
 */
public abstract class PropertyArray<T> {
	/** the name of the property. */
	private final String name;

	/** the number of elements in the array. */
	private final int size;

	/**
	 * Create a new property array.
	 * 
	 * @param name String; the name of the property
	 * @param size int; the size of the array
	 */
	public PropertyArray(final String name, final int size) {
		this.name = name;
		this.size = size;
	}

	/**
	 * Retrieve an element from the array from position i.
	 * 
	 * @param i int; the position in the array
	 * @return T; the value at position i
	 */
	public abstract T get(int i);

	/**
	 * Set a value in the array at position i.
	 * 
	 * @param i     int; the position
	 * @param value T; the value to set at position i
	 */
	public abstract void set(int i, T value);

	/**
	 * Retrieve a boolean element from the array from position i.
	 * 
	 * @param i int; the position in the array
	 * @return T; the boolean value at position i
	 */
	public abstract boolean getBoolean(int i);

	/**
	 * Set a boolean value in the array at position i.
	 * 
	 * @param i     int; the position
	 * @param value T; the boolean value to set at position i
	 */
	public abstract void setBoolean(int i, boolean value);

	/**
	 * Retrieve a byte element from the array from position i.
	 * 
	 * @param i int; the position in the array
	 * @return T; the byte value at position i
	 */
	public abstract byte getByte(int i);

	/**
	 * Set a byte value in the array at position i.
	 * 
	 * @param i     int; the position
	 * @param value T; the byte value to set at position i
	 */
	public abstract void setByte(int i, byte value);

	/**
	 * Retrieve a short element from the array from position i.
	 * 
	 * @param i int; the position in the array
	 * @return T; the short value at position i
	 */
	public abstract short getShort(int i);

	/**
	 * Set a short value in the array at position i.
	 * 
	 * @param i     int; the position
	 * @param value T; the short value to set at position i
	 */
	public abstract void setShort(int i, short value);

	/**
	 * Retrieve a int element from the array from position i.
	 * 
	 * @param i int; the position in the array
	 * @return T; the int value at position i
	 */
	public abstract int getInt(int i);

	/**
	 * Set a int value in the array at position i.
	 * 
	 * @param i     int; the position
	 * @param value T; the int value to set at position i
	 */
	public abstract void setInt(int i, int value);

	/**
	 * Retrieve a float element from the array from position i.
	 * 
	 * @param i int; the position in the array
	 * @return T; the float value at position i
	 */
	public abstract float getFloat(int i);

	/**
	 * Set a float value in the array at position i.
	 * 
	 * @param i     int; the position
	 * @param value T; the float value to set at position i
	 */
	public abstract void setFloat(int i, float value);

	/**
	 * Return the name of the property array.
	 * 
	 * @return String; the name of the array
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the size of the property array.
	 * 
	 * @return int; the size of the array
	 */
	public int getSize() {
		return this.size;
	}

}
