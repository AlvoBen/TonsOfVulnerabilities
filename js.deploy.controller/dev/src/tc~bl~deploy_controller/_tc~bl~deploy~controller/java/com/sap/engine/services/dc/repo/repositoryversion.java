/*
 * Created on 2005-11-2
 *
 */
package com.sap.engine.services.dc.repo;

/**
 * @author ivan-mih
 *
 */

/**
 * Represents the version of the DC repository structure. When modifying the
 * repository structure, add a new instance and adjust the <code>CURRENT</code>
 * instance.
 * 
 * @author Christian Gabrisch 31.03.2003
 */
public final class RepositoryVersion {
	private final int version;

	private RepositoryVersion(int version) {
		this.version = version;
	}

	public final static RepositoryVersion V1 = new RepositoryVersion(1);

	/**
	 * The current version of the SDM repository structure.
	 */
	public final static RepositoryVersion CURRENT = V1;

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (other.getClass().equals(this.getClass()) == false) {
			return false;
		}

		RepositoryVersion otherVersion = (RepositoryVersion) other;

		return (this.version == otherVersion.version);
	}

	public int hashCode() {
		return version;
	}

	/**
	 * Checks whether this <code>RepositoryVersion</code> is lower than the
	 * specified <code>RepositoryVersion</code>.
	 * 
	 * @param otherVersion
	 *            the <code>RepositoryVersion</code> to be compared
	 * @return <code>true</code> if this <code>RepositoryVersion</code> is lower
	 *         than <code>otherVersion</code>, <code>false</code> otherwise
	 */
	public boolean isLower(RepositoryVersion otherVersion) {
		return this.version < otherVersion.version;
	}

	/**
	 * Returns a <code>String</code> representation of this
	 * <code>RepositoryVersion</code>.
	 */
	public String toString() {
		return Integer.toString(version);
	}

	/**
	 * Returns a <code>RepositoryVersion</code> defined by the specified version
	 * string.
	 * 
	 * @return a <code>RepositoryVersion</code>, if
	 *         <code>repoVersAsString</code> denotes an integer,
	 *         <code>null</code> otherwise.
	 */
	public static RepositoryVersion get(int repoVersAsString) {
		try {
			return new RepositoryVersion(repoVersAsString);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}