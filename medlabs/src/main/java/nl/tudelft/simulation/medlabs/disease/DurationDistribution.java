package nl.tudelft.simulation.medlabs.disease;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.medlabs.simulation.TimeUnit;

/**
 * The ContagiousPeriod class is a helper class that stores the stochastic
 * duration for a contagious period.
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
public class DurationDistribution {
	/** the duration of Contagious Period. */
	private final DistContinuous durationDistribution;

	/** the unit. */
	private final TimeUnit unit;

	/**
	 * Create a stochastic contagious period based on a distribution specified in a
	 * time unit.
	 * 
	 * @param durationDistribution DistContinuous; the distribution function,
	 *                             specified in the unit
	 * @param unit                 TimeUnit the unit in which the distribution has
	 *                             been specified
	 */
	public DurationDistribution(final DistContinuous durationDistribution, final TimeUnit unit) {
		super();
		this.durationDistribution = durationDistribution;
		this.unit = unit;
	}

	/**
	 * Return a drawn duration of the contagious period in hours.
	 * 
	 * @return double; drawn duration of the contagious period in hours
	 */
	public double getDuration() {
		return TimeUnit.convert(this.durationDistribution.draw(), this.unit);
	}

}
