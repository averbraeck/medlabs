package nl.tudelft.simulation.medlabs.location;

import java.util.HashMap;
import java.util.Map;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import nl.tudelft.simulation.medlabs.location.animation.LocationAnimation;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * LocationType is a set of types of locations that can appear in the simulation.
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
public class LocationType extends LocalEventProducer
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the name of this Location Type. */
    private final String name;

    /** the class of the location belonging to this type. */
    private final Class<? extends Location> locationClass;

    /** the class of the animation of the location type. can be null if no animation. */
    private final Class<? extends LocationAnimation> animationClass;

    /** the Locations, based on their original id. */
    private TIntObjectMap<Location> locationMap = new TIntObjectHashMap<>();

    /** the locations of this type per grid cell. Key: grid-xy-key. Value: id's of the locations. */
    private Map<Integer, TIntList> gridLocationMap = new HashMap<>();

    /** cache of the nearest location(s) for a grid cell. */
    private Map<Integer, TIntList> nearestLocationCache = new HashMap<>();

    /** cache of the location(s) with a certain maximum distance to a grid cell. */
    private Map<Integer, Map<Double, TIntList>> maxDistanceLocationCache = new HashMap<>();

    /** the location type id (byte). */
    private final byte locationTypeid;

    /** whether the visit to this location by a person should result in the same sublocation or not. */
    private final boolean reproducible;

    /** whether the sublocation can cause infections. If not, the total location causes infections. */
    private final boolean infectInSublocation;

    /**
     * Factor for the rate of contagiousness in this location type, default 1.0. This number can be reduced by e.g. social
     * distancing and ventilation. In that case the factor is less than 1. The factor should be in the interval (0, 1].
     */
    private double correctionFactorArea = 1.0;

    /** the fraction of locations in this location type that stays open. */
    private double fractionOpen;

    /** the fraction of activities that will still take place (in open locations). */
    private double fractionActivities;

    /** the alternative location to spend time. */
    private LocationType alternativeLocationType;

    /** the name under which the replacement activities need to be reported. */
    private String reportAsLocationName;

    /** the model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected MedlabsModelInterface model;

    /** total capacity. */
    private int totalCapacity = 0;

    /** number of persons in location type for fast statistics. */
    private int numberPersons = 0;

    /** number of persons in location type for fast statistics. */
    private int numberReserved = 0;

    /** capacity constrained? If false, only warnings will be given when the location is too full. */
    private boolean capConstrained = false;

    /** capacity as maximum number of persons per square meter. */
    private double capPersonsPerM2 = 0.25;
    
    /** the factor to apply for the size to make locations 'grow' or 'shrink'. */
    private final double sizeFactor;

    /** capacity overflow problems -- these are reported once every interval. */
    private TObjectIntMap<Location> capacityOverflowMap = new TObjectIntHashMap<>();

    /** capacity constraint problems -- these are failed attempts to use the location. */
    private int failedAllocationAttempts = 0;

    /** statistics update event. */
    public static final EventType STATISTICS_EVENT =
            new EventType("STATISTICS_EVENT", new MetaData("numberPersons", "number of persons in this location type",
                    new ObjectDescriptor("numberPersons", "number of persons in this location type", Integer.class)));

    /** activity duration event. */
    public static final EventType DURATION_EVENT =
            new EventType("DURATION_EVENT", new MetaData("duration", "duration in this location type",
                    new ObjectDescriptor("duration", "duration in this location type", Double.class)));

    /** capacity violation event. */
    public static final EventType CAPACITY_VIOLATION_EVENT = new EventType("CAPACITY_VIOLATION_EVENT");

    /** capacity allocation problem event. */
    public static final EventType CAPACITY_ALLOCATION_EVENT = new EventType("CAPACITY_ALLOCATION_EVENT");

    /**
     * Create a new location type.
     * @param model MedlabsModelInterface; the model
     * @param locationTypeId byte; the id of the location type
     * @param name String; the name of the location type
     * @param locationClass Class&lt;? extends Location&gt;; the class of the location belonging to this type
     * @param animationClass Class&lt;? extends LocationAnimation&gt;; the class of the location animation belonging to this
     *            type
     * @param reproducible boolean; whether the visit to this location by a person should result in the same sublocation or not
     * @param infectInSublocation boolean; whether the sublocation can cause infections. If not, the total location will be
     *            used. If no infection needs to take place, set correctionFactorArea to 0.
     * @param correctionFactorArea double; factor for the rate of contagiousness in this location type, default 1.0.
     * @param capConstrained boolean; capacity constrained? If false, only warnings will be given when the location is too full
     * @param capPersonsPerM2 double; capacity as maximum number of persons per square meter
     * @param sizeFactor double; the factor to apply for the size to make locations 'grow' or 'shrink'
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public LocationType(final MedlabsModelInterface model, final byte locationTypeId, final String name,
            final Class<? extends Location> locationClass, final Class<? extends LocationAnimation> animationClass,
            final boolean reproducible, final boolean infectInSublocation, final double correctionFactorArea,
            final boolean capConstrained, final double capPersonsPerM2, final double sizeFactor)
    {
        this.model = model;
        this.locationTypeid = locationTypeId;
        this.name = name;
        this.animationClass = animationClass;
        this.locationClass = locationClass;
        this.reproducible = reproducible;
        this.infectInSublocation = infectInSublocation;
        this.correctionFactorArea = correctionFactorArea;
        this.capConstrained = capConstrained;
        this.capPersonsPerM2 = capPersonsPerM2;
        model.getLocationTypeNameMap().put(name, this);
        model.getLocationTypeIndexMap().put(locationTypeId, this);
        model.getLocationTypeList().add(this);
        this.fractionOpen = 1.0;
        this.fractionActivities = 1.0;
        this.alternativeLocationType = this;
        this.reportAsLocationName = name;
        this.sizeFactor = sizeFactor;
    }

    /**
     * @return the nameofLocType
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the locationMap
     */
    public TIntObjectMap<Location> getLocationMap()
    {
        return this.locationMap;
    }

    /**
     * @return the location type id (byte)
     */
    public byte getLocationTypeId()
    {
        return this.locationTypeid;
    }

    /**
     * Add a location to this type, and store it in the grid.
     * @param location Location; the corresponding location
     */
    public void addLocation(final Location location)
    {
        this.locationMap.put(location.getId(), location);
        int key = getModel().gridKeyLatLon(location.getLatitude(), location.getLongitude());
        TIntList gridLocations = this.gridLocationMap.get(key);
        if (gridLocations == null)
        {
            gridLocations = new TIntArrayList();
            this.gridLocationMap.put(key, gridLocations);
        }
        gridLocations.add(location.getId());
        this.totalCapacity += location.getCapacity();
    }

    /**
     * Increment the number of persons in this location type by 1.
     */
    public void incNumberPersons()
    {
        this.numberPersons++;
    }

    /**
     * Decrement the number of persons in this location type by 1.
     */
    public void decNumberPersons()
    {
        this.numberPersons--;
    }

    /**
     * Increment the number of reservations for this location type by 1.
     */
    public void incNumberReserved()
    {
        this.numberReserved++;
    }

    /**
     * Decrement the number of reservations for this location type by 1.
     */
    public void decNumberReserved()
    {
        this.numberReserved--;
    }

    /**
     * @param startLocation the location where the person is currently, and to which a 'near' location needs to be found.
     * @param maxDistanceM max distance in meters
     * @return a list of location id's of this location type with a max distance to the startLocation
     */
    public TIntList getLocationListMaxDistanceM(final Location startLocation, final double maxDistanceM)
    {
        int startKey = startLocation.getGridKey();
        int startX = startLocation.getGridX();
        int startY = startLocation.getGridY();
        TIntList ret = null;
        if (this.maxDistanceLocationCache.containsKey(startKey)
                && this.maxDistanceLocationCache.get(startKey).containsKey(maxDistanceM))
        {
            ret = this.maxDistanceLocationCache.get(startKey).get(maxDistanceM);
        }
        else
        {
            ret = new TIntArrayList();
            int hCells = this.model.metersToGridCells(maxDistanceM);
            for (int x = startX - hCells; x <= startX + hCells; x++)
            {
                for (int y = startY - hCells; y <= startY + hCells; y++)
                {
                    int key = getModel().gridKeyXY(x, y);
                    if (this.gridLocationMap.containsKey(key))
                        ret.addAll(this.gridLocationMap.get(key));
                }
            }
            if (!this.maxDistanceLocationCache.containsKey(startKey))
                this.maxDistanceLocationCache.put(startKey, new HashMap<>());
            this.maxDistanceLocationCache.get(startKey).put(maxDistanceM, ret);
        }
        return ret;
    }

    /**
     * @param startLocation the location where the person is currently, and to which a 'near' location needs to be found.
     * @param maxDistanceM max distance in meters
     * @return an array of locations of this location type with a max distance to the startLocation
     */
    public Location[] getLocationArrayMaxDistanceM(final Location startLocation, final double maxDistanceM)
    {
        TIntList ret = getLocationListMaxDistanceM(startLocation, maxDistanceM);
        Location[] arr = new Location[ret.size()];
        int i = 0;
        for (TIntIterator it = ret.iterator(); it.hasNext();)
            arr[i++] = this.locationMap.get(it.next());
        return arr;
    }

    /**
     * @param startLocation the location where the person is currently, and to which a 'near' location needs to be found.
     * @param maxDistanceM max distance in meters
     * @return a list of location id's of this location type with a max distance to the startLocation
     */
    public TIntList getLocationListMaxDistanceMCap(final Location startLocation, final double maxDistanceM)
    {
        // first test the general nearest location, and filter on capacity
        TIntList nearestLocations = filterCap(getLocationListMaxDistanceM(startLocation, maxDistanceM));
        if (nearestLocations.size() > 0)
            return nearestLocations;

        // give the single nearest available location if nothing can be found in the neighbourhood for now.
        Location nLocCap = getNearestLocationCap(startLocation);
        if (nLocCap != null)
            nearestLocations.add(nLocCap.getId());
        return nearestLocations;
    }

    /**
     * @param startLocation the location where the person is currently, and to which a 'near' location needs to be found.
     * @param maxDistanceM max distance in meters
     * @return an array of locations of this location type with a max distance to the startLocation
     */
    public Location[] getLocationArrayMaxDistanceMCap(final Location startLocation, final double maxDistanceM)
    {
        TIntList ret = getLocationListMaxDistanceMCap(startLocation, maxDistanceM);
        Location[] arr = new Location[ret.size()];
        int i = 0;
        for (TIntIterator it = ret.iterator(); it.hasNext();)
            arr[i++] = this.locationMap.get(it.next());
        return arr;
    }

    /**
     * @param startLocation the location where the person is currently, and to which a 'near' location needs to be found.
     * @return the nearest location of this location type to the startLocation
     */
    public Location getNearestLocation(final Location startLocation)
    {
        int startKey = startLocation.getGridKey();
        int startX = startLocation.getGridX();
        int startY = startLocation.getGridY();
        TIntList ret = null;
        if (this.nearestLocationCache.containsKey(startKey))
        {
            ret = this.nearestLocationCache.get(startKey);
        }
        else
        {
            ret = new TIntArrayList();
            if (this.gridLocationMap.containsKey(startKey))
                ret.addAll(this.gridLocationMap.get(startKey));
            else
            {
                for (int hCells = 1; hCells < 100; hCells++)
                {
                    for (int x = startX - hCells; x <= startX + hCells; x++)
                    {
                        for (int y : new int[] {startY - hCells, startY + hCells})
                        {
                            int key = x * 32768 + y;
                            if (this.gridLocationMap.containsKey(key))
                                ret.addAll(this.gridLocationMap.get(key));
                        }
                    }
                    for (int y = startY - hCells + 1; y < startY + hCells; y++)
                    {
                        for (int x : new int[] {startX - hCells, startX + hCells})
                        {
                            int key = x * 32768 + y;
                            if (this.gridLocationMap.containsKey(key))
                                ret.addAll(this.gridLocationMap.get(key));
                        }
                    }
                    if (ret.size() > 0)
                        break;
                }
            }
            this.nearestLocationCache.put(startKey, ret);
        }

        // choose one (reproducible) value from the found locations
        if (ret.size() == 0)
        {
            System.err.println(this.model.getSimulator().getSimulatorTime() + ": NO NEAREST LOCATION FOUND FOR TYPE " + this);
            this.failedAllocationAttempts++;
            return null;
        }
        if (ret.size() == 1)
        {
            return this.locationMap.get(ret.get(0));
        }
        return this.locationMap.get(ret.get(this.model.getReproducibleJava2Random().nextInt(0, ret.size() - 1,
                hashCode() + 31 * startLocation.hashCode())));
    }

    /**
     * @param startLocation the location where the person is currently, and to which a 'near' location needs to be found.
     * @return the nearest location of this location type to the startLocation
     */
    public Location getNearestLocationCap(final Location startLocation)
    {
        // test if there is capacity at all
        if (this.capConstrained && this.numberPersons + this.numberReserved >= this.totalCapacity)
        {
            this.failedAllocationAttempts++;
            return null;
        }
            
        // first test the general nearest location
        Location nearestLocation = getNearestLocation(startLocation);
        if (nearestLocation.belowCapacity())
            return nearestLocation;

        // otherwise, find a near location that is not full
        int startKey = startLocation.getGridKey();
        int startX = startLocation.getGridX();
        int startY = startLocation.getGridY();
        TIntList ret = new TIntArrayList();
        if (this.nearestLocationCache.containsKey(startKey))
        {
            ret = filterCap(this.nearestLocationCache.get(startKey));
        }
        if (ret.size() == 0)
        {
            for (int hCells = 1; hCells < 100; hCells++)
            {
                for (int x = startX - hCells; x <= startX + hCells; x++)
                {
                    for (int y : new int[] {startY - hCells, startY + hCells})
                    {
                        int key = x * 32768 + y;
                        if (this.gridLocationMap.containsKey(key))
                            ret.addAll(filterCap(this.gridLocationMap.get(key)));
                    }
                }
                for (int y = startY - hCells + 1; y < startY + hCells; y++)
                {
                    for (int x : new int[] {startX - hCells, startX + hCells})
                    {
                        int key = x * 32768 + y;
                        if (this.gridLocationMap.containsKey(key))
                            ret.addAll(filterCap(this.gridLocationMap.get(key)));
                    }
                }
                if (ret.size() > 0)
                    break;
            }
        }

        // choose one (reproducible) value from the found locations
        if (ret.size() == 0)
        {
            // System.err.println(
            // this.model.getSimulator().getSimulatorTime() + ": ALL LOCATIONS FOR TYPE " + this + " are full! cap="
            // + this.totalCapacity + ", use=" + this.numberPersons + ", reserved=" + this.numberReserved);
            this.failedAllocationAttempts++;
            return null;
        }
        if (ret.size() == 1)
        {
            return this.locationMap.get(ret.get(0));
        }
        return this.locationMap.get(ret.get(this.model.getReproducibleJava2Random().nextInt(0, ret.size() - 1,
                hashCode() + 31 * startLocation.hashCode())));
    }

    private TIntList filterCap(final TIntList locationList)
    {
        TIntList outList = new TIntArrayList();
        for (TIntIterator it = locationList.iterator(); it.hasNext();)
        {
            Location loc = this.locationMap.get(it.next());
            if (loc.belowCapacity())
                outList.add(loc.getId());
        }
        return outList;
    }

    /**
     * Schedulable method to report statistics every 5 minutes.
     */
    public void reportStatistics()
    {
        this.fireTimedEvent(
                new TimedEvent<Double>(STATISTICS_EVENT, this.numberPersons, this.model.getSimulator().getSimulatorTime()));

        try
        {
            this.model.getSimulator().scheduleEventRel(TimeUnit.convert(10.0, TimeUnit.MINUTE), this, "reportStatistics", null);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Schedulable method to report activity duration.
     * @param duration double; the duration to report, in hours
     */
    public void reportActivityDuration(final double duration)
    {
        this.fireTimedEvent(new TimedEvent<Double>(DURATION_EVENT, duration, this.model.getSimulator().getSimulatorTime()));
    }

    /**
     * Implement an open / closure policy. In order to (re)open a location type, call this method with <br>
     * <code>
     *    locationType.setClosurePolicy(1.0, 1.0, locationType, locationType.getName());
     * </code>
     * @param fractionOpen double; the fraction of locations in this location type that stays open
     * @param fractionActivities double; the fraction of activities that will still take place (in open locations)
     * @param alternativeLocationType LocationType; the alternative location to spend time
     * @param reportAsLocationName String; the name under which the replacement activities need to be reported
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public void setClosurePolicy(final double fractionOpen, final double fractionActivities,
            final LocationType alternativeLocationType, final String reportAsLocationName)
    {
        this.fractionOpen = fractionOpen;
        this.fractionActivities = fractionActivities;
        this.alternativeLocationType = alternativeLocationType;
        this.reportAsLocationName = reportAsLocationName;
    }

    /**
     * Report that there is a violation of the capacity for a location. Store it in a Map to report, e.g. once an hour.
     * @param location Location; the location that reported a violation
     * @param nrPersons int; the number of persons that is currently in the location.
     */
    public void reportCapacityProblem(final Location location, final int nrPersons)
    {
        if (nrPersons > this.capacityOverflowMap.get(location))
            this.capacityOverflowMap.put(location, nrPersons);
    }

    /**
     * Report that one or more locations used more capacity than what was available for the location. Currently, the reporting
     * is done on the console. Later, it will be sent to the ResultWriter.
     */
    public void reportCapacityProblems()
    {
        for (Location location : this.capacityOverflowMap.keys(new Location[0]))
        {
            int nr = this.capacityOverflowMap.get(location);
            System.out.println(this.model.getSimulator().getSimulatorTime() + ": " + nr + " persons in " + location
                    + ". Surface=" + location.getTotalSurfaceM2() + "m2. Capacity/m2=" + this.capPersonsPerM2 + ". Persons/m2 "
                    + (nr / location.getTotalSurfaceM2()));
            this.fireEvent(CAPACITY_VIOLATION_EVENT, new Object[] {location, nr, location.getTotalSurfaceM2(),
                    this.capPersonsPerM2, nr / location.getTotalSurfaceM2()});
        }
        this.capacityOverflowMap.clear();
        this.fireEvent(CAPACITY_ALLOCATION_EVENT, new Object[] {this, this.failedAllocationAttempts, this.totalCapacity,
                this.numberPersons, this.numberReserved});
        if (this.failedAllocationAttempts > 0)
        {
            System.out.println(this.model.getSimulator().getSimulatorTime() + ": " + this.failedAllocationAttempts
                    + " persons could not go to " + this.name + " in the past hour");
            this.failedAllocationAttempts = 0;
        }
        try
        {
            this.model.getSimulator().scheduleEventRel(TimeUnit.convert(60.0, TimeUnit.MINUTE), this, "reportCapacityProblems",
                    null);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * @return fractionOpen
     */
    public double getFractionOpen()
    {
        return this.fractionOpen;
    }

    /**
     * @return fractionActivities
     */
    public double getFractionActivities()
    {
        return this.fractionActivities;
    }

    /**
     * @return alternativeLocationType
     */
    public LocationType getAlternativeLocationType()
    {
        return this.alternativeLocationType;
    }

    /**
     * @return reportAsLocationName
     */
    public String getReportAsLocationName()
    {
        return this.reportAsLocationName;
    }

    /**
     * @return the animationClass
     */
    public Class<? extends LocationAnimation> getAnimationClass()
    {
        return this.animationClass;
    }

    /**
     * @return the locationClass
     */
    public Class<? extends Location> getLocationClass()
    {
        return this.locationClass;
    }

    /**
     * Return whether the visit to this location by a person should result in the same sublocation or not.
     * @return boolean; whether the visit to this location by a person should result in the same sublocation or not
     */
    public boolean isReproducible()
    {
        return this.reproducible;
    }

    /**
     * Return the rate of contagiousness in this location type, default 1.0. This number can be reduced by e.g. social
     * distancing and ventilation. In that case the factor is less than 1. The factor should be in the interval (0, 1].
     * @return double; the rate of contagiousness in this location type
     */
    public double getCorrectionFactorArea()
    {
        return this.correctionFactorArea;
    }

    /**
     * Set the rate of contagiousness in this location type, default 1.0. This number can be reduced by e.g. social distancing
     * and ventilation. In that case the factor is less than 1. The factor should be in the interval (0, 1].
     * @param correctionFactorArea double; the rate of contagiousness in this location type
     */
    public void setCorrectionFactorArea(final double correctionFactorArea)
    {
        this.correctionFactorArea = correctionFactorArea;
    }

    /**
     * @return capConstrained
     */
    public boolean isCapConstrained()
    {
        return this.capConstrained;
    }

    /**
     * @return capPersonsPerM2
     */
    public double getCapPersonsPerM2()
    {
        return this.capPersonsPerM2;
    }

    /**
     * @return model
     */
    public MedlabsModelInterface getModel()
    {
        return this.model;
    }

    /**
     * @return the infectInSublocation
     */
    public boolean isInfectInSublocation()
    {
        return this.infectInSublocation;
    }

    /**
     * @return sizeFactor
     */
    public double getSizeFactor()
    {
        return this.sizeFactor;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getName();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.locationTypeid;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LocationType other = (LocationType) obj;
        if (this.locationTypeid != other.locationTypeid)
            return false;
        if (this.name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals(other.name))
            return false;
        return true;
    }
}
