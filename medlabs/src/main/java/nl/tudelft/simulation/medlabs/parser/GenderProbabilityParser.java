package nl.tudelft.simulation.medlabs.parser;

import nl.tudelft.simulation.medlabs.common.MedlabsException;

/**
 * The genderProbabilityParser utility class can parse a string with
 * probabilities that differ per gender. The format is, e.g.,
 * 
 * <pre>
 *   gender{M: 0.021, F: 0.016}
 * </pre>
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
public final class GenderProbabilityParser {
	/**
	 * Utility class, no constructor.
	 */
	private GenderProbabilityParser() {
		// Utility class, no constructor
	}

	/**
	 * Parse a string with a gender-dependent probability. It always has to have the
	 * format gender{M: 0.021, F: 0.016}. The result can be used based on
	 * person.genderFemale as follows:
	 * 
	 * <pre>
	 *   if (getModel().distU01().draw() < getAgeProb()[person.genderFemale? 0 : 1])
	 *     ...
	 * </pre>
	 * 
	 * @param probStr String; the textual representation of the probabilities
	 * @return double[2]; where [0] indicates the probability for female, and [1]
	 *         the probability for male
	 * @throws MedlabsException when the string could not be parsed
	 */
	public static double[] parseGenderProbabilities(final String probStr) throws MedlabsException {
		if (!probStr.startsWith("gender{"))
			throw new MedlabsException(
					"parsing gender-probabilities '" + probStr + "': string does not start with 'gender{'");
		if (!probStr.trim().endsWith("}"))
			throw new MedlabsException("parsing gender-probabilities '" + probStr + "': does not end with '}'");
		double[] result = new double[2];
		result[0] = Double.NaN;
		result[1] = Double.NaN;
		String args = probStr.substring(probStr.indexOf('{') + 1, probStr.indexOf('}')).trim();
		String[] argArray = args.split(",");
		if (argArray.length != 2)
			throw new MedlabsException("parsing gender-probabilities '" + probStr + "': not containing two entries");
		for (String arg : argArray) {
			if (arg.indexOf(":") == -1)
				throw new MedlabsException(
						"parsing gender-probabilities '" + probStr + "': has entry " + arg + " without ':'");
			String[] arg2 = arg.split(":");
			if (arg2[0].toUpperCase().trim().equals("F")) {
				result[0] = argDouble(probStr, arg2[1]);
			} else if (arg2[0].toUpperCase().trim().equals("M")) {
				result[1] = argDouble(probStr, arg2[1]);
			} else
				throw new MedlabsException(
						"parsing gender-probabilities '" + probStr + "': has entry " + arg + ", not M or F");
		}
		if (Double.isNaN(result[0]))
			throw new MedlabsException("parsing gender-probabilities '" + probStr + "': F entry missing");
		if (Double.isNaN(result[1]))
			throw new MedlabsException("parsing gender-probabilities '" + probStr + "': M entry missing");
		return result;
	}

	private static double argDouble(final String probStr, final String arg) throws MedlabsException {
		try {
			return Double.parseDouble(arg.trim());
		} catch (Exception exception) {
			throw new MedlabsException(
					"parsing gender-probabilities '" + probStr + "': entry " + arg + " is not a double");
		}
	}
}
