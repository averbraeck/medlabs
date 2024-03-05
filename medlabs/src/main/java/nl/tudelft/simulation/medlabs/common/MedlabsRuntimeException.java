package nl.tudelft.simulation.medlabs.common;

/**
 * Runtime exception for the Medlabs project.
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
public class MedlabsRuntimeException extends RuntimeException {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a standard MedlabsRuntimeException.
	 */
	public MedlabsRuntimeException() {
		super();
	}

	/**
	 * Construct a MedlabsRuntimeException.
	 * 
	 * @param message the message to display
	 * @param cause   the underlying exception
	 */
	public MedlabsRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a MedlabsRuntimeException.
	 * 
	 * @param message the message to display
	 */
	public MedlabsRuntimeException(final String message) {
		super(message);
	}

	/**
	 * Construct a MedlabsRuntimeException.
	 * 
	 * @param cause the underlying exception
	 */
	public MedlabsRuntimeException(final Throwable cause) {
		super(cause);
	}

}
