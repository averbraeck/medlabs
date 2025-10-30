package nl.tudelft.simulation.medlabs.person;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.Point3d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.medlabs.ModelIdentifiable;
import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekPattern;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.properties.PropertiesInterface;

/**
 * This interface describes the contract of a person. Implementations of the behavior of a person can differ with respect to
 * speed and memory usage. A person should be able to:
 * <ul>
 * <li>execute activities based on a day/week pattern</li>
 * <li>start using a different activity pattern based on disease or policy measures</li>
 * <li>get infected with a disease and go through several stages of infection</li>
 * <li>receive vaccination(s) and report the status of vaccination(s)</li>
 * </ul>
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface Person extends Locatable, Serializable, PropertiesInterface, ModelIdentifiable
{
    /**
     * Initialize the behavior of the person. This has been separated from the constructor, since often other classes such as
     * locations and activity patterns have to be generated as well. The init() method is therefore NOT called from the
     * constructor and has to be called explicitly. Init() takes care of: starting the first activity; setting the initial
     * location for that activity; setting the initial disease state if present; starting the activity scheduling.
     */
    void init();

    /**
     * Execute the current activity, and asynchronously wait for the a call by the activity to the endActivity method.
     */
    void executeStartOfActivity();

    /**
     * Formal end of the activity -- possibly start a next activity.
     */
    void endActivity();

    /** {@inheritDoc} */
    @Override
    default Point3d getLocation() throws RemoteException
    {
        Location location = getCurrentLocation();
        return new Point3d(location.getLongitude(), location.getLatitude(), 1E-6);
    }

    /** Constant for Bounds3d; one degree in meters is 111 km; we make the Bounds3d for clicking 10x10x10 m. */
    Bounds3d PERSON_BOUNDS = new Bounds3d(10.0 / 111000.0, 10.0 / 111000.0, 10.0 / 111000.0);

    /** {@inheritDoc} */
    @Override
    default Bounds3d getBounds() throws RemoteException
    {
        return PERSON_BOUNDS;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters and setters, no real code
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return the age.
     * @return int; the age
     */
    int getAge();

    /**
     * Return the gender; true if female, false if male.
     * @return boolean; the gender
     */
    boolean getGenderFemale();

    /**
     * Set the exposure for a person.
     * @param exposureTime float; the exposure time of the person in hours (simulation clock)
     */
    void setExposureTime(float exposureTime);

    /**
     * @return float; the exposure time of the person in hours (simulation clock)
     */
    float getExposureTime();

    /**
     * @return the diseasePhase
     */
    DiseasePhase getDiseasePhase();

    /**
     * @param diseasePhase DiseasePhase; the diseasePhase to set
     */
    void setDiseasePhase(DiseasePhase diseasePhase);

    /**
     * @return the home location
     */
    Location getHomeLocation();

    /**
     * @return the homeSubLocationIndex
     */
    short getHomeSubLocationIndex();

    /**
     * @param homeSubLocationIndex the homeSubLocationIndex to set
     */
    void setHomeSubLocationIndex(short homeSubLocationIndex);

    /**
     * @return the current location
     */
    Location getCurrentLocation();

    /**
     * Set the current location.
     * @param currentLocation Location; the new location
     */
    void setCurrentLocation(Location currentLocation);

    /**
     * @return the currentSubLocationIndex
     */
    short getCurrentSubLocationIndex();

    /**
     * @param currentSubLocationIndex the currentSubLocationIndex to set
     */
    void setCurrentSubLocationIndex(short currentSubLocationIndex);

    /**
     * @return current activity
     */
    Activity getCurrentActivity();

    /**
     * @return the standard WeekPattern
     */
    WeekPattern getStandardWeekPattern();

    /**
     * @param standardWeekPattern the standard WeekPattern to set
     */
    void setStandardWeekPattern(WeekPattern standardWeekPattern);

    /**
     * @return the current WeekPattern
     */
    WeekPattern getCurrentWeekPattern();

    /**
     * @param currentWeekPattern the current WeekPattern to set
     */
    void setCurrentWeekPattern(WeekPattern currentWeekPattern);

}
