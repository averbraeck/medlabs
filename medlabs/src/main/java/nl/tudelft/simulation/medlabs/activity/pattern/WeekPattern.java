package nl.tudelft.simulation.medlabs.activity.pattern;

import nl.tudelft.simulation.medlabs.ModelNamed;
import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The WeekPattern is the state machine to provide Activities to a Person. Typically, it is implemented as a list of 7 day
 * patterns, but that is not required -- other implementations, such as randomly generated patterns or full week patterns can
 * exist as well. Still, the WeekPattern is day-aware as statistics are collected per day. This means that activities that span
 * a period across midnight are split into two partial activities: one before midnight, and one after.
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
public interface WeekPattern extends ModelNamed
{
    /**
     * Return the index of this week pattern in the model's weekPatternList.
     * @return int; the index of this week pattern in the model's weekPatternList
     */
    int getId();
    
    /**
     * Return the index of the next activity, after which the activity itself can be retrieved. The meaning of the index is
     * dependent on the implementation; it can be sequential, of it can have a number of slots per day (e.g., 7 slots of 4096
     * possible activities per day). For a Person, the retrieval of an Activity has therefore to be carried out in two steps:
     * 
     * <pre>
     * this.currentActivityIndex = this.currentWeekPattern.getNextActivityIndex(this.currentActivityIndex);
     * this.currentActivity = this.currentWeekPattern.getNextActivity(this.currentActivityIndex);
     * </pre>
     * 
     * The getNextActivityIndex(...) method takes into account day breaks. Suppose that the index of the next activity belongs
     * to the current day, while the day index indicates that we should have an activity on the next day. In that case,
     * dependent on settings in the Activity, either the activity on the 'wrong' day will just continue, or the index is adapted
     * to indicate the first activity of the current day. An example where you want to continue the activity is a situation
     * where a Person is out for the evening and needs to travel back home -- in that case you do want the travel to take place
     * even though it is scheduled at the 'wrong' day. In all cases when an activity is skipped, the location change is still
     * carried out to avoid that a person is stuck at the wrong location. Similarly, if the next activity is one of the next day
     * while the day is not over, a 'dummy' Activity with the CurrentLocator is scheduled till the end of the day to fill the
     * gap. A value of -1 can be returned in case a 'dummy' activity needs to be planned till the end of the day.
     * @param person Person; to set the location for the person doing the request in case activities need to be skipped
     * @param prevActivityIndex int; the index of the previous activity
     * @return int; the index of the next activity; if it has to be stored as a short, cast it before storage
     */
    int getNextActivityIndex(Person person, int prevActivityIndex);

    /**
     * Return the Activity belonging to the provided index. In case of an activityIndex of -1, return a dummy activity till the
     * end of the day.
     * @param activityIndex int; the index within the WeekPattern identifying the activity, or -1 for a dummy activity
     * @return Activity; the Activity belonging to the provided index, or a dummy activity when the index is -1
     */
    Activity getActivity(int activityIndex);

    /**
     * Return the noActivityUntilMidnight pattern which is the dummy activity to fill the gap till midnight at the current
     * location, in case there is a gap in the schedule at the end of the day.
     * @return Activity; the noActivityUntilMidnight pattern which is the dummy activity to fill the gap till midnight
     */
    Activity getNoActivityUntilMidnight();

}
