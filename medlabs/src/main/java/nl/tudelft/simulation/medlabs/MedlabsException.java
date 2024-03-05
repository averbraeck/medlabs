package nl.tudelft.simulation.medlabs;

/**
 * MedlabsException is the typed exception for the MedLabs project.
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
public class MedlabsException extends Exception {
	/** */
	private static final long serialVersionUID = 20211229L;

	/**
	 * Default exception without arguments.
	 */
	public MedlabsException() {
		super();
	}

	/**
	 * Exception with a custom message.
	 * 
	 * @param message String; the message
	 */
	public MedlabsException(final String message) {
		super(message);
	}

	/**
	 * Exception caused by another exception.
	 * 
	 * @param cause Throwable; the cause of this exception
	 */
	public MedlabsException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Exception with a custom message, caused by another exception.
	 * 
	 * @param message String; the message
	 * @param cause   Throwable; the cause of this exception
	 */
	public MedlabsException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
