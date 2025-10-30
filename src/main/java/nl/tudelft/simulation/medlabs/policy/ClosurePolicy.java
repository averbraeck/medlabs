package nl.tudelft.simulation.medlabs.policy;

import java.util.Collection;

import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * Policy class for the medlabs models. The ClosurePolicy deals with shutting down certain location, and possibly with adapting
 * the activity schedules of persons. The activity schedule changes are induced at midnight of every day by persons checking the
 * policy.
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
public class ClosurePolicy extends Policy
{
    /** the locations to close.. */
    private final Collection<LocationType> closureLocations;

    /**
     * @param model MedlabsModelInterface; the model
     * @param name String; the policy name
     * @param closureLocations the locations to close
     */
    public ClosurePolicy(final MedlabsModelInterface model, final String name, final Collection<LocationType> closureLocations)
    {
        super(model, name);
        this.closureLocations = closureLocations;
    }

    /** close the locations, i.e., enforce the policy. */
    public void close()
    {
        for (LocationType locationType : this.closureLocations)
        {
            for (Location location : locationType.getLocationMap().valueCollection())
            {
                location.setClosed(true);
            }
        }
    }

    /** open the locations again, e.g., when the the policy is lifted. */
    public void open()
    {
        for (LocationType locationType : this.closureLocations)
        {
            for (Location location : locationType.getLocationMap().valueCollection())
            {
                location.setClosed(false);
            }
        }
    }

}
