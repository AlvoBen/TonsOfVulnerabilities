package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UndeploymentStrategy implements Serializable {

	private static final long serialVersionUID = -8179615862613707920L;

	public transient static final UndeploymentStrategy IF_DEPENDING_STOP = new UndeploymentStrategy(
			new Integer(0), "IfDependingStop");

	public transient static final UndeploymentStrategy SKIP_DEPENDING = new UndeploymentStrategy(
			new Integer(1), "SkipDepending");

	public transient static final UndeploymentStrategy UNDEPLOY_DEPENDING = new UndeploymentStrategy(
			new Integer(2), "UndeployDepending");

	private transient static final Map STATEGY_MAP = new HashMap();

	static {
		STATEGY_MAP.put(IF_DEPENDING_STOP.getName(), IF_DEPENDING_STOP);
		STATEGY_MAP.put(SKIP_DEPENDING.getName(), SKIP_DEPENDING);
		STATEGY_MAP.put(UNDEPLOY_DEPENDING.getName(), UNDEPLOY_DEPENDING);
	}

	public static UndeploymentStrategy getUndeploymentStrategyByName(String name) {
		return (UndeploymentStrategy) STATEGY_MAP.get(name);
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

	public String getName() {
		return this.name;
	}

	public String toString() {
		return name;
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
		return id.hashCode();
	}

}
