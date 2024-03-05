package nl.tudelft.simulation.medlabs.activity;

import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * Travel activity for walking. The activity takes place in a large area where
 * probability for transmission is zero. The "walk" location in which the
 * activity takes place can be used to study the number of people who are
 * outside walking.
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
public class TravelActivityWalk extends TravelActivity {
	/** */
	private static final long serialVersionUID = 20140505L;

	/**
	 * Create an Activity type for a single person walking from A to B.
	 * 
	 * @param model         MedlabsModelInterface; pointer to the model for
	 *                      retrieving simulator and other relevant information
	 * @param name          String; the name of the activity
	 * @param travelLocator
	 * @param startLocator
	 * @param endLocator
	 */
	public TravelActivityWalk(final MedlabsModelInterface model, final String name,
			final LocatorInterface travelLocator, final LocatorInterface startLocator,
			final LocatorInterface endLocator) {
		super(model, name, travelLocator, startLocator, endLocator);
	}

	/** {@inheritDoc} */
	@Override
	public double getDuration(final Person person) {
		return Double.NaN;
	}

	/** {@inheritDoc} */
	@Override
	protected double getDuration(final Person person, final Location startLocation, final Location endLocation) {
		// assume a speed of 3.6 km/h based on straight distance = 1.0 m/s
		double distanceM = startLocation.distanceM(endLocation);
		double maxDuration = 3600.0; // don't walk longer than one hour
		if (distanceM < maxDuration) {
			return TimeUnit.convert(distanceM, TimeUnit.SECOND);
		} else {
			return TimeUnit.convert(maxDuration, TimeUnit.SECOND);
		}
	}

}
