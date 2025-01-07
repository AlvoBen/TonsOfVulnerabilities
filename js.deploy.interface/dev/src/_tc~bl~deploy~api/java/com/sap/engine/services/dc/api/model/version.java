package com.sap.engine.services.dc.api.model;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-1-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface Version {

	/**
	 * @return <code>String</code> representation of the this
	 *         <code>Version</code>
	 */
	public String getVersionAsString();

	/**
	 * Returns whether the specified <code>Object</code> is equal to this
	 * <code>Version</code>. The specified <code>Object</code> is considered
	 * equal to this <code>Version</code>, if and only if it is an instance of
	 * <code>Version</code> (different from <code>null</code>) that consists of
	 * the same sequence of numerical strings as this <code>Version</code>.
	 * 
	 * @param other
	 *            the specified <code>Object</code>.
	 * @return <code>true</code> if <code>other</code> is equal to this
	 *         <code>Version</code>; <code>false</code> otherwise.
	 */
	public boolean equals(Object other);

	/**
	 * Calculates a hash code of this <code>Version</code> such that two
	 * instances of <code>Version</code> that are equal return the same hash
	 * code.
	 * 
	 * @return a hash code
	 */
	public int hashCode();

	/**
	 * Gets a representation of this <code>Version</code> as version string.
	 * When invoked with the returned version string, the method
	 * <code>VersionFactory.create(versionString)</code> returns a
	 * <code>Version</code> equal to this <code>Version</code>. More formally,
	 * for each instance <code>v</code> of <code>Version</code>, <br>
	 * <code>v.equals(VersionFactory.create(v.toString())) == true</code>.
	 * 
	 * @return a representation of this <code>Version</code> as version string.
	 */
	public String toString();

}
