package nl.tudelft.simulation.medlabs.person;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * A Person is an Agent with a number of characteristics such as age, locations,
 * a disease status, and an activity pattern.
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
public abstract class AbstractPerson extends Agent implements Person {
	/** */
	private static final long serialVersionUID = 20201001L;

	/**
	 * gender as boolean because of differences in disease spread related to gender.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected boolean genderFemale;

	/** age because of differences in disease spread related to age. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected byte age;

	/**
	 * Create a Person with a number of basic properties. The init() method has to
	 * be called after the person has been created to make sure the disease state
	 * machine is started for the person if needed.
	 * 
	 * @param model        MedlabsModelInterface; the model
	 * @param id           int; unique id number of the person in the
	 *                     Model.getPersons() array
	 * @param genderFemale boolean; whether gender is female or not.
	 * @param age          byte; the age of the person
	 */
	public AbstractPerson(final MedlabsModelInterface model, final int id, final boolean genderFemale, final byte age) {
		super(model, id);
		this.genderFemale = genderFemale;
		this.age = age;

		// DON'T FORGET TO CALL INIT AFTER THE CONSTRUCTION OF A PERSON!
	}

	/** {@inheritDoc} */
	@Override
	public int getAge() {
		return this.age;
	}

	/** {@inheritDoc} */
	@Override
	public boolean getGenderFemale() {
		return this.genderFemale;
	}

	/** {@inheritDoc} */
	@Override
	public double getFloatProperty(final String name) {
		switch (name) {
		case "age":
			return this.age;
		case "genderFemale":
			return this.genderFemale ? 1f : 0f;
		default:
			return this.model.getPersonProperties().getFloat(name, getId());
		}
	}

}
