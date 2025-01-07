package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.IOException;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.SystemProfileManager;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.exception.DCResourceAccessor;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.tools.offlinedeploy.rdb.BootstrapExtractor;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class OfflineDeployProcessor extends AbstractDeployProcessor {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private static final String ENGINE_BOOTSTRAP = "engine-bootstrap";
	private static OfflineDeployProcessor INSTANCE;

	static synchronized OfflineDeployProcessor getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new OfflineDeployProcessor();
		}
		return INSTANCE;
	}

	private OfflineDeployProcessor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.AbstractDeployProcessor#deploy
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem,
	 * com.sap.engine.services.dc.cm.deploy.DeploymentData)
	 */
	public void deploy(DeploymentItem deplItem, DeploymentData deploymentData)
			throws DeploymentException {

		if (DeployWorkflowStrategy.ROLLING.equals(deploymentData
				.getDeployWorkflowStrategy())
				&& deplItem.getVersionStatus().equals(VersionStatus.NEW)) {
			DeploymentException de = new DeploymentException(
					DCResourceAccessor.getInstance().getMessageText(
							DCExceptionConstants.OFFLINE_ROLLING_DEPLOY_ERROR));
			de.setMessageID("ASJ.dpl_dc.003070");
			throw de;
		}
		if (location.beInfo()) {
			traceInfo(location,
					"Development component is an offline one, therefore only its status is changed to [{0}]",
					new Object[] { DeploymentStatus.OFFLINE_ADMITTED });
		}

		deplItem.setDeploymentStatus(DeploymentStatus.OFFLINE_ADMITTED);
		if (deplItem.getSda().getSoftwareType().equals(
				SoftwareType.getSoftwareTypeByName(ENGINE_BOOTSTRAP))) {
			try {
				BootstrapExtractor.extractBootstrapModule(deplItem.getSduFilePath(),
                                                  SystemProfileManager.getSysParamValue(SystemProfileManager.DIR_GLOBAL),
                                                  SystemProfileManager.getSysParamValue(SystemProfileManager.DIR_CLUSTER));
			} catch (IOException e) {
				deplItem.setDeploymentStatus(DeploymentStatus.ABORTED);
				deplItem.addDescription(e);
				final ErrorStrategy errorStrategy = deploymentData
						.getDeploymentErrorStrategy();
				if (errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
					String errMsg = DCLog
							.buildExceptionMessage(
									"ASJ.dpl_dc.001170",
									"An error occurred while deploying the deployment item [{0}].",
									new Object[] { deplItem.getBatchItemId() });
					throw new DeploymentException(errMsg, e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.AbstractDeployProcessor#deploy
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void deploy(CompositeDeploymentItem compositeDeploymentItem,
			DeploymentData deploymentData) {
		if (location.beDebug()) {
			traceDebug(location, 
					"Software component is an offline one, therefore only its status is changed to [{0}]",
					new Object[] { DeploymentStatus.OFFLINE_ADMITTED });
		}

		compositeDeploymentItem
				.setDeploymentStatus(DeploymentStatus.OFFLINE_ADMITTED);
	}

	@Override
	protected void notifyForDeploymentPerformed(
			DeploymentBatchItem deplBatchItem,
			DeployListenersList deployListenersList, long beginTime) {

		// do not generate events for deployment performed here because the
		// deployment is still to be
		// performed

	}

	
}
