package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-13
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class UndeployWorkflowStrategy implements Serializable {

	private static final long serialVersionUID = 6288681035656690269L;

	public transient static final UndeployWorkflowStrategy NORMAL = new UndeployWorkflowStrategy(
			new Integer(0), "normal");

	public transient static final UndeployWorkflowStrategy SAFETY = new UndeployWorkflowStrategy(
			new Integer(1), "safety");

	private transient static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		STRATEGY_MAP.put(NORMAL.getName(), NORMAL);
		STRATEGY_MAP.put(SAFETY.getName(), SAFETY);
	}

	public static UndeployWorkflowStrategy getUndeployWorkflowStrategyByName(
			String name) {
		return (UndeployWorkflowStrategy) STRATEGY_MAP.get(name);
	}

	public static Map getNameAndUndeployWorkflowStrategy() {
		return STRATEGY_MAP;
	}

	private UndeployWorkflowStrategy(Integer id, String name) {
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

		if (!(obj instanceof UndeployWorkflowStrategy)) {
			return false;
		}

		UndeployWorkflowStrategy other = (UndeployWorkflowStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
