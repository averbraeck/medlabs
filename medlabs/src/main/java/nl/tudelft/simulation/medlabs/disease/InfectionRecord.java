package nl.tudelft.simulation.medlabs.disease;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import nl.tudelft.simulation.medlabs.location.Location;

/**
 * InfectionRecord contains the result of an exposure of uninfected persons to infectious persons over a duration.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InfectionRecord
{
    /**
     * Construct an InfectionRecord.
     * @param exposedPhase DiseasePhase; the disease phase that the person will get after 'successful' exposure
     * @param location Location; the location, since person.getLocation() might not contain the correct info
     */
    public InfectionRecord(final DiseasePhase exposedPhase, final Location location)
    {
        super();
        this.exposedPhase = exposedPhase;
        this.location = location;
    }

    /** The disease phase associated with the state that the person will get after 'successful' exposure. */
    private final DiseasePhase exposedPhase;

    /** The list of infectious persons currently in the (sub)location. */
    private TIntList infectiousPersons = new TIntArrayList();

    /** The list of infected persons in the (sub)location as a result of the exposure. */
    private TIntList infectedPersons = new TIntArrayList();

    /** Whether a transmission calculation was carried out or not. */
    private boolean calculated = false;
    
    /** The location of the infection(s). */
    private final Location location;

    /**
     * @return infectiousPersons
     */
    public TIntList getInfectiousPersons()
    {
        return this.infectiousPersons;
    }

    /**
     * @param infectiousPerson add this infectious person to the list
     */
    public void addInfectiousPerson(final int infectiousPerson)
    {
        this.infectiousPersons.add(infectiousPerson);
    }

    /**
     * @return infectedPersons
     */
    public TIntList getInfectedPersons()
    {
        return this.infectedPersons;
    }

    /**
     * @param infectedPerson add this infectious person to the list
     */
    public void addInfectedPerson(final int infectedPerson)
    {
        this.infectedPersons.add(infectedPerson);
    }

    /**
     * @return calculated
     */
    public boolean isCalculated()
    {
        return this.calculated;
    }

    /**
     * @param calculated set calculated
     */
    public void setCalculated(final boolean calculated)
    {
        this.calculated = calculated;
    }

    /**
     * @return exposedPhase
     */
    public DiseasePhase getExposedPhase()
    {
        return this.exposedPhase;
    }

    /**
     * @return location
     */
    public Location getLocation()
    {
        return this.location;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "InfectionRecord [calculated=" + this.calculated + ", location=" + this.location + ", infectiousPersons="
                + this.infectiousPersons + ", infectedPersons=" + this.infectedPersons + "]";
    }

}
