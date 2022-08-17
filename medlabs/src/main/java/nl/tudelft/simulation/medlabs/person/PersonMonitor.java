package nl.tudelft.simulation.medlabs.person;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.event.TimedEventType;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * PersonMonitor fires person related events for graphs and statistics.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
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
public class PersonMonitor extends EventProducer
{
    /** */
    private static final long serialVersionUID = 1L;

    /** statistics update event for infection. */
    @SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
    public TimedEventType[] INFECT_AGE_PER_DAY_EVENT = new TimedEventType[11];

    /** statistics update event for infection. */
    private int[] infectionsPerAgeBracketPerDay = new int[11];

    /** statistics update event for infection. */
    public static final TimedEventType INFECT_AGE_PER_HOUR_EVENT = new TimedEventType("INFECT_AGE_PER_HOUR_EVENT", new MetaData(
            "infect_nr/h/age", "number of persons infected per age bracket per hour",
            new ObjectDescriptor("infect_nr/h/age", "number of persons infected per age bracket per hour", int[].class)));

    /** statistics update event for infection. */
    private int[] infectionsPerAgeBracketPerHour = new int[11];

    /** statistics update event for infection. */
    @SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
    public Map<LocationType, TimedEventType> INFECT_LOCATIONTYPE_PER_DAY_EVENT = new HashMap<>();

    /** statistics update event for infection. */
    private Map<LocationType, Integer> infectionsPerLocationTypePerDay = new HashMap<>();

    /** statistics update event for infection. */
    @SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:membername" })
    public Map<LocationType, TimedEventType> INFECT_LOCATIONTYPE_PER_HOUR_EVENT = new HashMap<>();

    /** statistics update event for infection. */
    public static final TimedEventType INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT =
            new TimedEventType("INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT",
                    new MetaData("infect_nr/h/loctype", "number of persons infected per location type per hour",
                            new ObjectDescriptor("infect_nr/h/loctype", "number of persons infected per location type per hour",
                                    Map.class)));

    /** statistics update event for infection. */
    private Map<LocationType, Integer> infectionsPerLocationTypePerHour = new HashMap<>();

    /** statistics update event for death. */
    public static final TimedEventType DEATHS_AGE_PER_DAY_EVENT = new TimedEventType("DEATHS_AGE_PER_DAY_EVENT",
            new MetaData("deaths/d/age", "number of deaths per age bracket per day",
                    new ObjectDescriptor("deaths/d/age", "number of deaths per age bracket per day", int[].class)));;

    /** statistics update event for death. */
    private int[] deathsPerAgeBracketPerDay = new int[11];

    /** statistics update event for death. */
    public static final TimedEventType DEATH_EVENT = new TimedEventType("DEATH_EVENT", new MetaData("death in age bracket",
            "death in age bracket", new ObjectDescriptor("death in age bracket", "death in age bracket", Double.class)));

    /** statistics update event for age distribution (at model start). */
    public static final TimedEventType AGE_EVENT =
            new TimedEventType("AGE_EVENT", new MetaData("age", "age", new ObjectDescriptor("age", "age", Double.class)));

    /** statistics update event for family composition (at model start). */
    public static final TimedEventType FAMILY_EVENT = new TimedEventType("FAMILY_EVENT",
            new MetaData("family size", "family size", new ObjectDescriptor("family size", "family size", Double.class)));

    /** event for infected person. */
    public static final TimedEventType INFECTED_PERSON_EVENT =
            new TimedEventType("INFECTED_PERSON_EVENT", new MetaData("infected person", "infected person",
                    new ObjectDescriptor("infected person", "infected person", Person.class)));

    /** event for person who died. */
    public static final TimedEventType DEAD_PERSON_EVENT = new TimedEventType("DEAD_PERSON_EVENT",
            new MetaData("dead person", "dead person", new ObjectDescriptor("dead person", "dead person", Person.class)));

    /** the model. */
    private final MedlabsModelInterface model;

    /**
     * Create a monitor for persons that can report events about persons.
     * @param model MedlabsModelInterface; the model
     */
    public PersonMonitor(final MedlabsModelInterface model)
    {
        this.model = model;

        for (int ageBracket = 0; ageBracket < 11; ageBracket++)
        {
            String bracket = (10 * ageBracket) + "-" + (10 * (ageBracket + 1) - 1);
            this.INFECT_AGE_PER_DAY_EVENT[ageBracket] =
                    new TimedEventType("INFECT_AGE_PER_DAY_EVENT_" + bracket,
                            new MetaData("infect_nr/d/age", "number of persons infected per age bracket per day",
                                    new ObjectDescriptor("infect_nr/d/age",
                                            "number of persons infected per age bracket per day", Integer.class)));
        }

        for (LocationType lt : this.model.getLocationTypeIndexMap().values())
        {
            this.INFECT_LOCATIONTYPE_PER_HOUR_EVENT.put(lt,
                    new TimedEventType("INFECT_LOCATIONTYPE_PER_HOUR_EVENT_" + lt.getName(),
                            new MetaData("infect_nr/h/loctype", "number of persons infected per location type per hour",
                                    new ObjectDescriptor("infect_nr/h/loctype",
                                            "number of persons infected per location type per hour", Integer.class))));
            this.infectionsPerLocationTypePerHour.put(lt, 0);
            this.INFECT_LOCATIONTYPE_PER_DAY_EVENT.put(lt,
                    new TimedEventType("INFECT_LOCATIONTYPE_PER_DAY_EVENT_" + lt.getName(),
                            new MetaData("infect_nr/d/loctype", "number of persons infected per location type per hour",
                                    new ObjectDescriptor("infect_nr/d/loctype",
                                            "number of persons infected per location type per hour", Integer.class))));
            this.infectionsPerLocationTypePerDay.put(lt, 0);
        }

        try
        {
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectAgePerHour", null);
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectAgePerDay", null);
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireDeathsAgePerDay", null);
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectLocationTypePerHour", null);
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectLocationTypePerDay", null);
        }
        catch (Exception e)
        {
            CategoryLogger.always().info("PersonMonitor.fireInfectAgePerHour got error: " + e.getMessage());
        }
    }

    /**
     * Report the age of the person being infected.
     * @param person the person
     */
    public void reportInfectPerson(final Person person)
    {
        int ageBracket = (int) Math.floor(person.getAge() / 10.0);
        this.infectionsPerAgeBracketPerDay[ageBracket]++;
        this.infectionsPerAgeBracketPerHour[ageBracket]++;
        fireTimedEvent(
                new TimedEvent<Double>(INFECTED_PERSON_EVENT, this, person, this.model.getSimulator().getSimulatorTime()));
    }

    /**
     * Fire the numbers of infections during the last hour and reset counters.
     */
    protected void fireInfectAgePerHour()
    {
        try
        {
            fireTimedEvent(new TimedEvent<Double>(INFECT_AGE_PER_HOUR_EVENT, this, this.infectionsPerAgeBracketPerHour,
                    this.model.getSimulator().getSimulatorTime()));
            for (int ageBracket = 0; ageBracket < 11; ageBracket++)
            {
                this.infectionsPerAgeBracketPerHour[ageBracket] = 0;
            }
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectAgePerHour", null);
        }
        catch (Exception e)
        {
            CategoryLogger.always().info("PersonMonitor.fireInfectAgePerHour got error: " + e.getMessage());
        }
    }

    /**
     * Fire the numbers of infections during the last day and reset counters.
     */
    protected void fireInfectAgePerDay()
    {
        try
        {
            for (int ageBracket = 0; ageBracket < 11; ageBracket++)
            {
                fireTimedEvent(new TimedEvent<Double>(this.INFECT_AGE_PER_DAY_EVENT[ageBracket], this,
                        this.infectionsPerAgeBracketPerDay[ageBracket], this.model.getSimulator().getSimulatorTime()));
                this.infectionsPerAgeBracketPerDay[ageBracket] = 0;
            }
            this.model.getSimulator().scheduleEventRel(24.0, this, this, "fireInfectAgePerDay", null);
        }
        catch (Exception e)
        {
            CategoryLogger.always().info("PersonMonitor.fireInfectAgePerDay got error: " + e.getMessage());
        }
    }

    /**
     * Report the age of the person who died.
     * @param person the person
     */
    public void reportDeathPerson(final Person person)
    {
        int ageBracket = (int) Math.floor(person.getAge() / 10.0);
        this.deathsPerAgeBracketPerDay[ageBracket]++;
        fireTimedEvent(new TimedEvent<Double>(DEATH_EVENT, this, (double) person.getAge(),
                this.model.getSimulator().getSimulatorTime()));
        fireTimedEvent(new TimedEvent<Double>(DEAD_PERSON_EVENT, this, person, this.model.getSimulator().getSimulatorTime()));
    }

    /**
     * Fire the numbers of deaths in the last day and reset counters.
     */
    protected void fireDeathsAgePerDay()
    {
        try
        {
            fireTimedEvent(new TimedEvent<Double>(DEATHS_AGE_PER_DAY_EVENT, this, this.deathsPerAgeBracketPerDay,
                    this.model.getSimulator().getSimulatorTime()));
            for (int ageBracket = 0; ageBracket < 11; ageBracket++)
            {
                this.deathsPerAgeBracketPerDay[ageBracket] = 0;
            }
            this.model.getSimulator().scheduleEventRel(24.0, this, this, "fireDeathsAgePerDay", null);
        }
        catch (Exception e)
        {
            CategoryLogger.always().info("PersonMonitor.fireDeathsAgePerDay got error: " + e.getMessage());
        }
    }

    /**
     * Report the location of the person being infected.
     * @param locationTypeIndex the location type
     */
    public void reportInfectionAtLocationType(final int locationTypeIndex)
    {
        LocationType lt = this.model.getLocationTypeIndexMap().get((byte) locationTypeIndex);
        this.infectionsPerLocationTypePerDay.put(lt, this.infectionsPerLocationTypePerDay.get(lt) + 1);
        this.infectionsPerLocationTypePerHour.put(lt, this.infectionsPerLocationTypePerHour.get(lt) + 1);
    }

    /**
     * Fire the numbers of the last hour and reset counters.
     */
    protected void fireInfectLocationTypePerHour()
    {
        try
        {
            fireTimedEvent(new TimedEvent<Double>(INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT, this,
                    new HashMap<>(this.infectionsPerLocationTypePerHour), this.model.getSimulator().getSimulatorTime()));
            for (Map.Entry<LocationType, Integer> entry : this.infectionsPerLocationTypePerHour.entrySet())
            {
                fireTimedEvent(new TimedEvent<Double>(INFECT_LOCATIONTYPE_PER_HOUR_EVENT.get(entry.getKey()), this,
                        entry.getValue(), this.model.getSimulator().getSimulatorTime()));
                this.infectionsPerLocationTypePerHour.put(entry.getKey(), 0);
            }
            this.model.getSimulator().scheduleEventRel(1.0, this, this, "fireInfectLocationTypePerHour", null);
        }
        catch (Exception e)
        {
            CategoryLogger.always().info("PersonMonitor.fireInfectLocationTypePerHour got error: " + e.getMessage());
        }
    }

    /**
     * Fire the numbers of the last day and reset counters.
     */
    protected void fireInfectLocationTypePerDay()
    {
        try
        {
            for (Map.Entry<LocationType, Integer> entry : this.infectionsPerLocationTypePerDay.entrySet())
            {
                fireTimedEvent(new TimedEvent<Double>(INFECT_LOCATIONTYPE_PER_DAY_EVENT.get(entry.getKey()), this,
                        entry.getValue(), this.model.getSimulator().getSimulatorTime()));
                this.infectionsPerLocationTypePerDay.put(entry.getKey(), 0);
            }
            this.model.getSimulator().scheduleEventRel(24.0, this, this, "fireInfectLocationTypePerDay", null);
        }
        catch (Exception e)
        {
            CategoryLogger.always().info("PersonMonitor.fireInfectLocationTypePerDay got error: " + e.getMessage());
        }
    }

    /**
     * Report age of person for histogram.
     * @param age the age
     */
    public void reportAge(final int age)
    {
        this.fireTimedEvent(
                new TimedEvent<Double>(AGE_EVENT, this, (double) age, this.model.getSimulator().getSimulatorTime()));
    }

    /**
     * Report family size for histogram.
     * @param size family size
     */
    public void reportFamilySize(final int size)
    {
        this.fireTimedEvent(
                new TimedEvent<Double>(FAMILY_EVENT, this, (double) size, this.model.getSimulator().getSimulatorTime()));
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "PersonMonitor";
    }
}
