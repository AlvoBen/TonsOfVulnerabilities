package com.sap.engine.services.dc.repo.impl;

import java.io.IOException;
import java.util.Map;

import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.Version;
import com.sap.engine.services.dc.util.StringUtils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class SduImpl implements Sdu {

	private static final long serialVersionUID = -3461649378261604748L;

	private final SduId sduId;
	private String location;
	private final Version version;
	private final String componentElementXML;
	private final Map properties;// $JL-SER$
	private String csnComponent;

	private final int hashCode;
	private final String toString;

	private String crc = "";

	// for AP6.5 backward compatibility..
	private String name;
	private String vendor;

	protected SduImpl(final SduId sduId, final String location,
			final Version version, final String csnComponent,
			final String componentElementXML, final Map properties) {
		final String sduImplClassName = "SduImpl";
		paramCheck(location, sduImplClassName, "location");
		paramCheck(version, sduImplClassName, "version");
		paramCheck(componentElementXML, sduImplClassName, "componentElementXML");
		// there is no need to check the properties argument as at the moment it
		// is
		// not used therefore we do not need to create an empty map for each
		// SDU.
		// paramCheck(properties, "SduImpl", "properties");

		this.sduId = sduId;
		this.location = StringUtils.intern(location);
		this.version = version;
		this.csnComponent = csnComponent == null ? StringUtils.intern("")
				: StringUtils.intern(csnComponent);
		this.componentElementXML = componentElementXML;

		this.hashCode = this.generateHashCode();
		this.toString = this.generateToString();
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sdu#getName()
	 */
	public String getName() {
		return this.sduId == null ? name : this.sduId.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sdu#getVendor()
	 */
	public String getVendor() {
		return this.sduId == null ? vendor : this.sduId.getVendor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sdu#getLocation()
	 */
	public String getLocation() {
		return this.location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sdu#getVersion()
	 */
	public Version getVersion() {
		return this.version;
	}

	public String getCsnComponent() {
		return this.csnComponent;
	}

	public String getComponentElementXML() {
		return this.componentElementXML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sdu#getProperties()
	 */
	public Map getProperties() {
		return this.properties;
	}

	public void setCrc(String aCrc) {
		final String emptyString = "";
		if (aCrc == null || emptyString.equals(aCrc)) {
			this.crc = StringUtils.intern(emptyString);
		} else {
			this.crc = aCrc;
		}
	}

	public String getCrc() {
		return this.crc;
	}

	final public int hashCode() {
		return this.hashCode;
	} // hashCode()

	final public boolean equals(Object other) {

		if (other == null) {
			return false;
		}

		if (this == other) {
			return true;
		}

		if (this.getClass() != other.getClass()) {
			return false;
		}

		final Sdu otherSdu = (Sdu) other;

		if (!getId().equals(otherSdu.getId())) {
			return false;
		}

		if (!isComparable(otherSdu)) {
			return false;
		}

		if (!isSameSdu(otherSdu)) {
			return false;
		}

		return true;
	}

	public String toString() {
		return this.toString;
	}

	final protected void paramCheck(Object obj, String methodName,
			String paramName) {
		if (obj != null) {
			return;
		}

		final String errText = "[ERROR CODE DPL.DC.3426] "
				+ this.getClass().getName() + "." + methodName
				+ "(...): parameter '" + paramName + "' is null.";
		throw new NullPointerException(errText);
	}

	private int generateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getName().hashCode();
		result = result * multiplier + getVendor().hashCode();
		result = result * multiplier + getLocation().hashCode();
		result = result * multiplier + getVersion().hashCode();

		return result;
	}

	private String generateToString() {
		return "name: '" + getName() + "', vendor: '" + getVendor()
				+ "', location: '" + getLocation() + "', version: '"
				+ getVersion() + "', crc: '" + getCrc() + "'.";
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.location = StringUtils.intern(this.location);

		final String emptyString = "";
		this.crc = (this.crc == null ? StringUtils.intern(emptyString)
				: StringUtils.intern(this.crc));

		this.csnComponent = (this.csnComponent == null ? StringUtils
				.intern(emptyString) : StringUtils.intern(this.csnComponent));
	}
}
