package nl.tudelft.simulation.medlabs.activity.pattern;

import java.util.List;

import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * A DayPattern contains a 24 hour pattern of activities.
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
public class DayPattern
{
    /** pointer to the model for retrieving simulator and other relevant information. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected MedlabsModelInterface model;

    /** the 24 hour pattern. */
    private final List<Activity> activities;

    /**
     * Create a 24-hour day pattern consisting of a sequence of activities.
     * @param model MedlabsModelInterface; the model
     * @param activities a sequential list of activities belonging to this day pattern
     */
    public DayPattern(final MedlabsModelInterface model, final List<Activity> activities)
    {
        this.model = model;
        this.activities = activities;
    }

    /**
     * Return the activities list.
     * @return List&lt;Activity&gt;; the activities list
     */
    public List<Activity> getActivities()
    {
        return this.activities;
    }

    /**
     * Return the activity at position index.
     * @param index int; the index of the activity
     * @return Activity; the activity at position index
     */
    public Activity get(final int index)
    {
        return this.activities.get(index);
    }

    /**
     * Return the last activity for the day.
     * @return Activity; the last activity for the day
     */
    public Activity getLast()
    {
        return get(this.activities.size() - 1);
    }

    /**
     * Return the size of the activities list.
     * @return int; the size of the activities list
     */
    public int size()
    {
        return this.activities.size();
    }

}
