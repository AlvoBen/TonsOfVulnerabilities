/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.ant.undeploy;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class represents the data for the undeploymnt strategy determined in the
 * undeployment task.
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPUndeploymentStrategy {

	/**
	 * Indicates to stop components depending on the undeployed one.
	 */
	public static final SAPUndeploymentStrategy IF_DEPENDING_STOP = new SAPUndeploymentStrategy(
			new Integer(0), "IfDependingStop");

	/**
	 * Indicates to undeploy the components depending on the undeployed one
	 */
	public static final SAPUndeploymentStrategy UNDEPLOY_DEPENDING = new SAPUndeploymentStrategy(
			new Integer(2), "UndeployDepending");

	private static final Map UNDEPLOY_STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		UNDEPLOY_STRATEGY_MAP.put(IF_DEPENDING_STOP.getName(),
				IF_DEPENDING_STOP);
		UNDEPLOY_STRATEGY_MAP.put(UNDEPLOY_DEPENDING.getName(),
				UNDEPLOY_DEPENDING);
	}

	public static SAPUndeploymentStrategy getComponentUndeploymentStrategyByName(
			String name) {
		return (SAPUndeploymentStrategy) UNDEPLOY_STRATEGY_MAP.get(name);
	}

	public static boolean isValid(String name) {
		return UNDEPLOY_STRATEGY_MAP.containsKey(name);
	}

	private SAPUndeploymentStrategy(Integer id, String name) {
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
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SAPUndeploymentStrategy)) {
			return false;
		}

		SAPUndeploymentStrategy other = (SAPUndeploymentStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
