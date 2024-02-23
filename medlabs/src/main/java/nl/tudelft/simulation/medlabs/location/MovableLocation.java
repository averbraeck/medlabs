package nl.tudelft.simulation.medlabs.location;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * A MovableLocation is a location that contains Persons that moves between Locations. Examples are a Bus, Metro, Car and Taxi.
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
public class MovableLocation extends Location
{
    /**
     * Create a movable location.
     * @param model MedlabsModelInterface; the model for looking up the simulator and other model objects
     * @param id int; the location id within the locationType
     * @param locationTypeId byte; the index number of the locationType
     * @param lat float; latitude of the location
     * @param lon float; longitude of the location
     * @param numberOfSubLocations short; number of sub locations (e.g., rooms)
     * @param surfaceM2 float total surface in m2
     */
    public MovableLocation(final MedlabsModelInterface model, final int id, final byte locationTypeId, final float lat,
            final float lon, final short numberOfSubLocations, final float surfaceM2)
    {
        super(model, id, locationTypeId, lat, lon, numberOfSubLocations, surfaceM2);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "[MOVABLE]." + this.getId();
    }

}
