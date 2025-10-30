package nl.tudelft.simulation.medlabs.simulation.gui;

import java.io.InputStream;

/**
 * Resource utility. Code based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class Resource
{

    /** Constructor. */
    private Resource()
    {
        //
    }

    /**
     * Obtains stream for resource, either in IDE or java.
     * @param name String; name of resource
     * @return the resolved input stream
     */
    public static InputStream getResourceAsStream(final String name)
    {
        InputStream stream = Resource.class.getResourceAsStream(name);
        if (stream != null)
        {
            return stream;
        }
        stream = Resource.class.getResourceAsStream("/resources" + name);
        if (stream != null)
        {
            return stream;
        }
        throw new RuntimeException("Unable to load resource " + name);
    }

}
