package nl.tudelft.simulation.medlabs.disease;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * DiseaseMonitor creates statistics for the number of people in each DiseasePhase every x hours.
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
public class DiseaseMonitor extends LocalEventProducer
{
    /** */
    private static final long serialVersionUID = 20200927L;

    /** The model. */
    private final MedlabsModelInterface model;

    /** the disease to monitor. */
    private final DiseaseProgression disease;

    /** Reporting interval in hours. */
    private final double intervalHours;

    /** statistics event for an infection event. */
    public static final EventType INFECTION_EVENT = new EventType("INFECTION_EVENT",
            new MetaData("infection", "infection instance with infectious person",
                    new ObjectDescriptor("infected-person", "infected person", Person.class),
                    new ObjectDescriptor("infectious-person", "infectious person", Person.class)));

    /** statistics event for an offspring event. */
    public static final EventType OFFSPRING_EVENT = new EventType("OFFSPRING_EVENT",
            new MetaData("offspring", "number of infected persons by an infectious person in a location",
                    new ObjectDescriptor("infectious-person", "infectious person", Person.class),
                    new ObjectDescriptor("location", "location", Location.class),
                    new ObjectDescriptor("number-infected", "number of infected persons", Integer.class)));

    /**
     * Create a DiseaseMonitor with a reporting interval.
     * @param model MedlabsModelInterface; the model
     * @param disease Disease; the disease for which to set up the monitoring and reporting
     * @param intervalHours double; reporting interval in hours
     */
    public DiseaseMonitor(final MedlabsModelInterface model, final DiseaseProgression disease, final double intervalHours)
    {
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(disease, "disease cannot be null");
        this.model = model;
        this.disease = disease;
        this.intervalHours = intervalHours;
        this.model.getSimulator().scheduleEventRel(0.0, this, "reportDiseaseStatistics", null);
    }

    /**
     * Schedulable method to report statistics every x hours.
     */
    protected void reportDiseaseStatistics()
    {
        for (DiseasePhase diseasePhase : this.disease.getDiseasePhases())
        {
            fireEvent(new TimedEvent<Double>(diseasePhase.DISEASE_STATISTICS_EVENT, diseasePhase.getNumberOfPersons(),
                    this.model.getSimulator().getSimulatorTime()));
        }
        try
        {
            this.model.getSimulator().scheduleEventRel(this.intervalHours, this, "reportDiseaseStatistics", null);
        }
        catch (SimRuntimeException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Report an infection for statistics.
     * @param infectedPerson Person; the person being exposed to the disease
     * @param infectiousPerson Person; the infectious person possibly transmitting the disease
     */
    public void reportInfection(final Person infectedPerson, final Person infectiousPerson)
    {
        fireEvent(new TimedEvent<Double>(INFECTION_EVENT, new Object[] {infectedPerson, infectiousPerson},
                this.model.getSimulator().getSimulatorTime()));
    }

    /**
     * Report offspring for offspring statistics.The method is called when an infectious person leaves a location.
     * @param infectiousPerson Person; the infectious person possibly transmitting the disease
     * @param location Location; location where the person spent time
     * @param nrInfected int; number of infected persons in this location; can be zero
     */
    public void reportOffspring(final Person infectiousPerson, final Location location, final int nrInfected)
    {
        fireEvent(new TimedEvent<Double>(OFFSPRING_EVENT, new Object[] {infectiousPerson, location, nrInfected},
                this.model.getSimulator().getSimulatorTime()));
    }

    /**
     * Return the disease for which this is the monitor.
     * @return Disease; the disease
     */
    public DiseaseProgression getDisease()
    {
        return this.disease;
    }

}
