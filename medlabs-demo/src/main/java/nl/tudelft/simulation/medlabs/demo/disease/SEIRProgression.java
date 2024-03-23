package nl.tudelft.simulation.medlabs.demo.disease;

import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.disease.DiseaseProgression;
import nl.tudelft.simulation.medlabs.disease.DiseaseState;
import nl.tudelft.simulation.medlabs.disease.DurationDistribution;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * The SEIRProgression model implements a state machine for disease progression.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * code is part of the HERoS project (Health Emergency Response in Interconnected Systems), which builds on the MEDLABS project.
 * The simulation tools are aimed at providing policy analysis tools to predict and help contain the spread of epidemics. They
 * make use of the DSOL simulation engine and the agent-based modeling formalism. This software is licensed under the BSD
 * license. See license.txt in the main project.
 * </p>
 * @author <a href="https://www.linkedin.com/in/mikhailsirenko">Mikhail Sirenko</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SEIRProgression extends DiseaseProgression
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * "S": Susceptible disease phase. This is the normal 'base' phase of every person.
     */
    public static DiseasePhase susceptible;

    /**
     * "E": Exposed disease phase. Note: the exposure date is stored with the Person. Exposed means the person WILL get ill.
     */
    public static DiseasePhase exposed;

    /** "I": Infected disease phase. */
    public static DiseasePhase infected;

    /** "R": Recovered disease phase. */
    public static DiseasePhase recovered;

    /** E -> I period: duration of the incubation period. */
    private DurationDistribution distIncubationPeriod;

    /**
     * I -> R period. The probability is assumed to be 1 (all asymptomatic persons recover).
     */
    private DurationDistribution distInfectedToRecovery;

    /**
     * Create the SEIR Progression model. A state machine is instantiated with probabilities for the state transitions and
     * durations between states.
     * 
     * <pre>
     * The following parameters need to be specified for the SEIR state machine:
     * 
     * The transition S -> E                            is determined by the Transmission model
     * Probability and Duration distribution E -> I     we call this the incubation period 
     * Duration distribution I -> R                     we assume I always leads to R
     * </pre>
     * 
     * @param model MedlabsModelInterface; the Medlabs model
     */
    public SEIRProgression(final MedlabsModelInterface model)
    {
        super(model, "SEIR");

        susceptible = addDiseasePhase("Susceptible", DiseaseState.SUSCEPTIBLE);
        exposed = addDiseasePhase("Exposed", DiseaseState.ILL);
        infected = addDiseasePhase("Infected-Asymptomatic", DiseaseState.ILL);
        recovered = addDiseasePhase("Recovered", DiseaseState.RECOVERED);

        // -------------------------------------------------------------
        // Key parameters/uncertainties
        // -------------------------------------------------------------

        // 1-3 days, symmetric triangular
        this.distIncubationPeriod =
                new DurationDistribution(new DistTriangular(this.model.getRandomStream(), 24.0, 48.0, 72.0), TimeUnit.HOUR);

        // 5-15 days, symmetric triangular.
        this.distInfectedToRecovery = new DurationDistribution(
                new DistTriangular(this.model.getRandomStream(), 9 * 24.0, 10 * 24.0, 11 * 24.0), TimeUnit.HOUR);
    }

    // -------------------------------------------------------------
    // Progression model
    // -------------------------------------------------------------


    /** {@inheritDoc} */
    @Override
    public boolean expose(final Person exposedPerson, final DiseasePhase exposurePhase)
    {
        exposedPerson.getDiseasePhase().removePerson();
        exposedPerson.setDiseasePhase(exposed);
        exposed.addPerson();
        double incubationPeriod = this.distIncubationPeriod.getDuration();

        this.model.getSimulator().scheduleEventRel(incubationPeriod, this, this, "changeDiseasePhase",
                new Object[] {exposedPerson, SEIRProgression.infected});
        return true;
    }
    
    /**
     * Update the disease phase for a person.
     * @param person Person; 
     * @param nextPhase DiseasePhase; 
     */
    protected void changeDiseasePhase(final Person person, final DiseasePhase nextPhase)
    {
        MedlabsModelInterface model = person.getModel();

        // -------------------------------------------------------------
        // Exposed
        // -------------------------------------------------------------

        if (nextPhase == exposed)
        {
            System.err.println("Should have been handled with expose(...) method");
            expose(person, exposed);
        }

        // -------------------------------------------------------------
        // Infected asymptomatic contagious
        // -------------------------------------------------------------

        else if (nextPhase == infected)
        {
            person.getDiseasePhase().removePerson();
            person.setDiseasePhase(infected);
            infected.addPerson();

            model.getSimulator().scheduleEventRel(this.distInfectedToRecovery.getDuration(), TimeUnit.HOUR, this, this,
                    "changeDiseasePhase", new Object[] {person, recovered});
            return;
        }

        // -------------------------------------------------------------
        // Recovered
        // -------------------------------------------------------------

        else if (nextPhase == recovered)
        {
            this.model.getPersonMonitor().reportDeathPerson(person);

            person.getDiseasePhase().removePerson();
            person.setDiseasePhase(recovered);
            recovered.addPerson();
            return;
        }

        // -------------------------------------------------------------
        // ERROR
        // -------------------------------------------------------------

        else
        {
            System.err.println("ERROR: Person " + person + " has unknown next disease phase " + nextPhase);
        }
    }

}
