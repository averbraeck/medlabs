package nl.tudelft.simulation.medlabs.simulation;

import nl.tudelft.simulation.jstats.streams.Java2Random;

/**
 * <br />
 * Copyright (c) 2011-2013 TU Delft, Faculty of TBM, Systems & Simulation <br />
 * This software is licensed without restrictions to Nederlandse Organisatie voor Toegepast Natuurwetenschappelijk Onderzoek TNO
 * (TNO), Erasmus University Rotterdam, Delft University of Technology, Panteia B.V., Stichting Projecten Binnenvaart, Ab Ovo
 * Nederland B.V., Modality Software Solutions B.V., and Rijkswaterstaat - Dienst Water, Verkeer en Leefomgeving, including the
 * right to sub-license sources and derived products to third parties. <br />
 * @version Feb 26, 2013 <br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck </a>
 */
public class RepeatableRandomStream extends Java2Random
{

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * do not use.
     */
    private RepeatableRandomStream()
    {
        super();
    }

    /**
     * do not use.
     * @param seed long; the seed
     */
    private RepeatableRandomStream(final long seed)
    {
        super(seed);
    }

    /**
     * Return a random stream that is the same every time for a certain name, but can be changed based on a number (e.g. a year)
     * @param identifier
     * @param seed
     * @return a random stream that is the same every time for a certain name and integer
     */
    public static final RepeatableRandomStream create(final String identifier, final long seed)
    {
        return new RepeatableRandomStream(identifier.hashCode() + seed);
    }

    /**
     * Return a random stream that is the same every time for a certain name.
     * @param identifier
     * @return a random stream that is the same every time for a certain name
     */
    public static final RepeatableRandomStream create(final String identifier)
    {
        return new RepeatableRandomStream(identifier.hashCode());
    }
}
