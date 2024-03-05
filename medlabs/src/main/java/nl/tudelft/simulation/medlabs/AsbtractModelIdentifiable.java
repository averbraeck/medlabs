package nl.tudelft.simulation.medlabs;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;

/**
 * AsbtractModelIdentifiable can be used as a superclass for classes with an
 * integer id and a reference to the model.
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
public abstract class AsbtractModelIdentifiable implements ModelIdentifiable {
	/** */
	private static final long serialVersionUID = 20211229L;

	/** the id of the object. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected int id;

	/** the reference to the model. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected MedlabsModelInterface model;

	/**
	 * Create model aware object with an int id.
	 * 
	 * @param id    int; the id of the object
	 * @param model MedlabsModelInterface; the reference to the model
	 */
	public AsbtractModelIdentifiable(final int id, final MedlabsModelInterface model) {
		Throw.whenNull(model, "model cannot be null");
		this.id = id;
		this.model = model;
	}

	/** {@inheritDoc} */
	@Override
	public int getId() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	public MedlabsModelInterface getModel() {
		return this.model;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("checkstyle:needbraces")
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AsbtractModelIdentifiable other = (AsbtractModelIdentifiable) obj;
		if (this.id != other.id)
			return false;
		return true;
	}

}
