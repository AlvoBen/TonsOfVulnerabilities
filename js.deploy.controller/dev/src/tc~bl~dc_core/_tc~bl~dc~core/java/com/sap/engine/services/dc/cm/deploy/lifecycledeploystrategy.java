package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LifeCycleDeployStrategy implements Serializable {

	private static final long serialVersionUID = -3955225713917071987L;

	public transient static final LifeCycleDeployStrategy BULK = new LifeCycleDeployStrategy(
			new Integer(0), "bulk");

	public transient static final LifeCycleDeployStrategy SEQUENTIAL = new LifeCycleDeployStrategy(
			new Integer(1), "sequential");

	public transient static final LifeCycleDeployStrategy DISABLE_LCM = new LifeCycleDeployStrategy(
			new Integer(2), "disable LCM");

	private transient static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		STRATEGY_MAP.put(BULK.getName(), BULK);
		STRATEGY_MAP.put(SEQUENTIAL.getName(), SEQUENTIAL);
		STRATEGY_MAP.put(DISABLE_LCM.getName(), DISABLE_LCM);
	}

	public static LifeCycleDeployStrategy getLifeCycleDeployStrategyByName(
			String name) {
		return (LifeCycleDeployStrategy) STRATEGY_MAP.get(name);
	}

	public static Map getNameAndLifeCycleDeployStrategy() {
		return STRATEGY_MAP;
	}

	private LifeCycleDeployStrategy(Integer id, String name) {
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

		if (!(obj instanceof LifeCycleDeployStrategy)) {
			return false;
		}

		final LifeCycleDeployStrategy other = (LifeCycleDeployStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
