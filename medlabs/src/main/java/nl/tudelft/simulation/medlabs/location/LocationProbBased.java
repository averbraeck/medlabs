package nl.tudelft.simulation.medlabs.location;

import java.util.Map;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.PersonType;

/**
 * LocationProbBased.java.
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
public class LocationProbBased extends Location {
	/** the R0 multiplication factor with the focal city's infection rate. */
	private final double infectionRateFactor;

	/** the absolute infection rate to use. */
	private final double infectionRate;

	/**
	 * cache for the times when persons present in the location entered this
	 * location.
	 */
	private TIntDoubleMap enterTimes = new TIntDoubleHashMap();

	/**
	 * the exact phase a person needs to enter into when being exposed in the
	 * location based on probability.
	 */
	private final DiseasePhase exposed;

	/**
	 * this Person-to-Person map links the group in the location where the
	 * probability-based infection takes place to a reference group in the city to
	 * take the infection of the previous day from. The groups use the simple
	 * classname in the map (e.g., "Worker").
	 */
	private final Map<PersonType, PersonType> referenceGroup;

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
	 * @param infectionRateFactor  double; the infection rate multiplication factor
	 *                             with the focal city's infectionRate for the
	 *                             mapped social group of persons (see the
	 *                             referenceGroup parameter)
	 * @param infectionRate        double; the absolute infection rate factor to use
	 * @param referenceGroup       Map&lt;PersonType, PersonType&gt;; this maps the
	 *                             group in the location where the probability-based
	 *                             infection takes place to a reference group in the
	 *                             city to take the infection of the previous day
	 *                             from; the groups use the simple classname in the
	 *                             map (e.g., "Worker")
	 * @param exposed              DiseasePhase; the exact phase a person needs to
	 *                             enter into when being exposed in the location
	 *                             based on probability
	 */
	public LocationProbBased(final MedlabsModelInterface model, final int locationId, final byte locationTypeId,
			final float lat, final float lon, final short numberOfSubLocations, final float surfaceM2,
			final double infectionRateFactor, final double infectionRate,
			final Map<PersonType, PersonType> referenceGroup, final DiseasePhase exposed) {
		super(model, locationId, locationTypeId, lat, lon, numberOfSubLocations, surfaceM2);
		this.infectionRateFactor = infectionRateFactor;
		this.infectionRate = infectionRate;
		this.referenceGroup = referenceGroup;
		this.exposed = exposed;
	}

	/** {@inheritDoc} */
	@Override
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

		// NO CALCULATION OF infection spread in this location (BEFORE this person
		// actually enters)
		// but DO STORE the time this person entered to calculate duration in location
		// sublocations are NOT important because it is all probability based
		this.enterTimes.put(person.getId(), this.model.getSimulator().getSimulatorTime().doubleValue());

		this.persons.add(person.getId());
		person.setCurrentSubLocationIndex(index);
		locationType.numberPersons++;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removePerson(final Person person) {
		if (this.persons.remove(person.getId())) {
			this.model.getLocationTypeIndexMap().get(this.locationTypeId).numberPersons--;
			double now = this.model.getSimulator().getSimulatorTime().doubleValue();
			double duration = now - this.enterTimes.get(person.getId());
			this.enterTimes.remove(person.getId());
			if (person.getDiseasePhase().isSusceptible()) {
				if (this.infectionRate > 0.0) {
					// infection rate is per 24 hours
					if (this.model.getU01().draw() < duration * this.infectionRate / 24.0) {
						person.setExposureTime((float) now);
						// Take the person him/herself as the cause -- probability-based so probably
						// same type of person
						this.model.getPersonMonitor().reportExposure(person, this, person);
						this.model.getPersonMonitor().reportExposureByRate(person, this.locationTypeId, duration,
								this.infectionRate);
						this.model.getDiseaseProgression().changeDiseasePhase(person, this.exposed);
					}
				} else if (this.infectionRateFactor > 0.0) {
					// multiply this with the probability of the social group getting infected
					PersonType referencePT = this.referenceGroup
							.get(this.model.getPersonTypeClassMap().get(person.getClass()));
					if (referencePT == null) {
						System.err.println(
								String.format("Tried to map person %s to a reference group, but not present in %s",
										person, this.referenceGroup.toString()));
					} else {
						// Use yesterday's infection rate for the reference group in the model
						int infectedRef = this.model.getPersonMonitor().getYesterdayInfectionsPersonType()
								.get(referencePT.getId());
						double prob = (this.infectionRateFactor * infectedRef) / referencePT.getNumberPersons();
						if (this.model.getU01().draw() < duration * prob / 24.0) {
							person.setExposureTime((float) now);
							// Take the person him/herself as the cause -- probability-based so probably
							// same type of person
							this.model.getPersonMonitor().reportExposure(person, this, person);
							this.model.getPersonMonitor().reportExposureByRateFactor(person, this.locationTypeId,
									duration, this.infectionRateFactor, referencePT, infectedRef,
									referencePT.getNumberPersons());
							this.model.getDiseaseProgression().changeDiseasePhase(person, this.exposed);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * @return infectionRateFactor
	 */
	public double getInfectionRateFactor() {
		return this.infectionRateFactor;
	}

	/**
	 * @return infectionRate
	 */
	public double getInfectionRate() {
		return this.infectionRate;
	}

}
