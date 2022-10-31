package nl.tudelft.simulation.medlabs.person.index;

import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekPattern;
import nl.tudelft.simulation.medlabs.common.MedlabsRuntimeException;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.AbstractPerson;
import nl.tudelft.simulation.medlabs.person.PersonType;
import nl.tudelft.simulation.medlabs.simulation.TinySimEvent;

/**
 * A Person is an Agent with a number of characteristics such as age, locations, a disease status, and an activity pattern. The
 * IdxPerson is implemented using int indexes as much as possible, saving 4 bytes of memory for each attribute as compared to a
 * pointer to an object.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class IdxPerson extends AbstractPerson
{
    /** */
    private static final long serialVersionUID = 20201001L;

    /** index of current week pattern, can be standard or adapted pattern due to lockdown or illness. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short currentWeekPatternIndex;

    /** standard day pattern, to be able to return to 'normal' after lockdown or illness. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short standardWeekPatternIndex;

    /** index of the current activity in the week pattern. -1 means dummy activity till the end of the day. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short activityIndex = 0;

    /** The start time of an activity to calculate its duration. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected float activityStartTime = 0.0f;

    /** id of home location where the person lives. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected int homeLocationId;

    /** sublocation index of the home where the person lives (to get families living together). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short homeSubLocationIndex;

    /** index of the current location of the person. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected int currentLocationId;

    /** sublocation index where the person performs the activity. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short currentSubLocationIndex;

    /** Time when the person got exposed to the disease and will become infected. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected float exposureTime;

    /** disease phase index, belonging to the disease index. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected byte diseasePhaseIndex;

    /**
     * Create a Person with a number of basic properties. The init() method has to be called after the person has been created
     * to make sure the disease state machine is started for the person if needed. The week pattern starts at day 0 and activity
     * index 0. In the latest version, persons are not initialized with a Disease anymore -- the disease is set to certain
     * persons AFTER the instantiation of the person.
     * @param model MedlabsModelInterface; the model
     * @param id int; unique id number of the person in the Model.getPersons() array
     * @param genderFemale boolean; whether gender is female or not.
     * @param age byte; the age of the person
     * @param homeLocationId int; the location of the home
     * @param weekPatternIndex short; the index of the standard week pattern for the person; this is also the initial week
     *            pattern that the person will use
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public IdxPerson(final MedlabsModelInterface model, final int id, final boolean genderFemale, final byte age,
            final int homeLocationId, final short weekPatternIndex)
    {
        super(model, id, genderFemale, age);
        this.homeLocationId = homeLocationId;
        this.currentLocationId = homeLocationId;
        this.currentWeekPatternIndex = weekPatternIndex;
        this.standardWeekPatternIndex = weekPatternIndex;
        this.model.getPersonMap().put(id, this);
        this.exposureTime = Float.NaN;
        this.diseasePhaseIndex = -1;

        PersonType pt = this.model.getPersonTypeClassMap().get(getClass());
        if (pt == null)
        {
            throw new MedlabsRuntimeException("PersonType " + getClass().getSimpleName() + " not registered in model");
        }
        pt.incNumberPersons();

        // DON'T FORGET TO CALL INIT AFTER THE CONSTRUCTION OF A PERSON!
    }

    /** {@inheritDoc} */
    @Override
    public void init()
    {
        getCurrentLocation().addPerson(this);
        try
        {
            this.model.getSimulator().scheduleEvent(
                    new TinySimEvent(this.model.getSimulator().getSimulatorTime(), this, this, "executeStartOfActivity", null));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void executeStartOfActivity()
    {
        try
        {
            getCurrentActivity().startActivity(this);
            this.activityStartTime = this.model.getSimulator().getSimulatorTime().floatValue();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void endActivity()
    {
        if (getDiseasePhase().isDead())
        {
            // TODO: PersonType pt = this.model.getPersonTypeClassMap().get(getClass());
            // TODO: pt.decNumberPersons();
            return;
        }
        float now = this.model.getSimulator().getSimulatorTime().floatValue();
        double activityHours = now - this.activityStartTime;
        this.activityStartTime = now;
        this.model.getActivityMonitor().addActivityTime(getCurrentLocation().getLocationType().getName(),
                getClass().getSimpleName(), activityHours);
        WeekPattern currentWeekPattern = getCurrentWeekPattern();
        this.activityIndex = (short) currentWeekPattern.getNextActivityIndex(this, this.activityIndex);
        // NOTE: the startActivity() method SHOULD be the last statement of endActivity()
        // A dummy travel activity, or a void activity calls endActivity AGAIN
        currentWeekPattern.getActivity(this.activityIndex).startActivity(this);
    }

    /** {@inheritDoc} */
    @Override
    public void changePhase(final DiseasePhase nextPhase)
    {
        this.model.getDiseaseProgression().changeDiseasePhase(this, nextPhase);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[id=" + getId() + ", age=" + getAge() + ", " + getCurrentLocation() + "."
                + this.currentSubLocationIndex + ", phase="
                + (getDiseasePhase() == null ? "HEALTHY" : getDiseasePhase().getName()) + "]";
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters and setters, no real code
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** {@inheritDoc} */
    @Override
    public Location getCurrentLocation()
    {
        return this.model.getLocationMap().get(this.currentLocationId);
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrentLocation(final Location currentLocation)
    {
        this.currentLocationId = currentLocation.getId();
    }

    /** {@inheritDoc} */
    @Override
    public short getCurrentSubLocationIndex()
    {
        return this.currentSubLocationIndex;
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrentSubLocationIndex(final short subLocationIndex)
    {
        this.currentSubLocationIndex = subLocationIndex;
    }

    /** {@inheritDoc} */
    @Override
    public Location getHomeLocation()
    {
        return this.model.getLocationMap().get(this.homeLocationId);
    }

    /** {@inheritDoc} */
    @Override
    public void setHomeSubLocationIndex(final short homeSubLocationIndex)
    {
        this.homeSubLocationIndex = homeSubLocationIndex;
    }

    /** {@inheritDoc} */
    @Override
    public short getHomeSubLocationIndex()
    {
        return this.homeSubLocationIndex;
    }

    /** {@inheritDoc} */
    @Override
    public DiseasePhase getDiseasePhase()
    {
        return this.model.getDiseaseProgression().getDiseasePhase(this.diseasePhaseIndex);
    }

    /**
     * @param diseasePhase DiseasePhase; the diseasePhase to set
     */
    @Override
    public void setDiseasePhase(final DiseasePhase diseasePhase)
    {
        this.diseasePhaseIndex = diseasePhase.getIndex();
        // see at midnight if the activity pattern needs to be changed
        getModel().getSimulator().scheduleEventAbs(24.0 * Math.ceil(getModel().getSimulator().getSimulatorTime() / 24.0), this,
                getModel(), "checkChangeActivityPattern", new Object[] {this});
    }

    /** {@inheritDoc} */
    @Override
    public float getExposureTime()
    {
        return this.exposureTime;
    }

    /** {@inheritDoc} */
    @Override
    public void setExposureTime(final float exposureTime)
    {
        this.exposureTime = exposureTime;
    }

    /** {@inheritDoc} */
    @Override
    public Activity getCurrentActivity()
    {
        return getCurrentWeekPattern().getActivity(this.activityIndex);
    }

    /** {@inheritDoc} */
    @Override
    public WeekPattern getStandardWeekPattern()
    {
        return this.model.getWeekPatternList().get(this.standardWeekPatternIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void setStandardWeekPattern(final WeekPattern standardWeekPattern)
    {
        this.standardWeekPatternIndex = (short) standardWeekPattern.getId();
    }

    /** {@inheritDoc} */
    @Override
    public WeekPattern getCurrentWeekPattern()
    {
        return this.model.getWeekPatternList().get(this.currentWeekPatternIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrentWeekPattern(final WeekPattern currentWeekPattern)
    {
        // TODO: set the activity to -1 to avoid problems with retrieving the previous activity
        this.currentWeekPatternIndex = (short) currentWeekPattern.getId();
    }

}
