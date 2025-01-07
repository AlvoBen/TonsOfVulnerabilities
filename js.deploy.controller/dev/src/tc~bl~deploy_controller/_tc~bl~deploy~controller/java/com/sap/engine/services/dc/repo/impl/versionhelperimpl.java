package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.Version;
import com.sap.engine.services.dc.repo.VersionHelper;

import com.sap.sl.util.sduread.api.SduReaderFactory;

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
final class VersionHelperImpl implements VersionHelper {

	private static final VersionHelperImpl INSTANCE = new VersionHelperImpl();

	private final com.sap.sl.util.sduread.api.VersionFactoryIF versionFactory;

	static final VersionHelperImpl getInstance() {
		return INSTANCE;
	}

	private VersionHelperImpl() {
		this.versionFactory = SduReaderFactory.getInstance()
				.getVersionFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.VersionHelper#isLower(com.sap.engine.
	 * services.dc.repo.Version, com.sap.engine.services.dc.repo.Version)
	 */
	public boolean isLower(Version version, Version otherVersion) {
		com.sap.sl.util.sduread.api.Version slVersion = this.versionFactory
				.createVersion(version.getVersionAsString());
		com.sap.sl.util.sduread.api.Version slOtherVersion = this.versionFactory
				.createVersion(otherVersion.getVersionAsString());

		return slVersion.isLower(slOtherVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.VersionHelper#isEquivalent(com.sap.engine
	 * .services.dc.repo.Version, com.sap.engine.services.dc.repo.Version)
	 */
	public boolean isEquivalent(Version version, Version otherVersion) {
		com.sap.sl.util.sduread.api.Version slVersion = this.versionFactory
				.createVersion(version.getVersionAsString());
		com.sap.sl.util.sduread.api.Version slOtherVersion = this.versionFactory
				.createVersion(otherVersion.getVersionAsString());

		return slVersion.isEquivalent(slOtherVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.VersionHelper#isLowerOrEquivalent(com
	 * .sap.engine.services.dc.repo.Version,
	 * com.sap.engine.services.dc.repo.Version)
	 */
	public boolean isLowerOrEquivalent(Version version, Version otherVersion) {
		com.sap.sl.util.sduread.api.Version slVersion = this.versionFactory
				.createVersion(version.getVersionAsString());
		com.sap.sl.util.sduread.api.Version slOtherVersion = this.versionFactory
				.createVersion(otherVersion.getVersionAsString());

		return slVersion.isLowerOrEquivalent(slOtherVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.VersionHelper#getVersionAsString(com.
	 * sap.engine.services.dc.repo.Version)
	 */
	public String getVersionAsString(Version version) {
		return this.versionFactory.createVersion(version.getVersionAsString())
				.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.VersionHelper#isValidVersionString(java
	 * .lang.String)
	 */
	public boolean isValidVersionString(String versionString) {
		if (versionString == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003358 The versionString could not be null!");
		}

		return this.versionFactory.isValidVersionString(versionString);
	}

}
