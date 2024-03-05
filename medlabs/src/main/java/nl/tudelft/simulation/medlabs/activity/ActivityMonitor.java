package nl.tudelft.simulation.medlabs.activity;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.TimedEvent;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * ActivityMonitor is a class that gathers statistics abut activities and their
 * location.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
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
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActivityMonitor implements EventProducer {
	/** */
	private static final long serialVersionUID = 20221030L;

	/** The model. */
	private final MedlabsModelInterface model;

	/** The map of location type name to a 0-based unique number. */
	private final Map<String, Integer> locationTypeToNrMap = new LinkedHashMap<>();

	/** The map of person type name to a 0-based unique number. */
	private final Map<String, Integer> personTypeToNrMap = new LinkedHashMap<>();

	/**
	 * The current-day array of hours per location type per person type. Totals are
	 * at index 0.
	 */
	private final List<TIntDoubleMap> dayHoursPerLocPerPerson = new ArrayList<>();

	/**
	 * The total array of hours per location type per person type. Totals are at
	 * index 0.
	 */
	private final List<TIntDoubleMap> totHoursPerLocPerPerson = new ArrayList<>();

	/** shortcut to total daymap. */
	TIntDoubleMap dayMap0;

	/** shortcut to total totmap. */
	TIntDoubleMap totMap0;

	/** statistics event for daily hours per location type per person type. */
	public static final EventType ACTIVITY_DAY_STATISTICS_EVENT = new EventType("ACTIVITY_DAY_STATISTICS_EVENT",
			new MetaData("acthours/loctype/persontype", "daily activity hours per location type per person type",
					new ObjectDescriptor("acthours/loctype/persontype",
							"daily activity hours per location type per person type", List.class),
					new ObjectDescriptor("loctype-nr", "map of location type name to number", Map.class),
					new ObjectDescriptor("persontype-nr", "map of person type name to number", Map.class)));

	/**
	 * statistics event for cumulative total hours per location type per person
	 * type.
	 */
	public static final EventType ACTIVITY_TOT_STATISTICS_EVENT = new EventType("ACTIVITY_TOT_STATISTICS_EVENT",
			new MetaData("totacthours/loctype/persontype", "total activity hours per location type per person type",
					new ObjectDescriptor("totacthours/loctype/persontype",
							"total activity hours per location type per person type", List.class),
					new ObjectDescriptor("loctype-nr", "map of location type name to number", Map.class),
					new ObjectDescriptor("persontype-nr", "map of person type name to number", Map.class)));

	/**
	 * Create a monitor for persons that can report events about persons.
	 * 
	 * @param model MedlabsModelInterface; the model
	 */
	public ActivityMonitor(final MedlabsModelInterface model) {
		this.model = model;
		// add the total columns as #0
		this.locationTypeToNrMap.put("TOTAL", 0);
		this.personTypeToNrMap.put("TOTAL", 0);
		this.dayHoursPerLocPerPerson.add(new TIntDoubleHashMap());
		this.dayHoursPerLocPerPerson.get(0).put(0, 0.0);
		this.totHoursPerLocPerPerson.add(new TIntDoubleHashMap());
		this.totHoursPerLocPerPerson.get(0).put(0, 0.0);
		this.dayMap0 = this.dayHoursPerLocPerPerson.get(0);
		this.totMap0 = this.totHoursPerLocPerPerson.get(0);
		// make sure the event is scheduled AFTER the midnight reset for the activities
		// to capture a full day
		this.model.getSimulator().scheduleEventRel(24.001, this, this, "reportActivityStatistics", null);
	}

	/**
	 * Schedulable method to report statistics every 24 hours.
	 */
	protected void reportActivityStatistics() {
		// send the maps to any listeners (e.g., the ResultWriter)
		try {
			this.fireEvent(new TimedEvent<Double>(
					ACTIVITY_DAY_STATISTICS_EVENT, new Object[] { (Serializable) this.dayHoursPerLocPerPerson,
							this.locationTypeToNrMap, this.personTypeToNrMap },
					this.model.getSimulator().getSimulatorTime()));
			this.fireEvent(new TimedEvent<Double>(
					ACTIVITY_TOT_STATISTICS_EVENT, new Object[] { (Serializable) this.totHoursPerLocPerPerson,
							this.locationTypeToNrMap, this.personTypeToNrMap },
					this.model.getSimulator().getSimulatorTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// clean the daily map
		for (int locnr : this.locationTypeToNrMap.values()) {
			for (int ptnr : this.personTypeToNrMap.values()) {
				this.dayHoursPerLocPerPerson.get(locnr).put(ptnr, 0.0);
			}
		}
		// schedule the next day's event
		try {
			this.model.getSimulator().scheduleEventRel(24.0, this, this, "reportActivityStatistics", null);
		} catch (SimRuntimeException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Report a duration for an activity of a person.
	 * 
	 * @param locationType String; the LocationType to report to. Since it is a
	 *                     String, it can be a special type.
	 * @param personType   String; the person type to report about
	 * @param hours        double; the number of hours to report
	 */
	public void addActivityTime(final String locationType, final String personType, final double hours) {
		Integer locTypeNr = this.locationTypeToNrMap.get(locationType);
		if (locTypeNr == null) {
			locTypeNr = this.locationTypeToNrMap.size();
			this.locationTypeToNrMap.put(locationType, locTypeNr);
			this.dayHoursPerLocPerPerson.add(new TIntDoubleHashMap());
			this.dayHoursPerLocPerPerson.get(locTypeNr).put(locTypeNr, 0.0); // totals
			this.totHoursPerLocPerPerson.add(new TIntDoubleHashMap());
			this.totHoursPerLocPerPerson.get(locTypeNr).put(0, 0.0); // totals
		}
		Integer personTypeNr = this.personTypeToNrMap.get(personType);
		if (personTypeNr == null) {
			personTypeNr = this.personTypeToNrMap.size();
			this.personTypeToNrMap.put(personType, personTypeNr);
			for (int locnr : this.locationTypeToNrMap.values()) {
				this.dayHoursPerLocPerPerson.get(locnr).put(personTypeNr, 0.0);
				this.totHoursPerLocPerPerson.get(locnr).put(personTypeNr, 0.0);
			}
		}
		TIntDoubleMap dayMap = this.dayHoursPerLocPerPerson.get(locTypeNr);
		TIntDoubleMap totMap = this.totHoursPerLocPerPerson.get(locTypeNr);
		dayMap.put(personTypeNr, dayMap.get(personTypeNr) + hours);
		totMap.put(personTypeNr, totMap.get(personTypeNr) + hours);
		dayMap.put(0, dayMap.get(0) + hours); // total per location
		totMap.put(0, totMap.get(0) + hours); // cumulative total per location
		this.dayMap0.put(0, this.dayMap0.get(0) + hours); // total per day
		this.totMap0.put(0, this.totMap0.get(0) + hours); // cumulative total per day
		this.dayMap0.put(personTypeNr, this.dayMap0.get(personTypeNr) + hours);
		this.totMap0.put(personTypeNr, this.totMap0.get(personTypeNr) + hours);
	}

	/** {@inheritDoc} */
	public Serializable getSourceId() {
		return "ActivityMonitor";
	}

	@Override
	public EventListenerMap getEventListenerMap() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
