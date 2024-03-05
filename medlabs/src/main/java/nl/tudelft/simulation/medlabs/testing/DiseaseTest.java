package nl.tudelft.simulation.medlabs.testing;

import nl.tudelft.simulation.medlabs.AbstractModelIdNamed;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * A DiseaseTest is a test that a person can do, with a positive or negative
 * outcome. Because a positive test outcome has very different consequences
 * (e.g., change of week pattern) than a negative test outcome (e.g., admission
 * to a festival), it is important to register the test outcome, rather than
 * just the test itself.
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
public class DiseaseTest extends AbstractModelIdNamed {
	/** */
	private static final long serialVersionUID = 20220110L;

	/**
	 * Create a named, identifiable, model aware test object. TODO: register the
	 * positive and negative outcome
	 * 
	 * @param model MedlabsModelInterface; the reference to the model
	 * @param id    int; the unique id of the test
	 * @param name  String; the name of the test
	 */
	public DiseaseTest(final MedlabsModelInterface model, final int id, final String name) {
		super(model, id, name);
	}

}
