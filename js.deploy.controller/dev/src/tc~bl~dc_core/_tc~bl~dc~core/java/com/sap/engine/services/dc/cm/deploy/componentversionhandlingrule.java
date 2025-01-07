package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-8
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class ComponentVersionHandlingRule implements Serializable {

	private static final long serialVersionUID = -4006016221188704738L;

	public transient static final ComponentVersionHandlingRule UPDATE_ALL_VERSIONS = new ComponentVersionHandlingRule(
			new Integer(0), "UpdateAllVersions");

	public transient static final ComponentVersionHandlingRule UPDATE_SAME_AND_LOWER_VERSIONS_ONLY = new ComponentVersionHandlingRule(
			new Integer(1), "UpdateSameAndLowerVersionsOnly");

	public transient static final ComponentVersionHandlingRule UPDATE_LOWER_VERSIONS_ONLY = new ComponentVersionHandlingRule(
			new Integer(2), "UpdateLowerVersionsOnly");

	public static final ComponentVersionHandlingRule UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY = new ComponentVersionHandlingRule(
			new Integer(3), "UpdateLowerOrChangedVersionsOnly");

	private transient static final Map VERSION_RULE_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		VERSION_RULE_MAP
				.put(UPDATE_ALL_VERSIONS.getName(), UPDATE_ALL_VERSIONS);
		VERSION_RULE_MAP.put(UPDATE_SAME_AND_LOWER_VERSIONS_ONLY.getName(),
				UPDATE_SAME_AND_LOWER_VERSIONS_ONLY);
		VERSION_RULE_MAP.put(UPDATE_LOWER_VERSIONS_ONLY.getName(),
				UPDATE_LOWER_VERSIONS_ONLY);
		VERSION_RULE_MAP.put(UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY.getName(),
				UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY);
	}

	public static Map getNameAndComponentVersionHandlingRule() {
		return VERSION_RULE_MAP;
	}

	public static ComponentVersionHandlingRule getComponentVersionHandlingRuleByName(
			String name) {
		return (ComponentVersionHandlingRule) VERSION_RULE_MAP.get(name);
	}

	private ComponentVersionHandlingRule(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
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

		if (!(obj instanceof ComponentVersionHandlingRule)) {
			return false;
		}

		ComponentVersionHandlingRule other = (ComponentVersionHandlingRule) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
