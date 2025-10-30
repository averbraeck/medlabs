package nl.tudelft.simulation.medlabs.disease;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * State machine for the disease, to calculate how long it takes to get into one of the next phases of the disease.
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
public abstract class DiseaseProgression extends AbstractModelNamed
{
    /** */
    private static final long serialVersionUID = 20140501L;

    /** the phases considered for this disease. */
    private final Map<String, DiseasePhase> diseasePhaseMap = new LinkedHashMap<>();

    /** a safe copy of the disease phases. */
    private List<DiseasePhase> diseasePhaseList = new ArrayList<>();

    /**
     * @param model MedlabsModelInterface; the model
     * @param name String; the name of the disease for reporting
     */
    public DiseaseProgression(final MedlabsModelInterface model, final String name)
    {
        super(model, name);
        Throw.whenNull(model.getRandomStream(), "randomStream should have been initialized before initializing Disease");
    }

    /**
     * Add a disease phase to the map of phases considered for this disease.
     * @param phaseName String; the name of the disease phase (key to find the phase)
     * @param diseaseState DiseaseState; the rough state of the disease for decision making
     * @return DiseasePhase; the disease phase that was just created
     */
    protected DiseasePhase addDiseasePhase(final String phaseName, final DiseaseState diseaseState)
    {
        DiseasePhase diseasePhase = new DiseasePhase(this, phaseName, (byte) this.diseasePhaseList.size(), diseaseState);
        this.diseasePhaseMap.put(phaseName, diseasePhase);
        this.diseasePhaseList.add(diseasePhase);
        return diseasePhase;
    }

    /**
     * Expose a person to the disease. The person will always become infected.
     * @param exposedPerson Person; the person being exposed to the disease
     * @param exposurePhase DiseasePhase; the phase indicating exposure took place
     */
    public abstract void expose(Person exposedPerson, DiseasePhase exposurePhase);

    /**
     * @return a safe copy of the phases belonging to this disease
     */
    public List<DiseasePhase> getDiseasePhases()
    {
        return this.diseasePhaseList;
    }

    /**
     * @param searchName String; the name to look up in the phase map
     * @return the diseasePhase belonging to the name
     */
    public DiseasePhase getDiseasePhase(final String searchName)
    {
        DiseasePhase diseasePhase = this.diseasePhaseMap.get(searchName);
        if (diseasePhase == null)
        {
            System.err.println("Looking up DiseasePhase name " + searchName + " -- non-existent!");
        }
        return diseasePhase;
    }

    /**
     * @param searchIndex byte; the index to look up in the phase list
     * @return the diseasePhase belonging to the index
     */
    public DiseasePhase getDiseasePhase(final byte searchIndex)
    {
        if (searchIndex < 0 || searchIndex >= this.diseasePhaseList.size())
        {
            System.err.println("Looking up DiseasePhase with index " + searchIndex + " -- non-existent!");
        }
        DiseasePhase diseasePhase = this.diseasePhaseList.get(searchIndex);
        return diseasePhase;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Disease [name=" + this.name + "]";
    }

}
