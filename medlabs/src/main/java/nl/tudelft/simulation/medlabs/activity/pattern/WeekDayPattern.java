package nl.tudelft.simulation.medlabs.activity.pattern;

import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.activity.TravelActivity;
import nl.tudelft.simulation.medlabs.activity.UntilFixedTimeActivity;
import nl.tudelft.simulation.medlabs.activity.locator.CurrentLocator;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The WeekPattern contains seven DayPatterns for a type of Person. This
 * implementation uses a maximum of 4096 slots per day, so the
 * <code>weekIndex = day * 4096 + dayIndex</code>, and
 * <code>dayIndex = weekIndex & 0xfff</code>.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved. The MEDLABS project (Modeling
 * Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at
 * providing policy analysis tools to predict and help contain the spread of
 * epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information
 * <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research
 * of Mingxin Zhang at TU Delft and is described in the PhD thesis "Large-Scale
 * Agent-Based Social Simulation" (2016). This software is licensed under the
 * BSD license. See license.txt in the main project.
 * </p>
 * 
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WeekDayPattern extends AbstractModelNamed implements WeekPattern {
	/** */
	private static final long serialVersionUID = 20211230L;

	/** id to look up fast. */
	private final short id;

	/** pattern for the 7 days of the week, 0 = Monday. */
	private final DayPattern[] dayPatternArray = new DayPattern[7];

	/** Static constant for NoActivity. */
	private static Activity noActivityUntilMidnight;

	/**
	 * Create a WeekPatttern and register it properly in the Model.
	 * 
	 * @param model MedlabsModelInterface; the model
	 * @param name  the name of the week pattern
	 */
	private WeekDayPattern(final MedlabsModelInterface model, final String name) {
		super(model, name);
		this.model.getWeekPatternMap().put(name, this);
		this.model.getWeekPatternList().add(this);
		this.id = (short) this.model.getWeekPatternList().indexOf(this);
		noActivityUntilMidnight = new UntilFixedTimeActivity(model, "NoActivity", new CurrentLocator(), 24.0);
	}

	/**
	 * Create a WeekPatttern where all days are the same. The WeekPattern will be
	 * registered in the Model.
	 * 
	 * @param model      MedlabsModelInterface; the model
	 * @param name       String; the week pattern name
	 * @param dayPattern DayPattern; the pattern for ALL 7 days
	 */
	public WeekDayPattern(final MedlabsModelInterface model, final String name, final DayPattern dayPattern) {
		this(model, name);
		for (int i = 0; i < 7; i++) {
			this.dayPatternArray[i] = dayPattern;
		}
	}

	/**
	 * Create a WeekPatttern with weekdays and weekend days. The WeekPattern will be
	 * registered in the Model.
	 * 
	 * @param model          MedlabsModelInterface; the model
	 * @param name           String; the week pattern name
	 * @param weekDayPattern DayPattern; the pattern for days 0-4 (Mo-Fr)
	 * @param weekendPattern DayPattern; the pattern for days 5-6 (Sa-Su)
	 */
	public WeekDayPattern(final MedlabsModelInterface model, final String name, final DayPattern weekDayPattern,
			final DayPattern weekendPattern) {
		this(model, name);
		for (int i = 0; i < 5; i++) {
			this.dayPatternArray[i] = weekDayPattern;
		}
		for (int i = 5; i < 7; i++) {
			this.dayPatternArray[i] = weekendPattern;
		}
	}

	/**
	 * Create a WeekPatttern where all days are different. The WeekPattern will be
	 * registered in the Model.
	 * 
	 * @param model       MedlabsModelInterface; the model
	 * @param name        String; the week pattern name
	 * @param dayPatterns DayPattern[7]; different patterns for 7 days, starting
	 *                    with Monday
	 */
	public WeekDayPattern(final MedlabsModelInterface model, final String name, final DayPattern[] dayPatterns) {
		this(model, name);
		for (int i = 0; i < 7; i++) {
			this.dayPatternArray[i] = dayPatterns[i];
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getNextActivityIndex(final Person person, final int prevActivityIndex) {
		// make sure the weekday overflows to the next day at midnight (add 36 seconds)
		int weekDay = ((int) Math.floor((this.model.getSimulator().getSimulatorTime() + 0.01) / 24)) % 7;
		if (prevActivityIndex == -1) {
			// return activity 0 of the next day
			return weekDay << 12;
		}
		int newIndex = prevActivityIndex + 1;
		int prevWeekDay = prevActivityIndex >> 12;
		int prevSize = this.dayPatternArray[prevWeekDay].size();
		if (prevWeekDay == weekDay && (newIndex & 0xfff) >= prevSize) {
			// there are no more activities for the current day -- fill with dummy activity
			// till the end of the day
			return -1;
		}
		if (prevWeekDay != weekDay) {
			// if there are no more activities for the current day: go to the next one
			if ((newIndex & 0xfff) == prevSize) {
				return weekDay << 12;
			}
			// otherwise: if the next activity is a 'rollover' one, just start it, although
			// it is out of sync
			if (getActivity(newIndex).isStartAfterMidnight()) {
				return newIndex;
			}
			// otherwise, put the person at the (destination) location of the last activity
			Activity lastDayActivity = this.dayPatternArray[prevWeekDay].getLast();
			if (lastDayActivity instanceof TravelActivity) {
				person.setCurrentLocation(((TravelActivity) lastDayActivity).getEndLocation(person));
			} else {
				person.setCurrentLocation(lastDayActivity.getActivityLocation(person));
			}
			return weekDay << 12;
		}
		// just return the next activity of the current day
		return newIndex;
	}

	/** {@inheritDoc} */
	@Override
	public Activity getActivity(final int activityIndex) {
		if (activityIndex == -1) {
			// gap to fill till the end of the day
			return noActivityUntilMidnight;
		}
		int weekDay = activityIndex >> 12;
		int dayIndex = activityIndex & 0xfff;
		if (dayIndex >= this.dayPatternArray[weekDay].size()) {
			// can happen after schedule change
			return noActivityUntilMidnight;
		}
		return this.dayPatternArray[weekDay].get(dayIndex);
	}

	/**
	 * Return the array of seven day patterns, starting with Monday.
	 * 
	 * @return DayPattern[7]; the array of 7 DayPatterns
	 */
	public DayPattern[] getDayPatternArray() {
		return this.dayPatternArray;
	}

	/** {@inheritDoc} */
	@Override
	public int getId() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	public Activity getNoActivityUntilMidnight() {
		return noActivityUntilMidnight;
	}

}
