package com.sap.engine.services.dc.api.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description: The value of the
 * LifeCycleDeployStrategy determines the way the Deploy Controller deploys the
 * components.
 * <ul>
 * <b>The possible life cycle deploy strategies are:</B>
 * <li>BULK - Deploy Controller first delivers the components to the containers
 * and after all the components are delivered they are started in the same order
 * they have been delivered.</li>
 * <li>SEQUENTIAL - Deploy Controller sequentially delivers a component to the
 * containers and then starts it.</li>
 * <li>DISABLE_LCM - Deploy Controller delivers the components to the containers
 * without starting the components.</li>
 * </ul>
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LifeCycleDeployStrategy {
	/**
	 * Deploy all components and at the end tries to start all of them.
	 */
	public static final LifeCycleDeployStrategy BULK = new LifeCycleDeployStrategy(
			new Integer(0), "bulk");
	/**
	 * Deploy with sequential start for each distinct component among the batch.
	 */
	public static final LifeCycleDeployStrategy SEQUENTIAL = new LifeCycleDeployStrategy(
			new Integer(1), "sequential");
	/**
	 * Do not start component after the deployment.
	 */
	public static final LifeCycleDeployStrategy DISABLE_LCM = new LifeCycleDeployStrategy(
			new Integer(2), "disable LCM");

	private static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		STRATEGY_MAP.put(BULK.getName(), BULK);
		STRATEGY_MAP.put(SEQUENTIAL.getName(), SEQUENTIAL);
		STRATEGY_MAP.put(DISABLE_LCM.getName(), DISABLE_LCM);
	}

	/**
	 * Returns a life cycle deploy strategy by a given strategy name.
	 * 
	 * @param name
	 *            of life cycle deploy strategy
	 * @return life cycle deploy strategy
	 */
	public static LifeCycleDeployStrategy getLifeCycleDeployStrategyByName(
			String name) {
		return (LifeCycleDeployStrategy) STRATEGY_MAP.get(name);
	}

	private LifeCycleDeployStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.toString = name + " deploy strategy";
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this life cycle deploy strategy.
	 * 
	 * @return name
	 */
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

		LifeCycleDeployStrategy other = (LifeCycleDeployStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
