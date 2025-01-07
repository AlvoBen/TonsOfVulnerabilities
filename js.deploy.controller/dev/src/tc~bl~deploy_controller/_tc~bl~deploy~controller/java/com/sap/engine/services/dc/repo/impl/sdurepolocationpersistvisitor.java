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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.NoWriteAccessException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.accounting.APredefinedComponent;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.compvers.CompVersException;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.ScaRepoLocation;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.SdaRepoLocation;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.repo.SduRepoLocationVisitor;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * Persists the <code>SduRepoLocation</code>.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class SduRepoLocationPersistVisitor implements SduRepoLocationVisitor {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final static String[] SDA_CFGS_FOR_DELETE = new String[] {
			LocationConstants.DEPS, LocationConstants.DC };

	private final ConfigurationHandler cfgHandler;
	private final Connection conn;

	private Exception exception;

	SduRepoLocationPersistVisitor(ConfigurationHandler cfgHandler,
			Connection conn) {
		this.cfgHandler = cfgHandler;
		this.conn = conn;
	}

	void getException() throws Exception {
		if (this.exception != null) {
			throw this.exception;
		}
	}

	/**
	 * Visits <code>SdaRepoLocation<code>.
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocationVisitor#visit(com.sap.engine.services.dc.repo.SdaRepoLocation)
	 */
	public void visit(SdaRepoLocation location) {
		this.exception = null;
		
		final String tagName = "persist sda repo location ";
		Accounting.beginMeasure( tagName, APredefinedComponent.ConfigurationManager  );
		try {
			final Sda sda = (Sda) location.getSdu();
			boolean isNew = false;

			Configuration sdaCfg = getSduCfg(location);
			if (sdaCfg == null) {
				isNew = true;
				sdaCfg = this.cfgHandler.createSubConfiguration(location
						.getLocation());
			}

			persistSerializedSdu(sdaCfg, location.getSdu());
			persistScaId(sdaCfg, sda);

			// clear all except the sca id, create_date, update_date
			sdaCfg.deleteSubConfigurations(SDA_CFGS_FOR_DELETE);

			initAndPersistSdaProps(sda, sdaCfg);

			if (hasSdaDeps(sda)) {
				persistSdaDeps(sdaCfg, sda);
			}

			// set update_date or create_date
			if (this.conn != null) {
				final String dateLabel = isNew ? SduConstants.CREATED
						: SduConstants.UPDATED;
				CfgUtils.setDBDate(sdaCfg, dateLabel, this.conn,
						new StringBuffer("visit(SdaRepoLocation).SdaId: '")
								.append(sda.getId()).append("'.").toString());
			}
		} catch (Exception e) {
			this.exception = e;
		} finally {
			Accounting.endMeasure( tagName );
		}
	}

	private void persistSerializedSdu(Configuration config, Sdu sdu)
			throws ConfigurationException, Exception {
		CfgUtils.createSerializedObject(sdu, config);
	}

	/**
	 * Visits <code>ScaRepoLocation<code>.
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocationVisitor#visit(com.sap.engine.services.dc.repo.ScaRepoLocation)
	 */
	public void visit(ScaRepoLocation location) {
		this.exception = null;
		
		final String tagName = "persist sca repo location ";
		Accounting.beginMeasure( tagName, APredefinedComponent.ConfigurationManager  );
		try {
			final Sca sca = (Sca) location.getSdu();
			boolean isNew = false;

			Configuration scaCfg = getSduCfg(location);
			if (scaCfg == null) {
				isNew = true;
				scaCfg = this.cfgHandler.createSubConfiguration(location
						.getLocation());
			}


			final Properties sduProps = initSduProps(sca);

			createAndInitPropSheetCfg(scaCfg, LocationConstants.SC, sduProps,
					isNew);

			persistSdaSetInSca(location, scaCfg, isNew);
			
			persistSerializedSdu(scaCfg, location.getSdu());

			if (this.conn != null) {
				// set update_date or create_date
				final String dateLabel = isNew ? SduConstants.CREATED
						: SduConstants.UPDATED;
				CfgUtils.setDBDate(scaCfg, dateLabel, this.conn,
						new StringBuffer("visit(SdaRepoLocation).ScaId: '")
								.append(sca.getId()).append("'.").toString());
			}
		} catch (Exception e) {
			this.exception = e;
		} finally {
			Accounting.endMeasure( tagName );
		}
	}

	private void initAndPersistSdaProps(final Sda sda, Configuration sdaCfg)
			throws ConfigurationException {
		final Properties sduProps = initSduProps(sda);

		// set sda sepecific data
		if (checkPropValues(sda.getClass().toString(), SduConstants.SDA_ST, sda
				.getSoftwareType().getName())) {
			sduProps.setProperty(SduConstants.SDA_ST, sda.getSoftwareType()
					.getName());
		}
		if (checkPropValues(sda.getClass().toString(),
				SduConstants.SDA_ST_SUB_TYPE, sda.getSoftwareType()
						.getSubTypeName())) {
			sduProps.setProperty(SduConstants.SDA_ST_SUB_TYPE, sda
					.getSoftwareType().getSubTypeName());
		}
		if (checkPropValues(sda.getClass().toString(),
				SduConstants.SDA_CSN_COMP, sda.getCsnComponent())) {
			sduProps.setProperty(SduConstants.SDA_CSN_COMP, sda
					.getCsnComponent());
		}

		createAndInitPropSheetCfg(sdaCfg, LocationConstants.DC, sduProps);
	}

	private void persistScaId(Configuration sdaCfg, Sda sda)
			throws RepositoryException, ConfigurationException,
			NullPointerException, SQLException, CompVersException {
		boolean hasScaId = sdaCfg
				.existsSubConfiguration(SduConstants.SDA_SCA_ID);

		final Configuration scaIdCfg;
		if (hasScaId) {
			scaIdCfg = sdaCfg.getSubConfiguration(SduConstants.SDA_SCA_ID);
		} else {
			scaIdCfg = sdaCfg.createSubConfiguration(SduConstants.SDA_SCA_ID,
					Configuration.CONFIG_TYPE_PROPERTYSHEET);
		}
		PropertySheet scaIdPropSheet = scaIdCfg.getPropertySheetInterface();

		ScaId oldScaId = null;
		if (hasScaId) {
			if (scaIdPropSheet != null
					&& scaIdPropSheet
							.existsPropertyEntry(SduConstants.SDU_NAME)
					&& scaIdPropSheet
							.existsPropertyEntry(SduConstants.SDU_VENDOR)) {
				final String name = (String) scaIdPropSheet.getPropertyEntry(
						SduConstants.SDU_NAME).getDefault();
				final String vendor = (String) scaIdPropSheet.getPropertyEntry(
						SduConstants.SDU_VENDOR).getDefault();

				oldScaId = RepositoryComponentsFactory.getInstance()
						.createScaId(name, vendor);
			} else {
				hasScaId = false;
			}
		}

		// The persistence logic is shared between the repo and the
		// temp offline data storage. If the target is not the repo
		// the SCA repo location shall not be touched
		if( !(	sdaCfg.getPath().startsWith(LocationConstants.ROOT_REPO_DC) ||
				sdaCfg.getPath().startsWith(LocationConstants.ROOT_REPO_SC) 
				)){
			
			return;	
		}
		
		if (sda.getScaId() != null && !hasScaId) {
			createScaIdInSda(sda, scaIdPropSheet, this.conn);	
			
		} else if (sda.getScaId() == null && hasScaId) {
			scaIdCfg.deleteConfiguration();
			SduRepoLocationDeleteVisitor.deleteSdaIdFromSca(oldScaId,
					(SdaId) sda.getId(), this.cfgHandler, this.conn);
			
		} else if (sda.getScaId() != null && hasScaId
				&& !sda.getScaId().equals(oldScaId)) {
			scaIdPropSheet.deleteAllPropertyEntries();
			SduRepoLocationDeleteVisitor.deleteSdaIdFromSca(oldScaId,
					(SdaId) sda.getId(), this.cfgHandler, this.conn);
			createScaIdInSda(sda, scaIdPropSheet, this.conn);
		}

	}

	private void createScaIdInSda(Sda sda, PropertySheet scaIdPropSheet, Connection _conn)
			throws ConfigurationException, NameAlreadyExistsException,
			NoWriteAccessException, InconsistentReadException, SQLException {
		final SduRepoLocation scaLocation = SduRepoLocationBuilder.getInstance()
		.build(sda.getScaId());
		Configuration scaCfg = getSduCfg(scaLocation);
		if (scaCfg != null) {
			final Set orgSdaIdsSet = SduRepoLocationLoaderVisitor
			.getOrigSdaIdsForSca(scaCfg);
			if(orgSdaIdsSet.contains(sda.getId())){
				if(location.beDebug()){
					DCLog.traceDebug(location,
							"Add SCA id [{0}] into the configuration structure of SDA [{1}]", 
							new Object[]{scaLocation.getSdu(), sda.getScaId()});
				}
				scaIdPropSheet.createPropertyEntries(initSduIdProps(sda.getScaId()));
				final Configuration sdasCfg = CfgUtils.createSubCfg(
						SduConstants.CFG_PS_SDAs, scaCfg);
				final Map oldSdaIdsCfgMap = SduRepoLocationLoaderVisitor
				.getSdaIdsScaCfgMap(sdasCfg);
				if(!oldSdaIdsCfgMap.containsKey(sda.getId())){
					if(location.beDebug()){
						DCLog.traceDebug(location,
								"Add SDA id [{0}] into the list of the SDAs of SCA [{1}]", 
								new Object[]{sda.getScaId(), scaLocation.getSdu()});
					}
					Properties sdaIdProps = initSduIdProps(sda.getId());
					createAndInitPropSheetCfgWithAvailableName(sdasCfg, sdaIdProps);
					if (_conn != null && scaCfg != null) {
						// modify the SCA update date
						CfgUtils.setDBDate(scaCfg, SduConstants.UPDATED, _conn,
								new StringBuffer("delete SdaId '").append(sda.getId()).append(
										"' from Sca '").append(sda.getScaId()).append("'.")
										.toString());

						// remove .ser file as it is fake old one
						if (scaCfg.existsFile(CfgUtils._SER)) {
							scaCfg.deleteFile(CfgUtils._SER);
						}
					}
					
				}
			}
		}
	}


	private Configuration getSduCfg(SduRepoLocation location)
			throws ConfigurationException, NullPointerException {
		Configuration sduRepoCfg;
		if (location.getConfiguration() != null) {
			sduRepoCfg = location.getConfiguration();
		} else {
			try {
				sduRepoCfg = CfgUtils.openSharedCfgWrite(this.cfgHandler, location
						.getLocation());
			} catch (NameNotFoundException nnfe) {
				sduRepoCfg = null;
			}
		}

		return sduRepoCfg;
	}

	private boolean hasSdaDeps(Sda sda) {
		final Set deps = sda.getDependencies();

		return (deps != null && !deps.isEmpty());
	}

	// Stores all sdas in the current sca as a Set in DB.
	private void persistSdaSetInSca(ScaRepoLocation scaLocation,
			Configuration scaCfg, boolean isNewSca)
			throws NullPointerException, ConfigurationException {
		final Configuration sdasCfg = CfgUtils.createSubCfg(
				SduConstants.CFG_PS_SDAs, scaCfg);

		final Sca sca = (Sca) scaLocation.getSdu();

		// if the SCA will not be stored under the root of Deploy Controller repository 
		// there is no need to resolve all the SC <-> DCs relations
		final Set existingSdaIds;
		if (scaLocation.getLocation().startsWith(
				LocationConstants.ROOT_REPO_SC
						+ LocationConstants.PATH_SEPARATOR)) {
			existingSdaIds = updateScDcRelations(sca, isNewSca, sdasCfg);			
		} else {
			existingSdaIds = sca.getSdaIds(); 
		}

		persistSdaIdsInSca(existingSdaIds, sdasCfg);

		// delete the old orig DCs
		scaCfg
				.deleteSubConfigurations(new String[] { SduConstants.CFG_PS_ORIG_SDAs });
		// store the new orig DCs
		final Configuration origSdasCfg = CfgUtils.createSubCfg(
				SduConstants.CFG_PS_ORIG_SDAs, scaCfg);
		final Set origSdaIds = sca.getOrigSdaIds();
		persistSdaIdsInSca(origSdaIds, origSdasCfg);
	}

	private Set updateScDcRelations(Sca sca, boolean isNewSca,
			final Configuration sdasCfg) throws ConfigurationException {
		final Set newSdaIds = sca.getSdaIds();
		final Set oldToTopLevelSdaIds = new HashSet();
		final Map oldSdaIdsCfgStillExisting = new HashMap();

		if (!isNewSca) {
			// deletes the SDA IDs which are not part of the currently deploying SCA
			// init a Set with them in order to set them later on as a
			// "top level" ones
			// "top level" DC = DC without SC id
			final Map oldSdaIdsCfgMap = SduRepoLocationLoaderVisitor
					.getSdaIdsScaCfgMap(sdasCfg);
			for (Iterator iter = oldSdaIdsCfgMap.entrySet().iterator(); iter
					.hasNext();) {
				final Map.Entry entry = (Map.Entry) iter.next();
				final SdaId oldSdaId = (SdaId) entry.getKey();
				final Configuration oldSdaIdCfg = (Configuration) entry
						.getValue();
				if (!newSdaIds.contains(oldSdaId)) {
					oldSdaIdCfg.deleteConfiguration();
					oldToTopLevelSdaIds.add(oldSdaId);
				} else {
					oldSdaIdsCfgStillExisting.put(oldSdaId, oldSdaIdCfg);
				}
			}
		}

		final Set existingSdaIds = filterExistingSdaIds(newSdaIds,
				oldToTopLevelSdaIds, oldSdaIdsCfgStillExisting, (ScaId) sca
						.getId());
		return existingSdaIds;
	}

	private Set filterExistingSdaIds(Set sdaIds, Set oldToTopLevelSdaIds,
			Map oldSdaIdsCfgStillExisting, ScaId scaId)
			throws ConfigurationException, NullPointerException {
		if (sdaIds.isEmpty() && oldToTopLevelSdaIds.isEmpty()
				&& oldSdaIdsCfgStillExisting.isEmpty()) {
			return sdaIds;
		}

		// deleting the SCA Id from the SDAs which are currently deployed and
		// are not part of the currently deploying SCA
		for (Iterator iter = oldToTopLevelSdaIds.iterator(); iter.hasNext();) {
			final SdaId oldToTopLevelSdaId = (SdaId) iter.next();
			try {
				final SduRepoLocation oldToTopLevelSdaId_SduRepoLocation = SduRepoLocationBuilder
						.getInstance().build(oldToTopLevelSdaId);
				final Configuration topLevelSdaCfg = this.cfgHandler
						.openConfiguration(oldToTopLevelSdaId_SduRepoLocation
								.getLocation(),
								ConfigurationHandler.WRITE_ACCESS, true);
				final Configuration scaIdCfg = topLevelSdaCfg
						.getSubConfiguration(SduConstants.SDA_SCA_ID);				
				PropertySheet scaIdPropSheet = scaIdCfg.getPropertySheetInterface();
				if (scaIdPropSheet != null
						&& scaIdPropSheet
								.existsPropertyEntry(SduConstants.SDU_NAME)
						&& scaIdPropSheet
								.existsPropertyEntry(SduConstants.SDU_VENDOR)) {
					final String name = (String) scaIdPropSheet.getPropertyEntry(
							SduConstants.SDU_NAME).getDefault();
					final String vendor = (String) scaIdPropSheet.getPropertyEntry(
							SduConstants.SDU_VENDOR).getDefault();
					if(scaId.equals(RepositoryComponentsFactory.getInstance()
							.createScaId(name, vendor))){						
						scaIdCfg.deleteConfiguration();
						// remove .ser file as it is a fake old one
						if (topLevelSdaCfg.existsFile(CfgUtils._SER)) {
							topLevelSdaCfg.deleteFile(CfgUtils._SER);
						}
					}
				}
			} catch (NameNotFoundException nnfe) {// $JL-EXC$
				continue;
			}
		}

		final Set existingSdaIds = new HashSet();
		for (Iterator iter = sdaIds.iterator(); iter.hasNext();) {
			final SdaId sdaId = (SdaId) iter.next();
			final Configuration scaSdaIdCfg = (Configuration) oldSdaIdsCfgStillExisting
					.get(sdaId);

			final SduRepoLocation sdaId_SduRepoLocation = SduRepoLocationBuilder
					.getInstance().build(sdaId);
			final Configuration sdaCfg = getSdaSubConfiguration(sdaId_SduRepoLocation.getLocation());
			if (sdaCfg != null) {
				final ScaId _scaId = SduRepoLocationLoaderVisitor
						.loadScaId(sdaCfg);
				if (_scaId != null && _scaId.equals(scaId)) {
					if (!oldSdaIdsCfgStillExisting.containsKey(sdaId)) {
						existingSdaIds.add(sdaId);
					}

				} else {
					iter.remove();
					if (scaSdaIdCfg != null) {
						scaSdaIdCfg.deleteConfiguration();
					}
				}

			} else {
				iter.remove();
				if (scaSdaIdCfg != null) {
					scaSdaIdCfg.deleteConfiguration();
				}
			}
		}

		return existingSdaIds;
	}

	private void persistSdaIdsInSca(Set sdaIds, Configuration sdaIdsCfg)
			throws ConfigurationException {
		final Iterator sdasIter = sdaIds.iterator();
		SdaId sdaId;
		Properties sdaIdProps;

		while (sdasIter.hasNext()) {
			sdaId = (SdaId) sdasIter.next();
			sdaIdProps = initSduIdProps(sdaId);

			createAndInitPropSheetCfgWithAvailableName(sdaIdsCfg, sdaIdProps);
		}
	}

	private void persistSdaDeps(Configuration sdaCfg, Sda sda)
			throws NullPointerException, ConfigurationException {
		final Configuration depsCfg = CfgUtils.createSubCfg(
				LocationConstants.DEPS, sdaCfg);
		final Configuration dtCfg = CfgUtils.createSubCfg(LocationConstants.DT,
				depsCfg);
		final Configuration toCfg = CfgUtils.createSubCfg(LocationConstants.TO,
				dtCfg);
		final Set depsSet = sda.getDependencies();

		if (depsSet != null && depsSet.size() > 0) {
			final Iterator depsIter = depsSet.iterator();
			Dependency dep = null;
			Properties depProps = null;
			int counter = 1;

			while (depsIter.hasNext()) {
				dep = (Dependency) depsIter.next();
				depProps = initDepProps(dep);
				createAndInitPropSheetCfg(toCfg, counter + "", depProps);
				counter++;
			}
		}
	}

	private Properties initDepProps(Dependency dep) {
		final Properties depProps = new Properties();

		if (checkPropValues(dep.getClass().toString(), SduConstants.SDU_NAME,
				dep.getName())) {
			depProps.setProperty(SduConstants.SDU_NAME, dep.getName());
		}
		if (checkPropValues(dep.getClass().toString(), SduConstants.SDU_VENDOR,
				dep.getVendor())) {
			depProps.setProperty(SduConstants.SDU_VENDOR, dep.getVendor());
		}

		return depProps;
	}

	private Properties initSduIdProps(SduId sduId) {
		final Properties sduIdProps = new Properties();
		if (checkPropValues(sduId.getClass().toString(), SduConstants.SDU_NAME,
				sduId.getName())) {
			sduIdProps.setProperty(SduConstants.SDU_NAME, sduId.getName());
		}
		if (checkPropValues(sduId.getClass().toString(),
				SduConstants.SDU_VENDOR, sduId.getVendor())) {
			sduIdProps.setProperty(SduConstants.SDU_VENDOR, sduId.getVendor());
		}

		return sduIdProps;
	}

	private Properties initSduProps(Sdu sdu) {
		final Properties sduProps = new Properties();

		if (checkPropValues(sdu.getClass().toString(), SduConstants.SDU_NAME,
				sdu.getName())) {
			sduProps.setProperty(SduConstants.SDU_NAME, sdu.getName());
		}
		if (checkPropValues(sdu.getClass().toString(), SduConstants.SDU_VENDOR,
				sdu.getVendor())) {
			sduProps.setProperty(SduConstants.SDU_VENDOR, sdu.getVendor());
		}
		if (checkPropValues(sdu.getClass().toString(),
				SduConstants.SDU_LOCATION, sdu.getLocation())) {
			sduProps.setProperty(SduConstants.SDU_LOCATION, sdu.getLocation());
		}
		if (checkPropValues(sdu.getClass().toString(),
				SduConstants.SDU_VERSION, sdu.getVersion().toString())) {
			sduProps.setProperty(SduConstants.SDU_VERSION, sdu.getVersion()
					.toString());
		}
		if (checkPropValues(sdu.getClass().toString(),
				SduConstants.SDU_COMP_ELEM, sdu.getComponentElementXML())) {
			sduProps.setProperty(SduConstants.SDU_COMP_ELEM, sdu
					.getComponentElementXML());
		}
		if (checkPropValues(sdu.getClass().toString(), SduConstants.SDU_CRC,
				sdu.getCrc())) {
			sduProps.setProperty(SduConstants.SDU_CRC, sdu.getCrc());
		}

		return sduProps;
	}

	private boolean checkPropValues(String code, String name, String value)
			throws NullPointerException {
		if (value == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003357 The value of the " + name
							+ " property of " + code + " is " + value + " .");
		}
		return true;
	}

	private void createAndInitPropSheetCfg(Configuration cfg,
			String subPropSheet, Properties sduProps)
			throws ConfigurationException {
		createAndInitPropSheetCfg(cfg, subPropSheet, sduProps, false);
	}

	private void createAndInitPropSheetCfg(Configuration cfg,
			String subPropSheet, Properties sduProps, boolean isNew)
			throws ConfigurationException {
		Configuration propsCfg = CfgUtils.createSubCfg(subPropSheet, cfg,
				Configuration.CONFIG_TYPE_PROPERTYSHEET);
		PropertySheet propSheet = propsCfg.getPropertySheetInterface();
		if (!isNew) {
			propSheet.deleteAllPropertyEntries();
		}
		propSheet.createPropertyEntries(sduProps);
	}

	private void createAndInitPropSheetCfgWithAvailableName(Configuration cfg,
			Properties sduProps) throws ConfigurationException {
		List lAllSubConfigurationNames = Arrays.asList(cfg
				.getAllSubConfigurationNames());

		int count = 0;
		do {
			count++;
		} while (lAllSubConfigurationNames.contains(count + ""));

		Configuration propsCfg = CfgUtils.createSubCfg(count + "", cfg,
				Configuration.CONFIG_TYPE_PROPERTYSHEET);

		PropertySheet propSheet = propsCfg.getPropertySheetInterface();

		propSheet.createPropertyEntries(sduProps);
	}
	
	private Configuration getSdaSubConfiguration(String fullCfgPath)
			throws ConfigurationLockedException, ConfigurationException {
		try {
			return cfgHandler.openConfiguration(fullCfgPath, 
					ConfigurationHandler.READ_ACCESS, true);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	
}
