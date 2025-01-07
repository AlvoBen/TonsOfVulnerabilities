package com.sap.engine.services.dc.repo;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-9
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface VersionHelper {

	/**
	 * Returns whether the first <code>Version</code> is lower than the
	 * specified other Version <code>Version</code>.
	 * 
	 * @param version
	 *            the version to be compared with the otherVersion
	 *            <code>Version</code>
	 * @param otherVersion
	 *            the version to be compared with the first <code>Version</code>
	 * @return <code>true</code> if this <code>Version</code> is lower than
	 *         <code>otherVersion</code>; <code>false</code> otherwise.
	 * @throws NullPointerException
	 *             if <code>version</code> is <code>null</code> or
	 *             <code>otherVersion</code> is <code>null</code>.
	 */
	public boolean isLower(Version version, Version otherVersion);

	/**
	 * Returns whether the first <code>Version</code> is equivalent to the
	 * specified <code>otherVersion</code>.
	 * 
	 * @param version
	 *            the version to be compared with the other <code>Version</code>
	 * @param otherVersion
	 *            the version to be compared with the first <code>Version</code>
	 * @return <code>true</code> if this <code>Version</code> is equivalent to
	 *         <code>otherVersion</code>; <code>false</code> otherwise.
	 * @throws NullPointerException
	 *             if <code>version</code> is <code>null</code> or
	 *             <code>otherVersion</code> is <code>null</code>.
	 */
	public boolean isEquivalent(Version version, Version otherVersion);

	/**
	 * A convenience method that returns whether the first <code>Version</code>
	 * is equivalent to or lower than the specified otherVersion
	 * <code>Version</code>.
	 * 
	 * @param version
	 *            the version to be compared with the other <code>Version</code>
	 * @param otherVersion
	 *            the version to be compared with the first <code>Version</code>
	 * @return <code>true</code> if the first <code>Version</code> is lower than
	 *         or equal to <code>otherVersion</code>; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if <code>version</code> is <code>null</code> or
	 *             <code>otherVersion</code> is <code>null</code>.
	 */
	public boolean isLowerOrEquivalent(Version version, Version otherVersion);

	/**
	 * @param version
	 *            the <code>Version</code>
	 * @return <code>String</code> representation of the specified
	 *         <code>Version</code>
	 */
	public String getVersionAsString(Version version);

	/**
	 * Checks whether the specified version as a <code>String</code> is valid.
	 * 
	 * @param versionString
	 *            <code>String</code> repsresentation of the version
	 * @return <code>true</code> if the specified verison is valid and
	 *         <code>false</code> otherwise.
	 */
	public boolean isValidVersionString(String versionString);

}
