package nl.tudelft.simulation.medlabs.activity.locator;

import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.index.IdxStudent;

/**
 * The SchoolLocator can locate the school of a Student.
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
public class SchoolLocator implements LocatorInterface
{
    /** local reproducible stream. */
    private StreamInterface stream = null;

    /** Experiment seed. */
    private long seed = 1L;

    /** {@inheritDoc} */
    @Override
    public Location getLocation(final Person person)
    {
        Location schoolLocation = ((IdxStudent) person).getSchoolLocation();
        LocationType slt = schoolLocation.getLocationType();
        if (slt.getFractionActivities() < 1.0 || slt.getFractionOpen() < 1.0)
        {
            // person might be forced to study somewhere else, e.g., at home
            if (slt.getFractionOpen() > 0.0)
            {
                if (this.stream == null)
                {
                    this.seed = person.getModel().getDefaultStream().getOriginalSeed() + "SchoolLocator".hashCode();
                    this.stream = new Java2Random(this.seed);
                }
                this.stream.setSeed(this.seed + schoolLocation.getId()); // reproducible by worklocation id
                if (this.stream.nextDouble() < slt.getFractionOpen())
                {
                    if (slt.getFractionActivities() > 0.0)
                    {
                        this.stream.setSeed(this.seed + person.getId()); // reproducible by person id
                        if (this.stream.nextDouble() < slt.getFractionActivities())
                        {
                            return schoolLocation; // can still go to the school location
                        }
                    }
                }
            }

            LocationType alt = slt.getAlternativeLocationType();
            if (person.getModel().getLocationTypeHouse().getLocationTypeId() == alt.getLocationTypeId())
                return person.getHomeLocation();
            return new NearestLocator(new CurrentLocator(), alt).getLocation(person);
        }

        // location is 100% open!
        return schoolLocation;
    }

}
