package com.sap.engine.services.dc.api.undeploy;

import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Describes undeployment strategies. The following strategies are possible:
 * <UL>
 * <LI>IF_DEPENDING_STOP - This is the default strategy. The Deploy Controller
 * checks within the repository whether there are deployed components that
 * depend on the one that has to be undeployed.<br>
 * If there are such components and they are not part of the list with ones
 * which have to be undeployed the Deploy Controller should cancel the
 * undeployment process.<br>
 * The dependent components have to be undeployed to succeed with the
 * undeployment.</LI>
 * <LI>UNDEPLOY_DEPENDING - The Deploy Controller checks within the repository
 * whether there are deployed components that depend on the one that has to be
 * undeployed.<br>
 * If there are such components the Deploy Controller has to undeploy them too.
 * </UL>
 * </DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public final class UndeploymentStrategy {
	/**
	 * Stop undeploy transaction if there is at least one
	 * <code>UndeployItem</code> on which other component depends.
	 */
	public static final UndeploymentStrategy IF_DEPENDING_STOP = new UndeploymentStrategy(
			new Integer(0), "IfDependingStop");

	/*
	 * public static final UndeploymentStrategy SKIP_DEPENDING = new
	 * UndeploymentStrategy(new Integer(1), "SkipDepending");
	 */
	/**
	 * Undeploy all components which depends of the given
	 * <code>UndeployItem</code>.
	 */
	public static final UndeploymentStrategy UNDEPLOY_DEPENDING = new UndeploymentStrategy(
			new Integer(2), "UndeployDepending");

	private static final Map STRATEGY_MAP = new HashMap();

	static {
		STRATEGY_MAP.put(IF_DEPENDING_STOP.getName(), IF_DEPENDING_STOP);
		STRATEGY_MAP.put(UNDEPLOY_DEPENDING.getName(), UNDEPLOY_DEPENDING);
	}

	private final Integer id;
	private final String name;

	private UndeploymentStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this undeployment strategy.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	/**
	 * Returns an undeployment strategy by a given stragtegy name.
	 * 
	 * @param name
	 *            of undeployment strategy
	 * @return undeployment strategy
	 */
	public static UndeploymentStrategy getUndeploymentStrategyByName(String name) {
		return (UndeploymentStrategy) STRATEGY_MAP.get(name);
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof UndeploymentStrategy)) {
			return false;
		}

		final UndeploymentStrategy other = (UndeploymentStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
