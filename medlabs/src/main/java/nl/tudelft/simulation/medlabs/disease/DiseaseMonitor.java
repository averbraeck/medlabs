package nl.tudelft.simulation.medlabs.disease;

import java.io.Serializable;

import org.djunits.Throw;
import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;

import nl.tudelft.simulation.dsol.SimRuntimeException;
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
public class DiseaseMonitor extends EventProducer
{
    /** */
    private static final long serialVersionUID = 20200927L;

    /** The model. */
    private final MedlabsModelInterface model;

    /** the disease to monitor. */
    private final DiseaseProgression disease;

    /** Reporting interval in hours. */
    private final double intervalHours;

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
        this.model.getSimulator().scheduleEventRel(0.0, this, this, "reportDiseaseStatistics", null);
    }

    /**
     * Schedulable method to report statistics every x hours.
     */
    protected void reportDiseaseStatistics()
    {
        for (DiseasePhase diseasePhase : this.disease.getDiseasePhases())
        {
            this.fireEvent(new TimedEvent<Double>(diseasePhase.DISEASE_STATISTICS_EVENT, this,
                    diseasePhase.getNumberOfPersons(), this.model.getSimulator().getSimulatorTime()));
        }
        try
        {
            this.model.getSimulator().scheduleEventRel(this.intervalHours, this, this, "reportDiseaseStatistics", null);
        }
        catch (SimRuntimeException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Report an infection for potential offspring counting.
     * @param infectedPerson Person; the person being exposed to the disease
     * @param infectiousPerson Person; the infectious person possibly transmitting the disease
     */
    public void reportInfection(final Person infectedPerson, final Person infectiousPerson)
    {
        System.out.println("Day " + this.model.getDay() + ": " + infectiousPerson + " infected " + infectedPerson);
    }
    
    /**
     * Return the disease for which this is the monitor.
     * @return Disease; the disease
     */
    public DiseaseProgression getDisease()
    {
        return this.disease;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "diseaseMonitor";
    }

}
