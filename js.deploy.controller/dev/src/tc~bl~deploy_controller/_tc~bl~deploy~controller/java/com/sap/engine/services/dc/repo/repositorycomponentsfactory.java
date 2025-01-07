package com.sap.engine.services.dc.repo;

import java.util.Collection;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */

public abstract class RepositoryComponentsFactory {
	private static RepositoryComponentsFactory INSTANCE;
	// TODO: Could be read from a global deploy service configurator
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.repo.impl.RepositoryComponentsFactoryImpl";

	protected RepositoryComponentsFactory() {
	}

	public static synchronized RepositoryComponentsFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static RepositoryComponentsFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (RepositoryComponentsFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003367 An error occurred while creating an instance of "
					+ "class RepositoryComponentsFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract DCReference createDCReference(String name, String vendor);

	public abstract DCReference createDCReference(String name, String vendor,
			String scAlias);

	public abstract Dependency createDependency(String name, String vendor);

	public abstract Dependency createDependency(DCReference dcRef);

	public abstract Version createVersion(String versionAsString);

	public abstract VersionHelper createVersionHelper();

	public abstract SdaId createSdaId(String name, String vendor);

	public abstract SdaId createSdaId(String vendorAndName);

	public abstract ScaId createScaId(String name, String vendor);

	public abstract Sda createSda(String name, String vendor, String location,
			Version version, String componentElementXML, String softwareType,
			String softwareSubType, String csnComponent);

	public abstract Sda createSda(String name, String vendor, String location,
			Version version, String componentElementXML, String softwareType,
			String softwareSubType, String csnComponent, Set dependencies);

	public abstract Sda createSda(String name, String vendor, String location,
			Version version, String componentElementXML, String softwareType,
			String softwareSubType, String csnComponent, Set dependencies,
			ScaId parentScaId);

	public abstract Sca createSca(String name, String vendor, String location,
			Version version, String componentElementXML);

	public abstract Sca createSca(String name, String vendor, String location,
			Version version, String componentElementXML, Set sdas, Set origSdas);

	public abstract SduLocation createSdaLocation(Sda sda, String location);

	public abstract SduLocation createScaLocation(Sca sca, String location,
			Collection groupedLocations);

	public abstract SduRepoLocation createSduRepoLocation(Sdu sdu);

	public abstract SduRepoLocation createSduRepoLocation(Configuration cfg);

	public abstract SduRepoLocation createSduRepoLocation(Sdu sdu,
			Configuration cfg);

	public abstract SduFileStorageLocation createSduStorageLocation(SduId sduId);

	public abstract ConfigurationChangedListener createRepoDCCfgListener();

	public abstract ConfigurationChangedListener createRepoSCCfgListener();

	public abstract CfgBuilder getCfgBuilder();

	public abstract DependencyCycle createDependencyCycle(Collection items);

}
