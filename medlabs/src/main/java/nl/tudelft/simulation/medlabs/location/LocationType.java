package nl.tudelft.simulation.medlabs.location;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.event.TimedEventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import nl.tudelft.simulation.medlabs.location.animation.LocationAnimation;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * LocationType is a set of types of locations that can appear in the simulation.
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
public class LocationType extends EventProducer
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

    /** resolution for grid in m. */
    private static double GRID_FACTOR = 500.0;

    /**
     * lat grid factor. Multiply the lat with this factor and truncate to get grid int. Optimized for Beijing = 40. 0.01 degree
     * at lon 116 = 1.112 km
     */
    private static double GRID_LAT_FACTOR = 111200.0 / GRID_FACTOR;

    /**
     * lon grid factor. Multiply the lon with this factor and truncate to get grid int. Optimized for Beijing = 116. 0.01 degree
     * at lat 40 = 0.852 km
     */
    private static double GRID_LON_FACTOR = 85200.0 / GRID_FACTOR;

    /** map of lat grid x lon grid -> location array indexes. */
    private Map<Long, Set<Integer>> gridLatLon = new HashMap<Long, Set<Integer>>();

    /** resolution for nearest cache in m. */
    private static double CACHE_FACTOR = 200.0;

    /** lat cache factor. Multiply the lat with this factor and truncate to get cache int. */
    private static double CACHE_LAT_FACTOR = 111200.0 / CACHE_FACTOR;

    /** lon cache factor. Multiply the lon with this factor and truncate to get grid int.. */
    private static double CACHE_LON_FACTOR = 85200.0 / CACHE_FACTOR;

    /** the model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected MedlabsModelInterface model;

    /** cache of nearest locations of THIS type to a given location of ANOTHER type. */
    private TLongIntMap nearestCache = new TLongIntHashMap();

    /** cache of set of closest locations with a certain distance from a cache grid location. */
    private Map<Long, Map<Float, Location[]>> distanceCache = new HashMap<Long, Map<Float, Location[]>>();

    /** number of persons in location type for fast statistics. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected int numberPersons = 0;

    /** statistics update event. */
    public static final TimedEventType STATISTICS_EVENT =
            new TimedEventType("STATISTICS_EVENT", new MetaData("numberPersons", "number of persons in this location type",
                    new ObjectDescriptor("numberPersons", "number of persons in this location type", Integer.class)));

    /** activity duration event. */
    public static final TimedEventType DURATION_EVENT =
            new TimedEventType("DURATION_EVENT", new MetaData("duration", "duration in this location type",
                    new ObjectDescriptor("duration", "duration in this location type", Double.class)));

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
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public LocationType(final MedlabsModelInterface model, final byte locationTypeId, final String name,
            final Class<? extends Location> locationClass, final Class<? extends LocationAnimation> animationClass,
            final boolean reproducible, final boolean infectInSublocation, final double correctionFactorArea)
    {
        this.model = model;
        this.locationTypeid = locationTypeId;
        this.name = name;
        this.animationClass = animationClass;
        this.locationClass = locationClass;
        this.reproducible = reproducible;
        this.infectInSublocation = infectInSublocation;
        this.correctionFactorArea = correctionFactorArea;
        model.getLocationTypeNameMap().put(name, this);
        model.getLocationTypeIndexMap().put(locationTypeId, this);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.name;
    }

    /**
     * @return the nameofLocType
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the locationArray
     */
    public TIntObjectMap<Location> getLocationMap()
    {
        return this.locationMap;
    }

    /**
     * @param location
     * @return long key based on lat and lon grid factors
     */
    private long keyGridLatLon(final Location location)
    {
        long gridLat = (long) (location.getLatitude() * GRID_LAT_FACTOR);
        long gridLon = (long) (location.getLongitude() * GRID_LON_FACTOR);
        return Integer.MAX_VALUE * gridLat + gridLon;
    }

    /**
     * @param location
     * @return long key based on lat and lon grid factors
     */
    private long keyCacheLatLon(final Location location)
    {
        long gridLat = (long) (location.getLatitude() * CACHE_LAT_FACTOR);
        long gridLon = (long) (location.getLongitude() * CACHE_LON_FACTOR);
        return Integer.MAX_VALUE * gridLat + gridLon;
    }

    /**
     * @param locationMap
     */
    public void setLocationMap(final TIntObjectMap<Location> locationMap)
    {
        this.locationMap = locationMap;

        // fill the lat/lon cache
        this.gridLatLon.clear();
        for (Location location : this.locationMap.values(new Location[0]))
        {
            long key = keyGridLatLon(location);
            Set<Integer> locationSet = this.gridLatLon.get(key);
            if (locationSet == null)
            {
                locationSet = new HashSet<Integer>();
                this.gridLatLon.put(key, locationSet);
            }
            // locationSet.add(location.getId());
            this.gridLatLon.get(key).add(location.getId());
        }

        // clear the caches
        this.nearestCache.clear();
        this.distanceCache.clear();
    }

    /**
     * @param location
     */
    public void setLocationgridLatLon(final Location location)
    {
        long key = keyGridLatLon(location);
        Set<Integer> locationSet = this.gridLatLon.get(key);
        if (locationSet == null)
        {
            locationSet = new HashSet<Integer>();
            this.gridLatLon.put(key, locationSet);
        }
        // locationSet.add(location.getId());
        this.gridLatLon.get(key).add(location.getId());

        long startLocationKey = keyCacheLatLon(location);
        this.nearestCache.put(startLocationKey, location.getId());

    }

    /**
     * @return the location type id (byte)
     */
    public byte getLocationTypeId()
    {
        return this.locationTypeid;
    }

    /**
     * Add a location to this type.
     * @param location Location; the corresponding location
     */
    public void addLocation(final Location location)
    {
        this.locationMap.put(location.getId(), location);
    }

    /**
     * @param startLocation
     * @param maxDistanceM max distance in meters
     * @return an array of locations of this location type with a max distance to the startLocation
     */
    public Location[] getLocationArrayMaxDistanceM(final Location startLocation, final double maxDistanceM)
    {
        return getLocationArrayMaxDistanceM(startLocation, maxDistanceM, true);
    }

    /**
     * @param startLocation
     * @param maxDistanceM max distance in meters
     * @param cache or not
     * @return an array of locations of this location type with a max distance to the startLocation
     */
    public Location[] getLocationArrayMaxDistanceM(final Location startLocation, final double maxDistanceM, final boolean cache)
    {
        // look up in the cache
        long keyCache = keyCacheLatLon(startLocation);
        if (this.distanceCache.containsKey(keyCache))
        {
            Location[] locArray = this.distanceCache.get(keyCache).get((float) maxDistanceM);
            if (locArray != null)
            {
                return locArray;
            }
        }

        Set<Location> locations = new HashSet<Location>();

        // see how many cells we use based on the resolution of the grid. go in both directions.
        long gridLat = (int) (startLocation.getLatitude() * GRID_LAT_FACTOR);
        long gridLon = (int) (startLocation.getLongitude() * GRID_LON_FACTOR);
        int delta = (int) Math.round(0.5 * maxDistanceM / GRID_FACTOR);

        for (long latIndex = gridLat - delta; latIndex <= gridLat + delta; latIndex++)
        {
            for (long lonIndex = gridLon - delta; lonIndex <= gridLon + delta; lonIndex++)
            {
                long key = Integer.MAX_VALUE * latIndex + lonIndex;

                Set<Integer> lonSet = this.gridLatLon.get(key);
                if (lonSet != null)
                {
                    for (int locIndex : lonSet)
                    {
                        Location location = this.locationMap.get(locIndex);
                        if (startLocation.distanceM(location) <= maxDistanceM)
                        {
                            locations.add(location);
                        }
                    }
                }
            }
        }

        Location[] locArray = locations.toArray(new Location[locations.size()]);
        if (cache && locArray.length > 0)
        {
            Map<Float, Location[]> distanceMap = this.distanceCache.get(keyCache);
            if (distanceMap == null)
            {
                distanceMap = new HashMap<Float, Location[]>();
                this.distanceCache.put(keyCache, distanceMap);
            }
            distanceMap.put((float) maxDistanceM, locArray);
        }
        return locArray;
    }

    /**
     * @param startLocation
     * @return the nearest location of this location type to the startLocation
     */
    public Location getNearestLocation(final Location startLocation)
    {
        long startLocationKey = keyCacheLatLon(startLocation);

        // look up in cache
        if (this.nearestCache.containsKey(startLocationKey))
        {
            return this.locationMap.get(this.nearestCache.get(startLocationKey));
        }

        // if not found, look in one cell
        Location nearestLocation = null;
        long key = keyGridLatLon(startLocation);
        Set<Integer> lonSet = this.gridLatLon.get(key);
        if (lonSet != null)
        {
            double minDistance = Double.MAX_VALUE;
            for (int locIndex : lonSet)
            {
                Location location = this.locationMap.get(locIndex);
                if (startLocation.distanceM(location) < minDistance)
                {
                    nearestLocation = location;
                    minDistance = startLocation.distanceM(location);
                }
            }

            this.nearestCache.put(startLocationKey, nearestLocation.getId());
            return nearestLocation;
        }

        // look in a wider distance, now 1 km
        Location[] locaarray = getLocationArrayMaxDistanceM(startLocation, 1000.0, false);
        TIntObjectMap<Location> cellLocations = new TIntObjectHashMap<>();
        for (int i = 0; i < locaarray.length; i++)
        {
            cellLocations.put(i, locaarray[i]);
        }

        // use all locations if nothing within 1000 meters
        if (cellLocations.size() == 0)
        {
            cellLocations = this.locationMap;
        }

        double minDistance = Double.MAX_VALUE;
        for (Location location : cellLocations.values(new Location[0]))
        {
            if (startLocation.distanceM(location) < minDistance)
            {
                nearestLocation = location;
                minDistance = startLocation.distanceM(location);
            }
        }

        if (nearestLocation == null)
        {
            return null;
        }
        this.nearestCache.put(startLocationKey, nearestLocation.getId());
        return nearestLocation;
    }

    /**
     * Schedulable method to report statistics every 5 minutes.
     */
    public void reportStatistics()
    {
        this.fireTimedEvent(new TimedEvent<Double>(STATISTICS_EVENT, this, this.numberPersons,
                this.model.getSimulator().getSimulatorTime()));

        try
        {
            this.model.getSimulator().scheduleEventRel(TimeUnit.convert(10.0, TimeUnit.MINUTE), this, this, "reportStatistics",
                    null);
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
        this.fireTimedEvent(
                new TimedEvent<Double>(DURATION_EVENT, this, duration, this.model.getSimulator().getSimulatorTime()));
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
     * @return the infectInSublocation
     */
    public boolean isInfectInSublocation()
    {
        return this.infectInSublocation;
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
