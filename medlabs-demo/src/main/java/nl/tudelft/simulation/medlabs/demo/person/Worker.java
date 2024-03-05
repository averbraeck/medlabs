package nl.tudelft.simulation.medlabs.demo.person;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.index.IdxWorker;

/**
 * Worker.java.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved. The code is part of the HERoS
 * project (Health Emergency Response in Interconnected Systems), which builds
 * on the MEDLABS project. The simulation tools are aimed at providing policy
 * analysis tools to predict and help contain the spread of epidemics. They make
 * use of the DSOL simulation engine and the agent-based modeling formalism.
 * This software is licensed under the BSD license. See license.txt in the main
 * project.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Worker extends IdxWorker {
	/** */
	private static final long serialVersionUID = 20211001L;

	/**
	 * Create a Worker with a number of basic properties, including the work
	 * location type and work location that the worker goes to. The init() method
	 * has to be called after the worker has been created to make sure the disease
	 * state machine is started for the person if needed. The week pattern starts at
	 * day 0 and activity index 0.
	 * 
	 * @param model            MedlabsModelInterface; the model
	 * @param id               int; unique id number of the person in the
	 *                         Model.getPersons() array
	 * @param genderFemale     boolean; whether gender is female or not.
	 * @param age              byte; the age of the person
	 * @param homeLocationId   int; the location of the home in the list of house
	 *                         locations
	 * @param weekPatternIndex short; the index of the standard week pattern for the
	 *                         person; this is also the initial week pattern that
	 *                         the person will use
	 * @param workLocationId   int; the location index of the work location,
	 *                         relative to the workTypeIndex
	 */
	public Worker(final MedlabsModelInterface model, final int id, final boolean genderFemale, final byte age,
			final int homeLocationId, final short weekPatternIndex, final int workLocationId) {
		super(model, id, genderFemale, age, homeLocationId, weekPatternIndex, workLocationId);
	}

}
