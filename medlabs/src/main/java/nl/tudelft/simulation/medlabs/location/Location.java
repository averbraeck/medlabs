package nl.tudelft.simulation.medlabs.location;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.Point3d;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import nl.tudelft.simulation.medlabs.common.Coordinate;
import nl.tudelft.simulation.medlabs.common.ModelLocatable;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;

/**
 * The Location class is a point on the map, with a certain number of
 * sublocations, a surface, and persons that are present in that location. The
 * Location is the also the supertype of the special types of locations, such as
 * busses, metro's, and bus or metro stops.
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
public class Location implements ModelLocatable {
	/** The location id, which is a unique integer within the locationType. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected int locationId;

	/** The locationTypeId indicates the index number of the locationType. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected final byte locationTypeId;

	/** The model for looking up the simulator and other model objects. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected MedlabsModelInterface model;

	/** The latitude of the location. */
	private float lat;

	/** The longitude of the location. */
	private float lon;

	/** The number of sub locations (e.g., rooms). */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected short numberOfSubLocations;

	/** The total surface in m2. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected final float totalSurfaceM2;

	/** Whether the location is closed or not (e.g., as the result of a policy). */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected boolean closed;

	/** The ids of the persons in the location. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected TIntSet persons = new TIntHashSet();

	/**
	 * Create a location.
	 * 
	 * @param model                MedlabsModelInterface; the model for looking up
	 *                             the simulator and other model objects
	 * @param locationId           int; the location id within the locationType
	 * @param locationTypeId       byte; the index number of the locationType
	 * @param lat                  float; latitude of the location
	 * @param lon                  float; longitude of the location
	 * @param numberOfSubLocations short; number of sub locations (e.g., rooms)
	 * @param surfaceM2            float total surface in m2
	 */
	public Location(final MedlabsModelInterface model, final int locationId, final byte locationTypeId, final float lat,
			final float lon, final short numberOfSubLocations, final float surfaceM2) {
		this.model = model;
		this.locationId = locationId;
		this.locationTypeId = locationTypeId;
		this.lat = lat;
		this.lon = lon;
		this.numberOfSubLocations = numberOfSubLocations;
		this.totalSurfaceM2 = surfaceM2;
		this.closed = false;

		this.model.getLocationMap().put(locationId, this);
		this.model.getLocationTypeIndexMap().get(locationTypeId).getLocationMap().put(locationId, this);
	}

	/**
	 * Add a person to this location.
	 * 
	 * @param person person to add
	 */
	public void addPerson(final Person person) {
		// Calculate the sublocation index
		short index;
		LocationType locationType = this.model.getLocationTypeIndexMap().get(this.locationTypeId);
		if (locationType.getLocationTypeId() == this.model.getLocationTypeHouse().getLocationTypeId())
			index = person.getHomeSubLocationIndex();
		else if (this.numberOfSubLocations < 2)
			index = 0;
		else if (locationType.isReproducible())
			index = (short) this.model.getReproducibleJava2Random().nextInt(0, this.numberOfSubLocations,
					(person.hashCode() * 1000 + this.locationId));
		else
			index = (short) this.model.getRandomStream().nextInt(0, this.numberOfSubLocations);

		// just to be sure
		if (index >= this.numberOfSubLocations)
			index = (short) (this.numberOfSubLocations - 1);

		// calculate infection spread in this location (BEFORE this person actually
		// enters)
		getModel().getDiseaseTransmission().calculateTransmissionEnter(this, index, person);

		this.persons.add(person.getId());
		person.setCurrentSubLocationIndex(index);
		locationType.numberPersons++;
	}

	/**
	 * Remove a person from this location.
	 * 
	 * @param person person to remove
	 * @return whether person was there or not
	 */
	public boolean removePerson(final Person person) {
		// calculate infection spread in this location (BEFORE a potential infectious or
		// affected person leaves)
		getModel().getDiseaseTransmission().calculateTransmissionLeave(this, person.getCurrentSubLocationIndex(),
				person);

		if (this.persons.remove(person.getId())) {
			this.model.getLocationTypeIndexMap().get(this.locationTypeId).numberPersons--;
			return true;
		}
		return false;
	}

	/**
	 * @return the latitude of coordinate
	 */
	public float getLatitude() {
		return this.lat;
	}

	/**
	 * @return the lon
	 */
	public float getLongitude() {
		return this.lon;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(final float lat) {
		this.lat = lat;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(final float lon) {
		this.lon = lon;
	}

	/**
	 * @return coordinate based on lat and lon
	 */
	public Coordinate getCoordinate() {
		return new Coordinate(this.lat, this.lon);
	}

	/**
	 * @param location other location
	 * @return distance in meters to another location
	 */
	public double distanceM(final Location location) {
		if (getCoordinate() == null || location.getCoordinate() == null) {
			System.err.println("sth is wrong!");
		}
		return getCoordinate().distanceM(location.getCoordinate());
	}

	/**
	 * @return the numberOfSubLocations
	 */
	public short getNumberOfSubLocations() {
		return this.numberOfSubLocations;
	}

	/**
	 * @param numberOfSubLocations the numberOfSubLocations to set
	 */
	public void setNumberOfSubLocations(final short numberOfSubLocations) {
		this.numberOfSubLocations = numberOfSubLocations;
	}

	/**
	 * @return the totalSurfaceM2
	 */
	public float getTotalSurfaceM2() {
		return this.totalSurfaceM2;
	}

	/**
	 * @return the person ids in this location as a TIntSet
	 */
	public TIntSet getAllPersonIds() {
		return this.persons;
	}

	/**
	 * @return the persons in this location as a Java collection
	 */
	public Set<Person> getAllPersons() {
		Set<Person> personSet = new HashSet<>();
		for (TIntIterator it = this.persons.iterator(); it.hasNext();) {
			personSet.add(this.model.getPersonMap().get(it.next()));
		}
		return personSet;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.locationId;
	}

	/**
	 * @return the locationTypeId
	 */
	public byte getLocationTypeId() {
		return this.locationTypeId;
	}

	/**
	 * @return the locationType
	 */
	public LocationType getLocationType() {
		return this.model.getLocationTypeIndexMap().get(this.locationTypeId);
	}

	/** {@inheritDoc} */
	@Override
	public MedlabsModelInterface getModel() {
		return this.model;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Loc[" + getLocationType().getName() + "]." + this.locationId;
	}

	/**
	 * @see nl.tudelft.simulation.dsol.animation.LocatableInterface#getLocation()
	 */
	@Override
	public Point3d getLocation() {
		return new Point3d(this.getLongitude(), this.getLatitude(), 0.01);
	}

	/**
	 * Constant for Bounds3d; one degree in meters is 111 km; we make the Bounds3d
	 * for clicking 10x10x10 m.
	 */
	private static Bounds3d locationBounds = new Bounds3d(10.0 / 111000.0, 10.0 / 111000.0, 10.0 / 111000.0);

	/** {@inheritDoc} */
	@Override
	public Bounds3d getBounds() throws RemoteException {
		return locationBounds;
	}

	/**
	 * @return is location closed
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * @param closed the closed to set
	 */
	public void setClosed(final boolean closed) {
		this.closed = closed;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.locationId;
		result = prime * result + this.locationTypeId;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("checkstyle:needbraces")
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (this.locationId != other.locationId)
			return false;
		if (this.locationTypeId != other.locationTypeId)
			return false;
		return true;
	}

}
