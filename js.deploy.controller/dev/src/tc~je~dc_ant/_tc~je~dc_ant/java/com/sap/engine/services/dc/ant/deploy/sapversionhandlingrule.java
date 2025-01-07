package com.sap.engine.services.dc.ant.deploy;

import java.util.HashMap;
import java.util.Map;

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
public final class SAPVersionHandlingRule {

	/**
	 * indicates to update all versions of deployed component do not regarding
	 * which versions of the component is already deployed. Always deploy.
	 */
	public static final SAPVersionHandlingRule UPDATE_ALL_VERSIONS = new SAPVersionHandlingRule(
			new Integer(0), "all");

	/**
	 * deploys if the deployed item is not deployed. if there is already
	 * deployed component with the same name and vendor then compares versions
	 * and if the version of the deployed component is same or lower with the
	 * new one then procede with deploy procedure
	 */
	public static final SAPVersionHandlingRule UPDATE_SAME_AND_LOWER_VERSIONS_ONLY = new SAPVersionHandlingRule(
			new Integer(1), "same_and_lower");

	/**
	 * deploys only components which are not deployed or have higher version
	 * than the deployed one with the same name and vendor
	 */
	public static final SAPVersionHandlingRule UPDATE_LOWER_VERSIONS_ONLY = new SAPVersionHandlingRule(
			new Integer(2), "lower");

	private static final Map VERSION_RULES_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		VERSION_RULES_MAP.put(UPDATE_ALL_VERSIONS.getName(),
				UPDATE_ALL_VERSIONS);
		VERSION_RULES_MAP.put(UPDATE_SAME_AND_LOWER_VERSIONS_ONLY.getName(),
				UPDATE_SAME_AND_LOWER_VERSIONS_ONLY);
		VERSION_RULES_MAP.put(UPDATE_LOWER_VERSIONS_ONLY.getName(),
				UPDATE_LOWER_VERSIONS_ONLY);
	}

	public static SAPVersionHandlingRule getComponentVersionHandlingRuleByName(
			String name) {
		return (SAPVersionHandlingRule) VERSION_RULES_MAP.get(name);
	}

	public static boolean isValid(String name) {
		return VERSION_RULES_MAP.containsKey(name);
	}

	private SAPVersionHandlingRule(Integer id, String name) {
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

		if (!(obj instanceof SAPVersionHandlingRule)) {
			return false;
		}

		SAPVersionHandlingRule other = (SAPVersionHandlingRule) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
