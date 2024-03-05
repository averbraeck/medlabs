package nl.tudelft.simulation.medlabs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.DistNormalTrunc;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.common.MedlabsException;

/**
 * DistributionParserTest tests the distribution parser.
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
public class DistributionParserTest {
	@Test
	public void testContinuousParser() throws MedlabsException {
		// distConstant
		StreamInterface stream = new MersenneTwister(2L);
		DistContinuous dc;
		for (String s : new String[] { "constant(10)", "Const(10.0)", " CONSTANT( 10.0 )" }) {
			dc = DistributionParser.parseDistContinuous(s, stream);
			assertTrue(dc instanceof DistConstant);
			assertEquals(10.0, ((DistConstant) dc).getConstant(), 1E-6);
		}

		// check for error handling
		for (String s : new String[] { "constantx(10)", "Const(10.0, 11.0)", " CONSTANT()", "constant",
				"const(x,y)," + "constant(10", "constant 10)" }) {
			Try.testFail(new Try.Execution() {
				@Override
				public void execute() throws Throwable {
					DistributionParser.parseDistContinuous(s, stream);
				}
			}, MedlabsException.class);
		}

		// exponential
		for (String s : new String[] { "Exponential(12.0)", "expo(12)" }) {
			dc = DistributionParser.parseDistContinuous(s, stream);
			assertTrue(dc instanceof DistExponential);
			assertEquals(12.0, ((DistExponential) dc).getMean(), 1E-6);
		}

		// triangular
		for (String s : new String[] { "Triangular(1, 2, 3)", "tria(1.0, 2.0, 3.0)", "TRIA(1,2,3)" }) {
			dc = DistributionParser.parseDistContinuous(s, stream);
			assertTrue(dc instanceof DistTriangular);
			assertEquals(1.0, ((DistTriangular) dc).getMin(), 1E-6);
			assertEquals(2.0, ((DistTriangular) dc).getMode(), 1E-6);
			assertEquals(3.0, ((DistTriangular) dc).getMax(), 1E-6);
		}

		// normal
		for (String s : new String[] { "normal(12., 2.0)", "NORM(12, 2)" }) {
			dc = DistributionParser.parseDistContinuous(s, stream);
			assertTrue(dc instanceof DistNormal);
			assertEquals(12.0, ((DistNormal) dc).getMu(), 1E-6);
			assertEquals(2.0, ((DistNormal) dc).getSigma(), 1E-6);
		}

		// normaltrunc
		for (String s : new String[] { "TruncatedNormal(12., 2.0, 10, 14)", "normaltrunc(12, 2, 10, 14)" }) {
			dc = DistributionParser.parseDistContinuous(s, stream);
			assertTrue(dc instanceof DistNormalTrunc);
			assertEquals(12.0, ((DistNormalTrunc) dc).getMu(), 1E-6);
			assertEquals(2.0, ((DistNormalTrunc) dc).getSigma(), 1E-6);
			assertEquals(10.0, ((DistNormalTrunc) dc).getMin(), 1E-6);
			assertEquals(14.0, ((DistNormalTrunc) dc).getMax(), 1E-6);
		}
	}
}
