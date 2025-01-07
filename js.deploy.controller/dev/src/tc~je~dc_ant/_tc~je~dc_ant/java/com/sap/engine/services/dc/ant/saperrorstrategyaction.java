package com.sap.engine.services.dc.ant;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.dc.api.ErrorStrategy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-9
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPErrorStrategyAction {

	/**
	 * valid for both processes deployment and undeployment
	 */
	public static final SAPErrorStrategyAction PREREQUISITES_CHECK_ACTION = new SAPErrorStrategyAction(
			new Integer(0), "prerequisites");

	/**
	 * make sense only for deployProcessor
	 */
	public static final SAPErrorStrategyAction DEPLOYMENT_ACTION = new SAPErrorStrategyAction(
			new Integer(1), "deploy");

	/**
	 * make sense only for undeployProcessor
	 */
	public static final SAPErrorStrategyAction UNDEPLOYMENT_ACTION = new SAPErrorStrategyAction(
			new Integer(2), "undeploy");

	private static final Map ACTIONS_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		ACTIONS_MAP.put(PREREQUISITES_CHECK_ACTION.getName(),
				PREREQUISITES_CHECK_ACTION);
		ACTIONS_MAP.put(DEPLOYMENT_ACTION.getName(), DEPLOYMENT_ACTION);
		ACTIONS_MAP.put(UNDEPLOYMENT_ACTION.getName(), UNDEPLOYMENT_ACTION);
	}

	public static SAPErrorStrategyAction getErrorStrategyActionByName(
			String name) {
		return (SAPErrorStrategyAction) ACTIONS_MAP.get(name);
	}

	public static boolean isValid(String name) {
		return ACTIONS_MAP.containsKey(name);
	}

	private SAPErrorStrategyAction(Integer id, String name) {
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

		if (!(obj instanceof ErrorStrategy)) {
			return false;
		}

		SAPErrorStrategyAction other = (SAPErrorStrategyAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
