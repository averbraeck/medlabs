package nl.tudelft.simulation.medlabs.disease;

import org.djutils.event.TimedEventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * DiseasePhase is a class in which a phase of the disease can be registered. Different diseases have different stages or phases
 * we might want to consider. The SEIR model is an often used model (Symptomatic, Exposed, Infected, Recovered) to indicate a
 * number of phases of a transmissable disease. This model can be extended with many other phases, e.g., a symptomatic or
 * asymptomatic development, hospital admission, or ICU admission.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
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
public class DiseasePhase
{
    /** the disease to which this phase belongs. */
    private final DiseaseProgression disease;

    /** name of the disease phase. */
    private final String name;

    /** index of the diseasePhase within the Disease. */
    private final byte index;

    /** the state of the disease, e.g., healthy, ill, or dead. */
    private final DiseaseState diseaseState;

    /** number of persons in this state. */
    private int numberOfPersons = 0;

    /** the statistics event type. */
    @SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
    public TimedEventType DISEASE_STATISTICS_EVENT;

    /**
     * Create a new disease phase with a name.
     * @param disease Disease; the disease to which this phase belongs
     * @param name String; the name of the disease phase
     * @param index int; index of the diseasePhase within the Disease
     * @param diseaseState DiseaseState; rough state for person decisions
     */
    public DiseasePhase(final DiseaseProgression disease, final String name, final byte index, final DiseaseState diseaseState)
    {
        this.disease = disease;
        this.name = name;
        this.index = index;
        this.diseaseState = diseaseState;
        this.DISEASE_STATISTICS_EVENT =
                new TimedEventType("DISEASE_STATISTICS_EVENT_" + disease.getName() + "_" + name, new MetaData(name,
                        disease.getName() + "_" + name, new ObjectDescriptor("number", "number in phase", Integer.class)));
    }

    /**
     * Add a person to this particular phase for statistics.
     */
    public void addPerson()
    {
        this.numberOfPersons++;
    }

    /**
     * Remove a person from this particular phase for statistics.
     */
    public void removePerson()
    {
        this.numberOfPersons--;
    }

    /**
     * Return the number of persona in this particular phase for statistics.
     * @return the number of persons in this phase for statistics
     */
    public int getNumberOfPersons()
    {
        return this.numberOfPersons;
    }

    /**
     * Set the number of persons for this particular phase for statistics.
     * @param numberOfPersons the number of persons in this particular phase
     */
    public void setNumberOfPersons(final int numberOfPersons)
    {
        this.numberOfPersons = numberOfPersons;
    }

    /**
     * Return whether the phase is a 'susceptible' phase.
     * @return boolean; whether the phase is a 'healthy' phase
     */
    public boolean isSusceptible()
    {
        return this.diseaseState.equals(DiseaseState.SUSCEPTIBLE);
    }

    /**
     * Return whether the phase is an 'ill' phase, e.g., exposed, infected, (a)symptomatic infectious in any stage.
     * @return boolean; whether the phase is an 'ill' phase
     */
    public boolean isIll()
    {
        return this.diseaseState.equals(DiseaseState.ILL);
    }

    /**
     * Return whether the phase is a 'dead' phase, so the person can, e.g., be removed from the simulation.
     * @return boolean; whether the phase is a 'dead' phase
     */
    public boolean isDead()
    {
        return this.diseaseState.equals(DiseaseState.DEAD);
    }

    /**
     * Return whether the phase is a 'recovered' phase.
     * @return boolean; whether the phase is a 'recovered' phase
     */
    public boolean isRecovered()
    {
        return this.diseaseState.equals(DiseaseState.RECOVERED);
    }

    /**
     * Return whether the phase is a 'immune' phase.
     * @return boolean; whether the phase is a 'immune' phase
     */
    public boolean isImmune()
    {
        return this.diseaseState.equals(DiseaseState.IMMUNE);
    }

    /**
     * Return the name of the disease phase.
     * @return String; the name of the disease phase
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Return the index number of the disease phase.
     * @return byte; the index number of the disease phase
     */
    public byte getIndex()
    {
        return this.index;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.disease == null) ? 0 : this.disease.hashCode());
        result = prime * result + this.index;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiseasePhase other = (DiseasePhase) obj;
        if (this.disease == null)
        {
            if (other.disease != null)
                return false;
        }
        else if (!this.disease.equals(other.disease))
            return false;
        if (this.index != other.index)
            return false;
        if (this.name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals(other.name))
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "DiseasePhase [disease=" + this.disease + ", name=" + this.name + ", index=" + this.index + ", numberOfPersons="
                + this.numberOfPersons + "]";
    }

}
