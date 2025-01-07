/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.model.impl;

import java.util.Set;

import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.model.Dependency;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.model.SoftwareType;
import com.sap.engine.services.dc.api.model.Version;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-11
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class ModelFactoryImpl implements ModelFactory {
	private static ModelFactoryImpl instance;

	public synchronized static ModelFactory getInstance() {
		if (instance == null) {
			instance = new ModelFactoryImpl();
		}
		return instance;
	}

	public Sda createSda(final String name, final String vendor,
			final String location, final Version version,
			final SoftwareType softwareType, final String componentElementXML,
			final String csnComponent, final Set dependencies,
			final Set dependingFrom, final ScaId scaId) {
		return new SdaImpl(name, vendor, location, version, softwareType,
				componentElementXML, csnComponent, dependencies, dependingFrom,
				scaId);
	}

	public Sca createSca(final String name, final String vendor,
			final String location, final Version version,
			final String componentElementXML, final String csnComponent,
			final Set sdas, final Set originalSdas, Set notDeployedSdas) {
		return new ScaImpl(name, vendor, location, version,
				componentElementXML, csnComponent, sdas, originalSdas, notDeployedSdas);
	}

	public Dependency createDependency(final String name, final String vendor) {
		return new DependencyImpl(name, vendor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.model.ModelFactory#createSoftwareType(
	 * java.lang.String, java.lang.String)
	 */
	public SoftwareType createSoftwareType(final String name,
			final String description) {
		return new SoftwareTypeImpl(name, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.model.ModelFactory#createSoftwareType(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public SoftwareType createSoftwareType(final String name,
			final String subTypeName, final String description) {
		return new SoftwareTypeImpl(name, subTypeName, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.model.ModelFactory#createVersion(java.
	 * lang.String)
	 */
	public Version createVersion(final String versionString) {
		return new VersionImpl(versionString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.model.ModelFactory#createSdaId(java.lang
	 * .String, java.lang.String)
	 */
	public SdaId createSdaId(final String name, final String vendor) {
		return new SdaIdImpl(name, vendor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.model.ModelFactory#createScaId(java.lang
	 * .String, java.lang.String)
	 */
	public ScaId createScaId(final String name, final String vendor) {
		return new ScaIdImpl(name, vendor);
	}

}