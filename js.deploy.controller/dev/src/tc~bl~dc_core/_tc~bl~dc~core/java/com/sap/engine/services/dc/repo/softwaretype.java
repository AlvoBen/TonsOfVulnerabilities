package com.sap.engine.services.dc.repo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.util.StringUtils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class SoftwareType implements Serializable {

	private static final long serialVersionUID = 1611067989319044893L;

	private transient static final String DEFAULT_SUB_TYPE_NAME = "";
	private transient static final String DEFAULT_DESCRIPTION = "";

	private transient static int idIdx = 10000;

	private transient static final Map softwareTypeContainer = new HashMap();

	private final Integer id;
	private String name;
	private String subTypeName;
	private String description;
	private String toString;

	/**
	 * The operation searches for a <code>SoftwareType</code> with the specified
	 * name within the internal container which consists of all supported
	 * <code>SoftwareType</code>s. If there is no supported
	 * <code>SoftwareType</code> with such a name, the operation creates a new
	 * one with the specified name;
	 * 
	 * @param softwareTypeName
	 *            specifies the <code>SoftwareType</code> name.
	 * @return <code>SoftwareType</code> with the specified name.
	 */
	public static synchronized SoftwareType getSoftwareTypeByName(
			String softwareTypeName) {
		return getSoftwareTypeByName(softwareTypeName, DEFAULT_SUB_TYPE_NAME);
	}

	public static Set getAllSoftwareTypes() {
		final Set result = new HashSet(softwareTypeContainer.values());
		return Collections.unmodifiableSet(result);
	}

	public static synchronized SoftwareType getSoftwareTypeByName(
			final String softwareTypeName, final String softwareSubTypeName) {
		checkArg(softwareTypeName, "softwareTypeName");
		final String swtSubTypeName = softwareSubTypeName != null ? softwareSubTypeName
				: DEFAULT_SUB_TYPE_NAME;

		SoftwareType softwareType = (SoftwareType) softwareTypeContainer
				.get(getSoftwareTypeUniqueKey(softwareTypeName, swtSubTypeName));
		if (softwareType == null) {
			softwareType = new SoftwareType(new Integer(++idIdx),
					softwareTypeName, swtSubTypeName, DEFAULT_DESCRIPTION);
		}

		return softwareType;
	}

	public static synchronized SoftwareType getSoftwareType(final Integer id,
			final String softwareTypeName, final String softwareSubTypeName,
			final String softwareTypeDescription, final boolean toCreate) {
		checkArg(softwareTypeName, "softwareTypeName");
		final String swtSubTypeName = softwareSubTypeName != null ? softwareSubTypeName
				: DEFAULT_SUB_TYPE_NAME;

		final String stUniqueKey = getSoftwareTypeUniqueKey(softwareTypeName,
				swtSubTypeName);
		SoftwareType softwareType = (SoftwareType) softwareTypeContainer
				.get(stUniqueKey);
		if (toCreate && softwareType == null) {
			softwareType = new SoftwareType(id, softwareTypeName,
					swtSubTypeName, softwareTypeDescription);
			softwareTypeContainer.put(stUniqueKey, softwareType);
		}
		return softwareType;
	}

	private static String getSoftwareTypeUniqueKey(SoftwareType softwareType) {
		return getSoftwareTypeUniqueKey(softwareType.getName(), softwareType
				.getSubTypeName());
	}

	private static String getSoftwareTypeUniqueKey(String softwareTypeName,
			String softwareSubTypeName) {
		if (softwareSubTypeName != null
				&& !softwareSubTypeName.equals(DEFAULT_SUB_TYPE_NAME)) {
			return softwareTypeName + "_" + softwareSubTypeName;
		}

		return softwareTypeName;
	}

	private static void checkArg(Object arg, String argName) {
		if (arg == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3429] The argument '" + argName
							+ "' is null.");
		}
	}

	private SoftwareType(Integer id, String name, String description) {
		this(id, name, DEFAULT_SUB_TYPE_NAME, description);
	}

	private SoftwareType(Integer id, String name, String subTypeName,
			String description) {
		checkArg(id, "id");
		checkArg(name, "name");
		checkArg(subTypeName, "subTypeName");
		checkArg(description, "description");

		this.id = id;
		this.name = StringUtils.intern(name);
		this.subTypeName = StringUtils.intern(subTypeName);
		this.description = StringUtils.intern(description);
		this.toString = DEFAULT_SUB_TYPE_NAME.equals(this.subTypeName) ? this.name
				: StringUtils.intern(this.name + "/" + this.subTypeName);
	}

	private Integer getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public String getSubTypeName() {
		return this.subTypeName;
	}

	public String getDescription() {
		return this.description;
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

		if (!(obj instanceof SoftwareType)) {
			return false;
		}

		SoftwareType other = (SoftwareType) obj;

		if (!this.getName().equals(other.getName())) {
			return false;
		}

		if (!this.getSubTypeName().equals(other.getSubTypeName())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.name = StringUtils.intern(this.name);
		this.subTypeName = StringUtils.intern(this.subTypeName);
		this.description = StringUtils.intern(this.description);
		this.toString = StringUtils.intern(this.toString);
	}
}