package com.sap.engine.services.dc.repo.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.dc.compvers.CompVersException;
import com.sap.engine.services.dc.compvers.CompVersFactory;
import com.sap.engine.services.dc.compvers.CompVersManager;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.ScaRepoLocation;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.SdaRepoLocation;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduFileStorageLocation;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.repo.SduRepoLocationVisitor;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class SduRepoLocationDeleteVisitor implements SduRepoLocationVisitor {
	
	private static Location location = DCLog.getLocation(SduRepoLocationDeleteVisitor.class);

	private final ConfigurationHandler cfgHandler;
	private final Connection conn;

	private Exception exception;

	SduRepoLocationDeleteVisitor(ConfigurationHandler cfgHandler,
			Connection conn) {
		this.cfgHandler = cfgHandler;
		this.conn = conn;
	}

	static void deleteSdaIdFromSca(ScaId scaId, SdaId sdaId,
			ConfigurationHandler _cfgHandler, Connection _conn)
			throws SQLException, ConfigurationException, RepositoryException,
			NullPointerException, CompVersException {
		final SduRepoLocation sdu_repo_location = SduRepoLocationBuilder.getInstance()
				.build(scaId);
		Configuration scaRepoCfg = null;
		try {
			scaRepoCfg = CfgUtils.openSharedCfgWrite(_cfgHandler, sdu_repo_location
					.getLocation());
		} catch (NameNotFoundException nnfe) {
			return;
		}

		final Configuration sdaIdsCfg = scaRepoCfg
				.getSubConfiguration(SduConstants.CFG_PS_SDAs);
		final String subNames[] = sdaIdsCfg.getAllSubConfigurationNames();
		Configuration sdaIdCfg;
		PropertySheet sdaIdPropSheet;
		String name;
		String vendor;
		SdaId tmpSdaId;
		for (int i = 0; i < subNames.length; i++) {
			sdaIdCfg = sdaIdsCfg.getSubConfiguration(subNames[i]);
			sdaIdPropSheet = sdaIdCfg.getPropertySheetInterface();
			name = (String) sdaIdPropSheet.getPropertyEntry(
					SduConstants.SDU_NAME).getDefault();
			vendor = (String) sdaIdPropSheet.getPropertyEntry(
					SduConstants.SDU_VENDOR).getDefault();
			tmpSdaId = RepositoryComponentsFactory.getInstance().createSdaId(
					name, vendor);

			if (sdaId.equals(tmpSdaId)) {
				if(location.beDebug()){
					DCLog.traceDebug(location, 
							"Delete SDA id [{0}] from the list of the SDAs of SCA [{1}]", 
							new Object[]{sdaId, scaId});
				}
				if (subNames.length > 1) {
					sdaIdCfg.deleteConfiguration();
				} else {
					// if there is no more DCs which belong to the SC, the SC
					// and its archive are deleted
					if(location.beDebug()){
						DCLog.traceDebug(location,
								"SCA [{0}] does not have any SDAs. It will be deleted.",
								new Object[]{scaId});
					}
					scaRepoCfg.deleteConfiguration();
					scaRepoCfg = null;

					final SduFileStorageLocation sduFileStorageLocation = RepositoryComponentsFactory
							.getInstance().createSduStorageLocation(scaId);
					RepoCfgPersistor.getInstance()
							.deleteSduFileStorageLocation(
									sduFileStorageLocation, _cfgHandler);

					Sdu sdu = RepositoryContainer.getDeploymentsContainer()
							.getDeployment(sdu_repo_location);
					CompVersManager compVersManager = CompVersFactory
							.getInstance().createCompVersManager();
					compVersManager.sduUndeployed(sdu);

				}

				break;
			}
		}

		if (_conn != null && scaRepoCfg != null) {
			// modify the SCA update date
			CfgUtils.setDBDate(scaRepoCfg, SduConstants.UPDATED, _conn,
					new StringBuffer("delete SdaId '").append(sdaId).append(
							"' from Sca '").append(scaId).append("'.")
							.toString());

			// remove .ser file as it is fake old one
			if (scaRepoCfg.existsFile(CfgUtils._SER)) {
				scaRepoCfg.deleteFile(CfgUtils._SER);
			}
		}
	}

	void getException() throws Exception {
		if (this.exception != null) {
			throw this.exception;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduRepoLocationVisitor#visit(com.sap.
	 * engine.services.dc.repo.SdaRepoLocation)
	 */
	public void visit(SdaRepoLocation location) {
		this.exception = null;

		try {
			if (location.getConfiguration() != null) {
				deleteCfg(location.getConfiguration());
			} else {
				deleteCfg(this.cfgHandler, location.getLocation());
			}

			final Sda sda = (Sda) location.getSdu();
			final ScaId scaId = sda.getScaId();

			if (scaId != null) {
				// delete the DC from the SC to which belongs
				deleteSdaIdFromSca(scaId, (SdaId) sda.getId(), this.cfgHandler,
						this.conn);
			}
		} catch (Exception e) {
			this.exception = e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduRepoLocationVisitor#visit(com.sap.
	 * engine.services.dc.repo.ScaRepoLocation)
	 */
	public void visit(ScaRepoLocation location) {
		this.exception = null;

		try {
			if (location.getConfiguration() != null) {
				deleteCfg(location.getConfiguration());
			} else {
				deleteCfg(this.cfgHandler, location.getLocation());
			}
		} catch (RepositoryException re) {
			this.exception = re;
		}
	}

	private void deleteCfg(ConfigurationHandler cfgHndl, String fullCfgPath)
			throws RepositoryException {
		try {
			Configuration cfg = CfgUtils.openCfgWrite(cfgHndl, fullCfgPath);
			cfg.deleteConfiguration();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.CANNOT_CFG,
					new String[] { "delete", fullCfgPath }, ce);
		}
	}

	private void deleteCfg(Configuration cfg) throws RepositoryException {
		try {
			cfg.deleteConfiguration();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.CANNOT_CFG,
					new String[] { "delete", cfg.getPath() }, ce);
		}
	}

}
