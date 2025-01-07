package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class DeployWorkflowStrategy implements Serializable {

	private transient static final long serialVersionUID = -7752898341868128372L;

	public transient static final DeployWorkflowStrategy NORMAL = new DeployWorkflowStrategy(
			new Integer(0), "normal");

	public transient static final DeployWorkflowStrategy SAFETY = new DeployWorkflowStrategy(
			new Integer(1), "safety");

	/**
	 *&quot;ROLLING&quot; mode
	 * 
	 * @deprecated The item will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	public transient static final DeployWorkflowStrategy ROLLING = new DeployWorkflowStrategy(
			new Integer(2), "rolling");

	private transient static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		STRATEGY_MAP.put(NORMAL.getName(), NORMAL);
		STRATEGY_MAP.put(SAFETY.getName(), SAFETY);
		STRATEGY_MAP.put(ROLLING.getName(), ROLLING);
	}

	public static DeployWorkflowStrategy getDeployStrategyByName(String name) {
		return (DeployWorkflowStrategy) STRATEGY_MAP.get(name);
	}

	public static Map getNameAndDeployWorkflowStrategy() {
		return STRATEGY_MAP;
	}

	private DeployWorkflowStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.toString = name + " deploy strategy";
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeployWorkflowStrategy)) {
			return false;
		}

		DeployWorkflowStrategy other = (DeployWorkflowStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
