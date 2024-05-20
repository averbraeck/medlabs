package nl.tudelft.simulation.medlabs.disease;

import java.io.Serializable;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongFloatHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import nl.tudelft.simulation.medlabs.AbstractModelNamed;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.SimpleDevsSimulatorInterface;

/**
 * Calculation that each location carries out when people leave or enter the location. When there are infectious persons in the
 * location, when he/she has a chance of getting infected, to determine whether infection actually takes place.<br>
 * <br>
 * The DiseaseTransmission model used two caches to speed up the infection calculations. The first cache contains people present
 * in sublocations for the sublocations with one or more infectious persons. The key of the internal Map is a Long where the
 * first 32 bits are used for the location id, and the last 32 bits for the sublocation number. The value is an ArrayList of
 * Person ids. The Map takes a considerable amount of memory but is can sped up calculations with a factor 500 or more... <br>
 * <br>
 * The second cache contains the last calculation time for a location where one or more infectious persons are present. Note
 * that locations where no infectious persons are located do not need to store such a time.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class DiseaseTransmission extends AbstractModelNamed implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** Cache for the sublocations where infectious persons are present to speed up the infection calculations. */
    private TLongObjectMap<TIntSet> infectiousPersonsInSublocationCache = new TLongObjectHashMap<>();

    /** Cache for the number of people per sublocation. */
    private TLongIntMap nrPersonsInSublocationMap = new TLongIntHashMap();

    /**
     * Cache with the last time when disease transmission has been calculated for a sublocation. Stored here instead of an array
     * at each location, which is not needed for those sublocations where no infected people are present.
     */
    private TLongFloatMap lastCalculationCache = new TLongFloatHashMap();

    /** Number of caused infections per infectious person in their current location (offspring calculation). */
    private TIntIntMap infectionsPerInfectiousPersonMap = new TIntIntHashMap(1);

    /** the simulator. */
    private final SimpleDevsSimulatorInterface simulator;

    /**
     * Create a new Transmission model.
     * @param model MedlabsModelInterface; the Medlabs model
     * @param name String; name of the disease
     */
    public DiseaseTransmission(final MedlabsModelInterface model, final String name)
    {
        super(model, name);
        this.simulator = model.getSimulator();
    }

    /**
     * This method is called when a person enters a location. When there are no infectious persons, the method returns fast. The
     * method can also return quickly when the delta-time is very short (e.g, less than a minute but be aware that spread in
     * public transport and shops might suffer since many people arrive and depart with short intervals). Note that the method
     * can maintain the attribute infectiousPersonPresent in Location to speed up the calculations.
     * @param location Location; the location for which to calculate infectious spread
     * @param subLocationIndex short; the index of the sublocation
     * @param person Person; the person entering
     */
    public void calculateTransmissionEnter(final Location location, final short subLocationIndex, final Person person)
    {
        long key = makeCacheKey(location, subLocationIndex);
        if (location.getLocationTypeId() >= 0)
        {
            this.nrPersonsInSublocationMap.put(key, this.nrPersonsInSublocationMap.get(key) + 1);
        }
        if (!location.getLocationType().isInfectInSublocation() || location.getLocationTypeId() < 0)
            return;

        if (!isSublocationInfected(key))
        {
            // check if the newly entered person is infectious
            if (person.getDiseasePhase().isIll())
            {
                TIntSet persons = this.infectiousPersonsInSublocationCache.get(key);
                if (persons == null)
                {
                    persons = new TIntHashSet();
                    this.infectiousPersonsInSublocationCache.put(key, persons);
                    this.lastCalculationCache.put(key, this.simulator.getSimulatorTime().floatValue());
                    // add the persons who are already there
                    TIntObjectMap<Person> personMap = getModel().getPersonMap();
                    for (TIntIterator it = location.getAllPersonIds().iterator(); it.hasNext();)
                    {
                        Person p = personMap.get(it.next());
                        if (p.getCurrentSubLocationIndex() == subLocationIndex)
                            persons.add(p.getId());
                    }
                }
                persons.add(person.getId());
                updateLastCalculationTime(key);
            }
            return;
        }

        // cache entry already existed
        InfectionRecord infectionRecord = infectPeople(location, getPersons(key),
                this.simulator.getSimulatorTime().doubleValue() - getLastCalculationTime(key));
        if (infectionRecord.isCalculated())
        {
            expose(infectionRecord);
            updateLastCalculationTime(key);
        }
        this.infectiousPersonsInSublocationCache.get(key).add(person.getId());
    }

    /**
     * This method is called when a person leaves a location. When there are no infectious persons, the method returns fast. The
     * method can also return quickly when the delta-time is very short (e.g, less than a minute but be aware that spread in
     * public transport and shops might suffer since many people arrive and depart with short intervals). Note that the method
     * can maintain the attribute infectiousPersonPresent in Location to speed up the calculations.
     * @param location Location; the location for which to calculate infectious spread
     * @param subLocationIndex short; the index of the sublocation
     * @param person Person; the person leaving
     */
    public void calculateTransmissionLeave(final Location location, final short subLocationIndex, final Person person)
    {
        long key = makeCacheKey(location, subLocationIndex);
        if (location.getLocationTypeId() >= 0)
        {
            this.nrPersonsInSublocationMap.put(key, this.nrPersonsInSublocationMap.get(key) - 1);
        }
        if (!location.getLocationType().isInfectInSublocation() || location.getLocationTypeId() < 0)
            return;

        if (!isSublocationInfected(key))
        {
            return;
        }
        TIntSet persons = getPersons(key);

        InfectionRecord infectionRecord =
                infectPeople(location, persons, this.simulator.getSimulatorTime().doubleValue() - getLastCalculationTime(key));
        if (infectionRecord.isCalculated())
        {
            expose(infectionRecord);
            updateLastCalculationTime(key);
            // check if the person infected someone or is considered infectious (both may have changed over time)
            if (this.infectionsPerInfectiousPersonMap.containsKey(person.getId())
                    || infectionRecord.getInfectiousPersons().contains(person.getId()))
            {
                int nrInfected = this.infectionsPerInfectiousPersonMap.containsKey(person.getId())
                        ? this.infectionsPerInfectiousPersonMap.get(person.getId()) : 0;
                getModel().getDiseaseMonitor().reportOffspring(person, person.getCurrentLocation(), nrInfected);
                this.infectionsPerInfectiousPersonMap.remove(person.getId());
            }
        }
        persons.remove(person.getId());

        // Check whether there are still infectious persons in the location after this person left
        // Note that we cannot just base this on the person who leaves because a) more than one infectious person may be
        // present, and b) the status of a person's disease may have changed in the meantime.
        // But when the person who leaves is still susceptible, no recalculation needs to take place.

        if (persons.size() == 0)
        {
            this.infectiousPersonsInSublocationCache.remove(key);
            this.lastCalculationCache.remove(key);
            return;
        }

        if (!person.getDiseasePhase().isSusceptible())
        {
            TIntObjectMap<Person> personMap = getModel().getPersonMap();
            boolean infected = false;
            for (TIntIterator it = persons.iterator(); it.hasNext();)
            {
                Person p = personMap.get(it.next());
                if (p.getDiseasePhase().isIll())
                {
                    infected = true;
                    break;
                }
            }
            if (!infected)
            {
                this.infectiousPersonsInSublocationCache.remove(key);
                this.lastCalculationCache.remove(key);
            }
        }
    }

    /**
     * Calculate the disease spread for all persons present in this location during the 'duration' in hours. The method could
     * return quickly when the delta-time is very short (e.g, less than a minute but be aware that spread in public transport
     * and shops might suffer since many people arrive and depart with short intervals).
     * @param location Location; the location for which to calculate infectious spread
     * @param personsInSublocation TIntSet; the persons present in the sublocation (one or more are potentially contagious)
     * @param duration double; the time for which the calculation needs to take place, in hours
     * @return InfectionRecord; information about the infection(s) that took place
     */
    public abstract InfectionRecord infectPeople(Location location, TIntSet personsInSublocation, double duration);

    /**
     * Carry out the actual exposure as the result of a transmission, and trigger all associated statistics.
     * @param infectionRecord InfectionRecord; information about the infected and infectious persons in the location.
     */
    private void expose(final InfectionRecord infectionRecord)
    {
        TIntList infectedPersons = infectionRecord.getInfectedPersons();
        TIntList infectiousPersons = infectionRecord.getInfectiousPersons();
        for (int i = 0; i < infectedPersons.size(); i++)
        {
            Person exposedPerson = getModel().getPersonMap().get(infectedPersons.get(i));
            exposedPerson.setExposureTime(this.simulator.getSimulatorTime().floatValue());

            Person infectiousPerson = null;
            if (infectiousPersons.size() == 1)
            {
                infectiousPerson = this.model.getPersonMap().get(infectiousPersons.get(0));
            }
            else if (infectiousPersons.size() > 1)
            {
                infectiousPerson = this.model.getPersonMap()
                        .get(infectiousPersons.get(this.model.getRandomStream().nextInt(0, infectiousPersons.size() - 1)));
            }
            else
            {
                System.err.println("Exposure took place, but no infectious person in location!");
            }
            getModel().getDiseaseMonitor().reportInfection(exposedPerson, infectiousPerson);
            this.model.getDiseaseProgression().expose(exposedPerson, infectionRecord.getExposedPhase());
            this.model.getPersonMonitor().reportExposure(exposedPerson, exposedPerson.getCurrentLocation(), infectiousPerson);
            this.infectionsPerInfectiousPersonMap.putIfAbsent(infectiousPerson.getId(), 0);
            this.infectionsPerInfectiousPersonMap.put(infectiousPerson.getId(),
                    1 + this.infectionsPerInfectiousPersonMap.get(infectiousPerson.getId()));
        }
    }

    /**
     * Make the key index for the cache.
     * @param location Location; the location to use
     * @param subLocationIndex int; the sublocation number
     * @return long; the key for the cache
     */
    protected long makeCacheKey(final Location location, final short subLocationIndex)
    {
        return (((long) location.getId()) << 32) + subLocationIndex;
    }

    /**
     * Return whether the sublocation of this location contains one or more infected persons.
     * @param location Location; the location to check
     * @param subLocationIndex int; the sublocation number to check
     * @return boolean; whether the sublocation of this location contains one or more infected persons
     */
    public boolean isSublocationInfected(final Location location, final short subLocationIndex)
    {
        return this.infectiousPersonsInSublocationCache.containsKey(makeCacheKey(location, subLocationIndex));
    }

    /**
     * Return whether the key coding a sublocation and a location contains one or more infected persons.
     * @param key long; the key to check
     * @return boolean; whether the sublocation of this location contains one or more infected persons
     */
    protected boolean isSublocationInfected(final long key)
    {
        return this.infectiousPersonsInSublocationCache.containsKey(key);
    }

    /**
     * Set a parameter for the transmission model.
     * @param parameterName String; parameter name
     * @param value double; new value
     */
    public abstract void setParameter(String parameterName, double value);

    /**
     * Update the last calculation time for the location cache key.
     * @param key long; the key calculated as (((long) location.getId()) &lt;&lt; 32) + subLocationIndex
     */
    protected void updateLastCalculationTime(final long key)
    {
        this.lastCalculationCache.put(key, this.simulator.getSimulatorTime().floatValue());
    }

    /**
     * Return the persons for this location cache key.
     * @param key long; the key calculated as (((long) location.getId()) &lt;&lt; 32) + subLocationIndex
     * @return TIntSet; the persons for this location cache key
     */
    protected TIntSet getPersons(final long key)
    {
        return this.infectiousPersonsInSublocationCache.get(key);
    }

    /**
     * Return the last time infections were calculated after a person entered or left a (sub)location.
     * @param key long; the key calculated as (((long) location.getId()) &lt;&lt; 32) + subLocationIndex
     * @return float; the last time infections were calculated for this location cache key
     */
    protected float getLastCalculationTime(final long key)
    {
        return this.lastCalculationCache.get(key);
    }

    /**
     * Return the number of persons for this sublocation.
     * @param location Location; the location
     * @param subLocationIndex short; the index of the sublocation
     * @return int; the number of persons in this sublocation
     */
    public int getNrPersonsInSublocation(final Location location, final short subLocationIndex)
    {
        long key = makeCacheKey(location, subLocationIndex);
        return this.nrPersonsInSublocationMap.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "DiseaseTransmission [name=" + this.name + "]";
    }

}
