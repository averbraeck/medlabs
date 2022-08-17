package nl.tudelft.simulation.medlabs.properties;

/**
 * PropertiesInterface contains the contract for an object type (Person, Location) to have additional properties. This means
 * that the Person class or the Location class implements the PropertiesInterface. The only return value is float at the moment;
 * this makes calculations easier. When you expect a boolean value; just do <code>value != 0f</code>; when you expect an integer
 * value, take <code>Math.round(value)</code>.
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
public interface PropertiesInterface
{
    /**
     * Retrieve a floating point value for a named property (can point to an existing attribute or to a central table).
     * @param name String; the name of the property to retrieve
     * @return float; the floating point value of the property for the object requesting it
     */
    double getFloatProperty(String name);

}
