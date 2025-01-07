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
package com.sap.engine.services.dc.repo.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.ScaRepoLocation;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaRepoLocation;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduRepoLocationVisitor;
import com.sap.engine.services.dc.repo.Version;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.ValidatorUtils;
import com.sap.tc.logging.Location;

/**
 * Loads the <code>SduRepoLocation</code>.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class SduRepoLocationLoaderVisitor implements SduRepoLocationVisitor {
	
	private Location location = getLocation(this.getClass());

	private final ConfigurationHandler cfgHandler;

	private ConfigurationException configurationException;

	SduRepoLocationLoaderVisitor(ConfigurationHandler cfgHandler) {
		this.cfgHandler = cfgHandler;
	}

	public void getException() throws ConfigurationException {
		if (configurationException != null) {
			throw configurationException;
		}
	}

	/**
	 * Visits <code>SdaRepoLocation<code>.
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocationVisitor#visit(com.sap.engine.services.dc.repo.SdaRepoLocation)
	 */
	public void visit(SdaRepoLocation location) {
		init();

		try {
			final Configuration sduCfg;
			if (location.getConfiguration() != null) {
				sduCfg = location.getConfiguration();
			} else {
				try {
					sduCfg = CfgUtils.openCfgRead(cfgHandler, location
							.getLocation());
				} catch (NameNotFoundException nnfe) {
					// there is no such stored <code>Sdu</code>, so returns
					// NULL.
					location.setSdu(null);
					return;
				}
			}

			Sdu deserSdu = getDeserializedSdu(sduCfg);

			if (deserSdu != null) {
				location.setSdu(deserSdu);
				return;
			}

			try {
				final PropertySheet propSheet = getPropertyEntries(sduCfg,
						LocationConstants.DC);

				final String softwareTypeSubType = getCfgProperty(propSheet,
						SduConstants.SDA_ST_SUB_TYPE);
				final String csnComponent = getCfgProperty(propSheet,
						SduConstants.SDA_CSN_COMP);
				final Version version = RepositoryComponentsFactory
						.getInstance().createVersion(
								propSheet.getPropertyEntry(
										SduConstants.SDU_VERSION).getDefault()
										.toString());

				Sda sda = RepositoryComponentsFactory.getInstance().createSda(
						propSheet.getPropertyEntry(SduConstants.SDU_NAME)
								.getDefault().toString(),
						propSheet.getPropertyEntry(SduConstants.SDU_VENDOR)
								.getDefault().toString(),
						propSheet.getPropertyEntry(SduConstants.SDU_LOCATION)
								.getDefault().toString(),
						version,
						propSheet.getPropertyEntry(SduConstants.SDU_COMP_ELEM)
								.getDefault().toString(),
						propSheet.getPropertyEntry(SduConstants.SDA_ST)
								.getDefault().toString(), softwareTypeSubType,
						csnComponent, getSdaDeps(sduCfg), loadScaId(sduCfg));

				sda.setCrc(getCfgProperty(propSheet, SduConstants.SDU_CRC));

				location.setSdu(sda);
			} finally {
				if (sduCfg != null && cfgHandler != null) {
					cfgHandler.closeConfiguration(sduCfg);
				}
			}
		} catch (ConfigurationException ce) {
			configurationException = ce;
		}
	}

	private Sdu getDeserializedSdu(Configuration sduCfg)
			throws ConfigurationException {
		try {
			return (Sdu) CfgUtils.getDeserializedObject(sduCfg);
		} catch (Exception e) {
			if (location.beDebug()) {
				traceDebug(
						location,
						"Exception while deserializing object: [{0}]",
						new Object[] { e.getLocalizedMessage() });
			}
			return null;
		}

	}

	/**
	 * Visits <code>ScaRepoLocation<code>.
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocationVisitor#visit(com.sap.engine.services.dc.repo.ScaRepoLocation)
	 */
	public void visit(ScaRepoLocation location) {
		init();

		try {
			final Configuration sduCfg;
			if (location.getConfiguration() != null) {
				sduCfg = location.getConfiguration();
			} else {
				try {
					sduCfg = CfgUtils.openCfgRead(cfgHandler, location
							.getLocation());
				} catch (NameNotFoundException nnfe) {
					// there is no such stored <code>Sdu</code>, so returns
					// NULL.
					location.setSdu(null);
					return;
				}
			}

			Sdu deserSdu = getDeserializedSdu(sduCfg);

			if (deserSdu != null) {
				location.setSdu(deserSdu);
				return;
			}

			try {
				final PropertySheet propSheet = getPropertyEntries(sduCfg,
						LocationConstants.SC);
				final Version version = RepositoryComponentsFactory
						.getInstance().createVersion(
								propSheet.getPropertyEntry(
										SduConstants.SDU_VERSION).getDefault()
										.toString());

				final Set sdaIds = getSdaIdsForSca(sduCfg);
				final Set origSdaIds = getOrigSdaIdsForSca(sduCfg);
				if (origSdaIds.isEmpty()) {
					origSdaIds.addAll(sdaIds);
				}

				final Sca sca = RepositoryComponentsFactory.getInstance()
						.createSca(
								propSheet.getPropertyEntry(
										SduConstants.SDU_NAME).getDefault()
										.toString(),
								propSheet.getPropertyEntry(
										SduConstants.SDU_VENDOR).getDefault()
										.toString(),
								propSheet.getPropertyEntry(
										SduConstants.SDU_LOCATION).getDefault()
										.toString(),
								version,
								propSheet.getPropertyEntry(
										SduConstants.SDU_COMP_ELEM)
										.getDefault().toString(), sdaIds,
								origSdaIds);

				sca.setCrc(getCfgProperty(propSheet, SduConstants.SDU_CRC));

				location.setSdu(sca);
			} finally {
				if (sduCfg != null && cfgHandler != null) {
					cfgHandler.closeConfiguration(sduCfg);
				}
			}
		} catch (ConfigurationException ce) {
			configurationException = ce;
		}
	}

	/**
	 * @param sdaCfg
	 * @return <code>ScaId</code> which specifies the SC to which the
	 *         <code>sdaCfg</code> related DC belongs to. Returns
	 *         <code>null</code> if the DC does not belons to any SC i. e. it is
	 *         a top level one.
	 * @throws ConfigurationException
	 */
	static ScaId loadScaId(Configuration sdaCfg) throws ConfigurationException {
		final boolean hasScaId = sdaCfg
				.existsSubConfiguration(SduConstants.SDA_SCA_ID);

		if (hasScaId) {
			final Configuration scaIdCfg = sdaCfg
					.getSubConfiguration(SduConstants.SDA_SCA_ID);
			final PropertySheet scaIdPropSheet = scaIdCfg
					.getPropertySheetInterface();
			if (scaIdPropSheet.existsPropertyEntry(SduConstants.SDU_NAME)
					&& scaIdPropSheet
							.existsPropertyEntry(SduConstants.SDU_VENDOR)) {
				final String name = (String) scaIdPropSheet.getPropertyEntry(
						SduConstants.SDU_NAME).getDefault();
				final String vendor = (String) scaIdPropSheet.getPropertyEntry(
						SduConstants.SDU_VENDOR).getDefault();

				return RepositoryComponentsFactory.getInstance().createScaId(
						name, vendor);
			}
		}

		return null;
	}

	static Map getSdaIdsScaCfgMap(Configuration sdaIdsCfg)
			throws ConfigurationException {
		final String[] subNames = sdaIdsCfg.getAllSubConfigurationNames();
		Configuration sdaIdCfg;
		PropertySheet sdaIdPropSheet;
		String name;
		String vendor;
		final Map resSdaIdsCfgMap = new HashMap();

		ValidatorUtils.validate(subNames);
		for (int i = 0; i < subNames.length; i++) {
			sdaIdCfg = sdaIdsCfg.getSubConfiguration(subNames[i]);
			sdaIdPropSheet = sdaIdCfg.getPropertySheetInterface();
			name = (String) sdaIdPropSheet.getPropertyEntry(
					SduConstants.SDU_NAME).getDefault();
			vendor = (String) sdaIdPropSheet.getPropertyEntry(
					SduConstants.SDU_VENDOR).getDefault();

			resSdaIdsCfgMap.put(RepositoryComponentsFactory.getInstance()
					.createSdaId(name, vendor), sdaIdCfg);
		}

		return resSdaIdsCfgMap;
	}

	private String getCfgProperty(final PropertySheet propSheet,
			final String propName) throws ConfigurationException,
			NameNotFoundException {
		final String defaultCfgPropValue = null;
		try {
			final PropertyEntry propEntry = propSheet
					.getPropertyEntry(propName);
			if (propEntry != null) {
				return propEntry.getDefault().toString();
			}

			return defaultCfgPropValue;
		} catch (NameNotFoundException nnfe) {
			return defaultCfgPropValue;
		}
	}

	private PropertySheet getPropertyEntries(Configuration sduCfg,
			String propSheetName) throws ConfigurationException {
		final Configuration sduPropCfg = sduCfg
				.getSubConfiguration(propSheetName);
		final PropertySheet propSheet = sduPropCfg.getPropertySheetInterface();
		return propSheet;
	}

	private Set getSdaIdsForSca(Configuration sduCfg)
			throws ConfigurationException {
		Configuration sdaIdsCfg = sduCfg
				.getSubConfiguration(SduConstants.CFG_PS_SDAs);
		String subNames[] = sdaIdsCfg.getAllSubConfigurationNames();
		Configuration sdaIdCfg;
		PropertySheet sdaIdPropSheet;
		String name;
		String vendor;
		final Set resSdaIds = new HashSet();

		ValidatorUtils.validate(subNames);
		for (int i = 0; i < subNames.length; i++) {
			sdaIdCfg = sdaIdsCfg.getSubConfiguration(subNames[i]);
			sdaIdPropSheet = sdaIdCfg.getPropertySheetInterface();
			name = (String) sdaIdPropSheet.getPropertyEntry(
					SduConstants.SDU_NAME).getDefault();
			vendor = (String) sdaIdPropSheet.getPropertyEntry(
					SduConstants.SDU_VENDOR).getDefault();
			resSdaIds.add(RepositoryComponentsFactory.getInstance()
					.createSdaId(name, vendor));
		}

		return resSdaIds;
	}

	static Set getOrigSdaIdsForSca(Configuration sduCfg)
			throws ConfigurationException {
		final Set resSdaIds = new HashSet();

		if (sduCfg.existsSubConfiguration(SduConstants.CFG_PS_ORIG_SDAs)) {
			Configuration sdaIdsCfg = sduCfg
					.getSubConfiguration(SduConstants.CFG_PS_ORIG_SDAs);
			String subNames[] = sdaIdsCfg.getAllSubConfigurationNames();
			Configuration sdaIdCfg;
			PropertySheet sdaIdPropSheet;
			String name;
			String vendor;

			ValidatorUtils.validate(subNames);
			for (int i = 0; i < subNames.length; i++) {
				sdaIdCfg = sdaIdsCfg.getSubConfiguration(subNames[i]);
				sdaIdPropSheet = sdaIdCfg.getPropertySheetInterface();
				name = (String) sdaIdPropSheet.getPropertyEntry(
						SduConstants.SDU_NAME).getDefault();
				vendor = (String) sdaIdPropSheet.getPropertyEntry(
						SduConstants.SDU_VENDOR).getDefault();
				resSdaIds.add(RepositoryComponentsFactory.getInstance()
						.createSdaId(name, vendor));
			}
		}

		return resSdaIds;
	}

	private Set getSdaDeps(Configuration sduCfg) throws ConfigurationException {
		if (!sduCfg.existsSubConfiguration(LocationConstants.DEPS)) {
			return new HashSet();
		}
		final Configuration depsCfg = sduCfg
				.getSubConfiguration(LocationConstants.DEPS);
		if (!depsCfg.existsSubConfiguration(LocationConstants.DT)) {
			return new HashSet();
		}
		final Configuration dtCfg = depsCfg
				.getSubConfiguration(LocationConstants.DT);
		if (!dtCfg.existsSubConfiguration(LocationConstants.TO)) {
			return new HashSet();
		}
		final Configuration toCfg = dtCfg
				.getSubConfiguration(LocationConstants.TO);

		String subNames[] = toCfg.getAllSubConfigurationNames();
		Set deps = new HashSet();
		if (subNames != null) {
			Configuration currCfg = null;
			PropertySheet propSheet = null;
			PropertyEntry propEntry = null;
			String name = null;
			String vendor = null;
			Dependency currDep = null;
			for (int i = 0; i < subNames.length; i++) {
				currCfg = toCfg.getSubConfiguration(subNames[i]);
				if (currCfg.getConfigurationType() == Configuration.CONFIG_TYPE_PROPERTYSHEET) {
					propSheet = currCfg.getPropertySheetInterface();

					propEntry = propSheet
							.getPropertyEntry(SduConstants.SDU_NAME);
					name = (String) propEntry.getDefault();

					propEntry = propSheet
							.getPropertyEntry(SduConstants.SDU_VENDOR);
					vendor = (String) propEntry.getDefault();

					currDep = RepositoryComponentsFactory.getInstance()
							.createDependency(name, vendor);
					deps.add(currDep);
				}
			}
		}
		return deps;
	}

	private void init() {
		configurationException = null;
		;
	}
}
