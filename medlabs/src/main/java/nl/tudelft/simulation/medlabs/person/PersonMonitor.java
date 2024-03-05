package nl.tudelft.simulation.medlabs.person;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.TimedEvent;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * PersonMonitor fires person related events for graphs and statistics.
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
public class PersonMonitor implements EventProducer {
	/** */
	private static final long serialVersionUID = 1L;

	/** statistics update event for infection. */
	@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
	public EventType[] INFECT_AGE_PER_DAY_EVENT = new EventType[11];

	/** statistics update event for infection. */
	private int[] infectionsPerAgeBracketPerDay = new int[11];

	/** statistics update event for infection. */
	public static final EventType INFECT_AGE_PER_HOUR_EVENT = new EventType("INFECT_AGE_PER_HOUR_EVENT",
			new MetaData("infect_nr/h/age", "number of persons infected per age bracket per hour", new ObjectDescriptor(
					"infect_nr/h/age", "number of persons infected per age bracket per hour", int[].class)));

	/** statistics update event for infection. */
	private int[] infectionsPerAgeBracketPerHour = new int[11];

	/** statistics update event for infection. */
	@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
	public Map<LocationType, EventType> INFECT_LOCATIONTYPE_PER_DAY_EVENT = new LinkedHashMap<>();

	/** statistics update event for infection. */
	private Map<LocationType, Integer> infectionsPerLocationTypePerDay = new LinkedHashMap<>();

	/** statistics update event for infection. */
	@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
	public Map<LocationType, EventType> INFECT_LOCATIONTYPE_PER_HOUR_EVENT = new LinkedHashMap<>();

	/** statistics update event for infection. */
	public static final EventType INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT = new EventType(
			"INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT",
			new MetaData("infect_nr/h/loctype", "number of persons infected per location type per hour",
					new ObjectDescriptor("infect_nr/h/loctype", "number of persons infected per location type per hour",
							Map.class)));

	/** statistics update event for infection. */
	private Map<LocationType, Integer> infectionsPerLocationTypePerHour = new LinkedHashMap<>();

	/** statistics update event for death. */
	public static final EventType DEATHS_AGE_PER_DAY_EVENT = new EventType("DEATHS_AGE_PER_DAY_EVENT",
			new MetaData("deaths/d/age", "number of deaths per age bracket per day",
					new ObjectDescriptor("deaths/d/age", "number of deaths per age bracket per day", int[].class)));;

	/** statistics update event for death. */
	private int[] deathsPerAgeBracketPerDay = new int[11];

	/** statistics update event for death. */
	public static final EventType DEATH_EVENT = new EventType("DEATH_EVENT", new MetaData("deaths per age",
			"deaths per age", new ObjectDescriptor("deaths per age", "deaths per age", Integer.class)));

	/** statistics update event for age distribution (at model start). */
	public static final EventType AGE_EVENT = new EventType("AGE_EVENT",
			new MetaData("age", "age", new ObjectDescriptor("age", "age", Integer.class)));

	/** statistics update event for family composition (at model start). */
	public static final EventType FAMILY_EVENT = new EventType("FAMILY_EVENT", new MetaData("family size",
			"family size", new ObjectDescriptor("family size", "family size", Integer.class)));

	/** event for infected person. */
	public static final EventType INFECTED_PERSON_EVENT = new EventType("INFECTED_PERSON_EVENT");

	/** event for person who died. */
	public static final EventType DEAD_PERSON_EVENT = new EventType("DEAD_PERSON_EVENT", new MetaData("dead person",
			"dead person", new ObjectDescriptor("dead person", "dead person", Person.class)));

	/** event for infections per person type per day. */
	public static final EventType DAY_INFECTIONS_PERSON_TYPE = new EventType("DAY_INFECTIONS_PERSON_TYPE");

	/** event for cumulative infections per person type per day. */
	public static final EventType TOT_INFECTIONS_PERSON_TYPE = new EventType("TOT_INFECTIONS_PERSON_TYPE");

	/** event for infections from person type to person type per day. */
	public static final EventType DAY_INFECTIONS_PERSON_TO_PERSON_TYPE = new EventType(
			"DAY_INFECTIONS_PERSON_TO_PERSON_TYPE");

	/** event for cumulative infections from person type to person type. */
	public static final EventType TOT_INFECTIONS_PERSON_TO_PERSON_TYPE = new EventType(
			"TOT_INFECTIONS_PERSON_TO_PERSON_TYPE");

	/**
	 * event for infections per location type from person type to person type per
	 * day.
	 */
	public static final EventType DAY_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE = new EventType(
			"DAY_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE");

	/**
	 * event for cumulative infections per location type from person type to person
	 * type.
	 */
	public static final EventType TOT_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE = new EventType(
			"TOT_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE");

	/** event for infections by infection rate. */
	public static final EventType INFECTION_BY_RATE = new EventType("INFECTION_BY_RATE");

	/** event for infections by infection rate factor. */
	public static final EventType INFECTION_BY_RATE_FACTOR = new EventType("INFECTION_BY_RATE_FACTOR");

	/**
	 * Number of infections per person type today till the current moment -- reset
	 * to 0 at midnight.
	 */
	private TIntIntMap dayInfectionsPersonType = new TIntIntHashMap();

	/**
	 * Number of infections per person type for yesterday. Filled every 24 hours at
	 * midnight
	 */
	private TIntIntMap yesterdayInfectionsPersonType = new TIntIntHashMap();

	/**
	 * Cumulative number of infections per person type.
	 */
	private TIntIntMap totInfectionsPersonType = new TIntIntHashMap();

	/**
	 * Number of infections from person type to person type per day. The key of the
	 * map is ((infectingPersonTypeId << 16) + infectedPersonTypeId).
	 */
	private TIntIntMap dayInfectionsPersonTypeToPersonType = new TIntIntHashMap();

	/**
	 * Cumulative number of infections from person type to person type. The key of
	 * the map is ((infectingPersonTypeId << 16) + infectedPersonTypeId)
	 */
	private TIntIntMap totInfectionsPersonTypeToPersonType = new TIntIntHashMap();

	/**
	 * Number of infections per location type from person type to person type per
	 * day. The key of the map is (((infectingLocationTypeId + 128 << 20)) +
	 * (infectingPersonTypeId << 10) + infectedPersonTypeId)
	 */
	private TIntIntMap dayInfectionsLocPersonPerson = new TIntIntHashMap();

	/**
	 * Cumulative number of infections per location type from person type to person
	 * type. The key of the map is (((infectingLocationTypeId + 128)<< 20) +
	 * (infectingPersonTypeId << 10) + infectedPersonTypeId)
	 */
	private TIntIntMap totInfectionsLocPersonPerson = new TIntIntHashMap();

	/** the model. */
	private final MedlabsModelInterface model;

	/**
	 * Create a monitor for persons that can report events about persons.
	 * 
	 * @param model MedlabsModelInterface; the model
	 */
	public PersonMonitor(final MedlabsModelInterface model) {
		this.model = model;

		for (int ageBracket = 0; ageBracket < 11; ageBracket++) {
			String bracket = (10 * ageBracket) + "-" + (10 * (ageBracket + 1) - 1);
			this.INFECT_AGE_PER_DAY_EVENT[ageBracket] = new EventType("INFECT_AGE_PER_DAY_EVENT_" + bracket,
					new MetaData("infect_nr/d/age", "number of persons infected per age bracket per day",
							new ObjectDescriptor("infect_nr/d/age",
									"number of persons infected per age bracket per day", Integer.class)));
		}

		for (LocationType lt : this.model.getLocationTypeIndexMap().values()) {
			this.INFECT_LOCATIONTYPE_PER_HOUR_EVENT.put(lt,
					new EventType("INFECT_LOCATIONTYPE_PER_HOUR_EVENT_" + lt.getName(),
							new MetaData("infect_nr/h/loctype", "number of persons infected per location type per hour",
									new ObjectDescriptor("infect_nr/h/loctype",
											"number of persons infected per location type per hour", Integer.class))));
			this.infectionsPerLocationTypePerHour.put(lt, 0);
			this.INFECT_LOCATIONTYPE_PER_DAY_EVENT.put(lt,
					new EventType("INFECT_LOCATIONTYPE_PER_DAY_EVENT_" + lt.getName(),
							new MetaData("infect_nr/d/loctype", "number of persons infected per location type per hour",
									new ObjectDescriptor("infect_nr/d/loctype",
											"number of persons infected per location type per hour", Integer.class))));
			this.infectionsPerLocationTypePerDay.put(lt, 0);
		}

		try {
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectAgePerHour", null);
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectAgePerDay", null);
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireDeathsAgePerDay", null);
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectLocationTypePerHour", null);
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectLocationTypePerDay", null);
			this.model.getSimulator().scheduleEventRel(23.9999, this, this, "midnightUpdates", null);
		} catch (Exception e) {
			CategoryLogger.always().info("PersonMonitor.fireInfectAgePerHour got error: " + e.getMessage());
		}
	}

	/**
	 * Report the age of the person being infected.
	 * 
	 * @param person         the person
	 * @param infectLocation the location where the infection took place
	 */
	private void reportInfectPerson(final Person person, final Location infectLocation) {
		try {
			int ageBracket = (int) Math.floor(person.getAge() / 10.0);
			this.infectionsPerAgeBracketPerDay[ageBracket]++;
			this.infectionsPerAgeBracketPerHour[ageBracket]++;
			fireTimedEvent(new TimedEvent<Double>(INFECTED_PERSON_EVENT, new Object[] { person, infectLocation },
					this.model.getSimulator().getSimulatorTime()));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Report the location of the person being infected.
	 * 
	 * @param locationTypeIndex the location type
	 */
	private void reportInfectionAtLocationType(final int locationTypeIndex) {
		LocationType lt = this.model.getLocationTypeIndexMap().get((byte) locationTypeIndex);
		this.infectionsPerLocationTypePerDay.put(lt, this.infectionsPerLocationTypePerDay.get(lt) + 1);
		this.infectionsPerLocationTypePerHour.put(lt, this.infectionsPerLocationTypePerHour.get(lt) + 1);
	}

	/**
	 * Report exposure of a person to disease, where the exposed person does get
	 * infected.
	 * 
	 * @param exposedPerson   Person; the exposed person
	 * @param infectLocation  Location; the location where the exposure took place
	 * @param infectingPerson Person; the most likely infecting person
	 */
	public void reportExposure(final Person exposedPerson, final Location infectLocation,
			final Person infectingPerson) {
		reportInfectPerson(exposedPerson, infectLocation);
		reportInfectionAtLocationType(infectLocation.getLocationTypeId());

		PersonType ptExposed = this.model.getPersonTypeClassMap().get(exposedPerson.getClass());
		PersonType ptInfecting = this.model.getPersonTypeClassMap().get(infectingPerson.getClass());
		int key = ptExposed.getId();
		this.dayInfectionsPersonType.put(key, 1 + this.dayInfectionsPersonType.get(key));
		this.totInfectionsPersonType.put(key, 1 + this.totInfectionsPersonType.get(key));
		// The key of the dayInfectionsPersonTypeToPersonType map is
		// ((infectingPersonTypeId << 16) + infectedPersonTypeId).
		key = (ptInfecting.getId() << 16) + ptExposed.getId();
		this.dayInfectionsPersonTypeToPersonType.put(key, 1 + this.dayInfectionsPersonTypeToPersonType.get(key));
		this.totInfectionsPersonTypeToPersonType.put(key, 1 + this.totInfectionsPersonTypeToPersonType.get(key));
		// totInfectionsLocPersonPerson key = (((infectingLocationTypeId + 128) << 20) +
		// (infectingPersonTypeId << 10) +
		// infectedPersonTypeId)
		key = (((int) infectLocation.getLocationTypeId() + 128) << 20) + (ptInfecting.getId() << 10)
				+ ptExposed.getId();
		this.dayInfectionsLocPersonPerson.put(key, 1 + this.dayInfectionsLocPersonPerson.get(key));
		this.totInfectionsLocPersonPerson.put(key, 1 + this.totInfectionsLocPersonPerson.get(key));
	}

	/**
	 * Extra method to report infection with a (constant or varying) 'rate', in
	 * addition to the reportExposure method.
	 * 
	 * @param exposedPerson  Person; the exposed person
	 * @param locationTypeId int; the location type where the exposure took place
	 * @param duration       double; the duration in that location in hours
	 * @param infectionRate  double; the provided rate for that location type
	 */
	public void reportExposureByRate(final Person exposedPerson, final int locationTypeId, final double duration,
			final double infectionRate) {
		try {
			double now = this.model.getSimulator().getSimulatorTime();
			fireTimedEvent(new TimedEvent<Double>(INFECTION_BY_RATE,
					new Object[] { exposedPerson, locationTypeId, duration, infectionRate }, now));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extra method to report infection with a (constant or varying) 'rate', in
	 * addition to the reportExposure method.
	 * 
	 * @param exposedPerson       Person; the exposed person
	 * @param locationTypeId      int; the location type where the exposure took
	 *                            place
	 * @param duration            double; the duration in that location in hours
	 * @param infectionRateFactor double; the provided rate factor (correction
	 *                            factor) for that location type
	 * @param ref                 PersonType; the reference group
	 * @param nrInfectedRef       int; the nr of persons that were infected
	 *                            yesterday in that reference group
	 * @param nrTotalRef          int; the total number of persons in the reference
	 *                            group
	 */
	public void reportExposureByRateFactor(final Person exposedPerson, final int locationTypeId, final double duration,
			final double infectionRateFactor, final PersonType ref, final int nrInfectedRef, final int nrTotalRef) {
		double now = this.model.getSimulator().getSimulatorTime();
		try {
			fireTimedEvent(new TimedEvent<Double>(INFECTION_BY_RATE_FACTOR, new Object[] { exposedPerson,
					locationTypeId, duration, infectionRateFactor, ref, nrInfectedRef, nrTotalRef }, now));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Make the number of infections per person type available for probability-based
	 * infections.
	 */
	protected void midnightUpdates() {
		try {
			// fire events for the infection matrices
			double now = this.model.getSimulator().getSimulatorTime();
			int ptSize = this.model.getPersonTypeList().size();
			int[] dayNrs = new int[ptSize];
			int[] totNrs = new int[ptSize];
			for (int i = 0; i < ptSize; i++) {
				int ptId = this.model.getPersonTypeList().get(i).getId();
				dayNrs[i] = this.dayInfectionsPersonType.get(ptId);
				totNrs[i] = this.totInfectionsPersonType.get(ptId);
			}
			fireTimedEvent(new TimedEvent<Double>(DAY_INFECTIONS_PERSON_TYPE, dayNrs, now));
			fireTimedEvent(new TimedEvent<Double>(TOT_INFECTIONS_PERSON_TYPE, totNrs, now));

			for (int infectingIndex = 0; infectingIndex < ptSize; infectingIndex++) {
				int ptInfectingId = this.model.getPersonTypeList().get(infectingIndex).getId();
				dayNrs = new int[ptSize + 1];
				totNrs = new int[ptSize + 1];
				dayNrs[0] = infectingIndex;
				totNrs[0] = infectingIndex;
				for (int exposedIndex = 0; exposedIndex < ptSize; exposedIndex++) {
					int ptExposedId = this.model.getPersonTypeList().get(exposedIndex).getId();
					int key = (ptInfectingId << 16) + ptExposedId;
					dayNrs[exposedIndex + 1] = this.dayInfectionsPersonTypeToPersonType.get(key);
					totNrs[exposedIndex + 1] = this.totInfectionsPersonTypeToPersonType.get(key);
				}
				fireTimedEvent(new TimedEvent<Double>(DAY_INFECTIONS_PERSON_TO_PERSON_TYPE, dayNrs, now));
				fireTimedEvent(new TimedEvent<Double>(TOT_INFECTIONS_PERSON_TO_PERSON_TYPE, totNrs, now));
			}

			for (int lt = 0; lt < this.model.getLocationTypeList().size(); lt++) {
				int ltId = this.model.getLocationTypeList().get(lt).getLocationTypeId();
				for (int infectingIndex = 0; infectingIndex < ptSize; infectingIndex++) {
					int ptInfectingId = this.model.getPersonTypeList().get(infectingIndex).getId();
					dayNrs = new int[ptSize + 2];
					totNrs = new int[ptSize + 2];
					dayNrs[0] = lt;
					dayNrs[1] = infectingIndex;
					totNrs[0] = lt;
					totNrs[1] = infectingIndex;
					for (int exposedIndex = 0; exposedIndex < ptSize; exposedIndex++) {
						int ptExposedId = this.model.getPersonTypeList().get(exposedIndex).getId();
						int key = (((int) ltId + 128) << 20) + (ptInfectingId << 10) + ptExposedId;
						dayNrs[exposedIndex + 2] = this.dayInfectionsLocPersonPerson.get(key);
						totNrs[exposedIndex + 2] = this.totInfectionsLocPersonPerson.get(key);
					}
					fireTimedEvent(new TimedEvent<Double>(DAY_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE, dayNrs, now));
					fireTimedEvent(new TimedEvent<Double>(TOT_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE, totNrs, now));
				}
			}

			// fill yesterday data for probability-based infections and clear day data
			this.yesterdayInfectionsPersonType.clear();
			this.yesterdayInfectionsPersonType.putAll(this.dayInfectionsPersonType);
			this.model.getSimulator().scheduleEventRel(24.0, this, this, "midnightUpdates", null);
			this.dayInfectionsPersonType.clear();
			this.dayInfectionsPersonTypeToPersonType.clear();
			this.dayInfectionsLocPersonPerson.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fire the numbers of infections during the last hour and reset counters.
	 */
	protected void fireInfectAgePerHour() {
		try {
			fireTimedEvent(new TimedEvent<Double>(INFECT_AGE_PER_HOUR_EVENT, this.infectionsPerAgeBracketPerHour,
					this.model.getSimulator().getSimulatorTime()));
			for (int ageBracket = 0; ageBracket < 11; ageBracket++) {
				this.infectionsPerAgeBracketPerHour[ageBracket] = 0;
			}
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectAgePerHour", null);
		} catch (Exception e) {
			CategoryLogger.always().info("PersonMonitor.fireInfectAgePerHour got error: " + e.getMessage());
		}
	}

	/**
	 * Fire the numbers of infections during the last day and reset counters.
	 */
	protected void fireInfectAgePerDay() {
		try {
			for (int ageBracket = 0; ageBracket < 11; ageBracket++) {
				fireTimedEvent(new TimedEvent<Double>(this.INFECT_AGE_PER_DAY_EVENT[ageBracket],
						this.infectionsPerAgeBracketPerDay[ageBracket], this.model.getSimulator().getSimulatorTime()));
				this.infectionsPerAgeBracketPerDay[ageBracket] = 0;
			}
			this.model.getSimulator().scheduleEventRel(24.0, this, this, "fireInfectAgePerDay", null);
		} catch (Exception e) {
			CategoryLogger.always().info("PersonMonitor.fireInfectAgePerDay got error: " + e.getMessage());
		}
	}

	/**
	 * Report the age of the person who died.
	 * 
	 * @param person the person
	 */
	public void reportDeathPerson(final Person person) {
		try {
			int ageBracket = (int) Math.floor(person.getAge() / 10.0);
			this.deathsPerAgeBracketPerDay[ageBracket]++;
			fireTimedEvent(
					new TimedEvent<Double>(DEATH_EVENT, person.getAge(), this.model.getSimulator().getSimulatorTime()));
			fireTimedEvent(
					new TimedEvent<Double>(DEAD_PERSON_EVENT, person, this.model.getSimulator().getSimulatorTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fire the numbers of deaths in the last day and reset counters.
	 */
	protected void fireDeathsAgePerDay() {
		try {
			fireTimedEvent(new TimedEvent<Double>(DEATHS_AGE_PER_DAY_EVENT, this.deathsPerAgeBracketPerDay,
					this.model.getSimulator().getSimulatorTime()));
			for (int ageBracket = 0; ageBracket < 11; ageBracket++) {
				this.deathsPerAgeBracketPerDay[ageBracket] = 0;
			}
			this.model.getSimulator().scheduleEventRel(24.0, this, this, "fireDeathsAgePerDay", null);
		} catch (Exception e) {
			CategoryLogger.always().info("PersonMonitor.fireDeathsAgePerDay got error: " + e.getMessage());
		}
	}

	/**
	 * Fire the numbers of the last hour and reset counters.
	 */
	protected void fireInfectLocationTypePerHour() {
		try {
			fireTimedEvent(new TimedEvent<Double>(INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT,
					new HashMap<>(this.infectionsPerLocationTypePerHour),
					this.model.getSimulator().getSimulatorTime()));
			for (Map.Entry<LocationType, Integer> entry : this.infectionsPerLocationTypePerHour.entrySet()) {
				fireTimedEvent(new TimedEvent<Double>(this.INFECT_LOCATIONTYPE_PER_HOUR_EVENT.get(entry.getKey()),
						entry.getValue(), this.model.getSimulator().getSimulatorTime()));
				this.infectionsPerLocationTypePerHour.put(entry.getKey(), 0);
			}
			this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectLocationTypePerHour", null);
		} catch (Exception e) {
			CategoryLogger.always().info("PersonMonitor.fireInfectLocationTypePerHour got error: " + e.getMessage());
		}
	}

	/**
	 * Fire the numbers of the last day and reset counters.
	 */
	protected void fireInfectLocationTypePerDay() {
		try {
			for (Map.Entry<LocationType, Integer> entry : this.infectionsPerLocationTypePerDay.entrySet()) {
				fireTimedEvent(new TimedEvent<Double>(this.INFECT_LOCATIONTYPE_PER_DAY_EVENT.get(entry.getKey()),
						entry.getValue(), this.model.getSimulator().getSimulatorTime()));
				this.infectionsPerLocationTypePerDay.put(entry.getKey(), 0);
			}
			this.model.getSimulator().scheduleEventRel(24.0, this, this, "fireInfectLocationTypePerDay", null);
		} catch (Exception e) {
			CategoryLogger.always().info("PersonMonitor.fireInfectLocationTypePerDay got error: " + e.getMessage());
		}
	}

	/**
	 * Report age of person for histogram.
	 * 
	 * @param age the age
	 */
	public void reportAge(final int age) {
		try {
			this.fireTimedEvent(new TimedEvent<Double>(AGE_EVENT, age, this.model.getSimulator().getSimulatorTime()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Report family size for histogram.
	 * 
	 * @param size family size
	 */
	public void reportFamilySize(final int size) {
		try {
			this.fireTimedEvent(
					new TimedEvent<Double>(FAMILY_EVENT, size, this.model.getSimulator().getSimulatorTime()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return yesterdayInfectionsPersonType
	 */
	public TIntIntMap getYesterdayInfectionsPersonType() {
		return this.yesterdayInfectionsPersonType;
	}

	/** {@inheritDoc} */
	public Serializable getSourceId() {
		return "PersonMonitor";
	}

	EventListenerMap eventListenerMap = new EventListenerMap();

	@Override
	public EventListenerMap getEventListenerMap() throws RemoteException {
		return eventListenerMap;
	}
}
