package com.sap.engine.services.dc.api.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Indicates which version are relevant for deploy.</DD>
 * <DT><B>Usage: </B></DT>
 * <DD>
 * {@link com.sap.engine.services.dc.api.deploy.DeployProcessor#setComponentVersionHandlingRule(ComponentVersionHandlingRule)}
 * <BR>
 * e.g. deployProcessor.setComponentVersionHandlingRule(
 * ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS);</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-4-8</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor
 */
public final class ComponentVersionHandlingRule {
	/**
	 * Indicates to update all versions of deployed component do not regarding
	 * which versions of the component is already deployed. Always deploy.
	 */
	public static final ComponentVersionHandlingRule UPDATE_ALL_VERSIONS = new ComponentVersionHandlingRule(
			new Integer(0), "UpdateAllVersions");
	/**
	 * Deploys if the deployed item is not deployed. if there is already
	 * deployed component with the same name and vendor then compares versions
	 * and if the version of the deployed component is same or lower with the
	 * new one then procede with deploy procedure.
	 */
	public static final ComponentVersionHandlingRule UPDATE_SAME_AND_LOWER_VERSIONS_ONLY = new ComponentVersionHandlingRule(
			new Integer(1), "UpdateSameAndLowerVersionsOnly");
	/**
	 * Deploys only components which are not deployed or have higher version
	 * than the deployed one with the same name and vendor.
	 */
	public static final ComponentVersionHandlingRule UPDATE_LOWER_VERSIONS_ONLY = new ComponentVersionHandlingRule(
			new Integer(2), "UpdateLowerVersionsOnly");

	/**
	 * Deploys SCAs and top level SDAs which are not deployed or have higher
	 * version than the deployed one with the same name and vendor. Deploys SDAs
	 * contained in SCAs which are not deployed or have different version than
	 * the deployed one with the same name and vendor.
	 */
	public static final ComponentVersionHandlingRule UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY = new ComponentVersionHandlingRule(
			new Integer(3), "UpdateLowerOrChangedVersionsOnly");

	private final Integer id;
	private final String name;

	private static final Map VERSION_HANDLING_MAP = new HashMap();

	static {
		VERSION_HANDLING_MAP.put(UPDATE_ALL_VERSIONS.getName(),
				UPDATE_ALL_VERSIONS);
		VERSION_HANDLING_MAP.put(UPDATE_SAME_AND_LOWER_VERSIONS_ONLY.getName(),
				UPDATE_SAME_AND_LOWER_VERSIONS_ONLY);
		VERSION_HANDLING_MAP.put(UPDATE_LOWER_VERSIONS_ONLY.getName(),
				UPDATE_LOWER_VERSIONS_ONLY);
		VERSION_HANDLING_MAP.put(UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY
				.getName(), UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY);
	}

	private ComponentVersionHandlingRule(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this component version handling rule.
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
	 * Returns a component version handling rule by a given name
	 * 
	 * @param name
	 * @return component version handling rule
	 */
	public static ComponentVersionHandlingRule getComponentVersionHandlingRuleByName(
			String name) {
		return (ComponentVersionHandlingRule) VERSION_HANDLING_MAP.get(name);
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
		return this.id.hashCode();
	}

}