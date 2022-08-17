/**
 * 
 */
package nl.tudelft.simulation.medlabs.person.index;

import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Student;
import nl.tudelft.simulation.medlabs.person.Worker;

/**
 * A WorkStudent is a student who also carries out work. WorkStudents have both a work location and a study location.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class IdxWorkStudent extends IdxPerson implements Worker, Student
{
    /** */
    private static final long serialVersionUID = 20201001L;

    /** id of working location where the worker works. */
    private final int workLocationId;

    /** id of school location where the student studies. */
    private final int schoolLocationId;

    /**
     * Create a Worker with a number of basic properties, including the work location type and work location that the worker
     * goes to. The init() method has to be called after the worker has been created to make sure the disease state machine is
     * started for the person if needed. The week pattern starts at day 0 and activity index 0.
     * @param model MedlabsModelInterface; the model
     * @param id int; unique id number of the person in the Model.getPersons() array
     * @param genderFemale boolean; whether gender is female or not.
     * @param age byte; the age of the person
     * @param homeLocationId int; the location of the home in the list of house locations
     * @param weekPatternIndex short; the index of the standard week pattern for the person; this is also the initial week
     *            pattern that the person will use
     * @param workLocationId int; the location index of the work location, relative to the workTypeIndex
     * @param schoolLocationId int; the location index of the school, relative to the schoolTypeIndex
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public IdxWorkStudent(final MedlabsModelInterface model, final int id, final boolean genderFemale, final byte age,
            final int homeLocationId, final short weekPatternIndex, final int workLocationId, final int schoolLocationId)
    {
        super(model, id, genderFemale, age, homeLocationId, weekPatternIndex);
        this.workLocationId = workLocationId;
        this.schoolLocationId = schoolLocationId;
    }

    /** {@inheritDoc} */
    @Override
    public Location getWorkLocation()
    {
        return this.model.getLocationMap().get(this.workLocationId);
    }

    /** {@inheritDoc} */
    @Override
    public Location getSchoolLocation()
    {
        return this.model.getLocationMap().get(this.schoolLocationId);
    }

}
