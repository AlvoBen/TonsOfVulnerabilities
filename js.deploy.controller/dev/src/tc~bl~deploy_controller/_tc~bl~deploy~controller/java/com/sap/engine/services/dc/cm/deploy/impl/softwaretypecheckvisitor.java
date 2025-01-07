package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.isDebugLoggable;
import static com.sap.engine.services.dc.util.logging.DCLog.logDebugThrowable;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizationException;
import com.sap.engine.services.dc.cm.security.authorize.Authorizer;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SoftwareTypeCheckVisitor extends ValidationVisitor {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private final SoftwareTypeService softwareTypeService;
	private Boolean isAuthorized4Offline = null;
	private AuthorizationException authorized4OfflineException = null;

	SoftwareTypeCheckVisitor(final SoftwareTypeService softwareTypeService) {
		this.softwareTypeService = softwareTypeService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem deploymentItem) {
		deploymentItem.startTimeStatEntry("Check Software type: "
				+ deploymentItem.getSdu().getId(),
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Check software type:" + deploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, SoftwareTypeCheckVisitor.class);
		try {
			final SoftwareType deplItemSoftwareType = deploymentItem.getSda()
					.getSoftwareType();
			if (!this.softwareTypeService.isSupported(deplItemSoftwareType)) {
				deploymentItem
						.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
				deploymentItem.addDescription("The specified software type '"
						+ deplItemSoftwareType + "' is not supported.");
			} else if (softwareTypeService.getOfflineSoftwareTypes().contains(
					deplItemSoftwareType)) {
				isAuthorized4Offline();
				if (isAuthorized4Offline.equals(Boolean.FALSE)) {
					deploymentItem
							.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
					deploymentItem
							.addDescription("The specified software type '"
									+ deplItemSoftwareType
									+ "' is an offline one, but "
									+ " the user is not authorized for offline deployment. "
									+ authorized4OfflineException.getMessage());
				}
			}
		} finally {
			Accounting.endMeasure(tagName);
			deploymentItem.finishTimeStatEntry();
		}
	}

	private void isAuthorized4Offline() {
		if (isAuthorized4Offline == null) {
			try {
				isAuthorized4Offline = Boolean.FALSE;
				final Authorizer authorizer = AuthorizerFactory.getInstance()
						.createAuthorizer();
				authorizer.isAuthorized4Offline();
				isAuthorized4Offline = Boolean.TRUE;
			} catch (AuthorizationException e) {
				authorized4OfflineException = e;
				if (isDebugLoggable()) {
					String msg = DCLog.buildExceptionMessage(
							"ASJ.dpl_dc.001115", "{0}",
							new Object[] { authorized4OfflineException
									.getMessage() });
					logDebugThrowable(location, null, msg, authorized4OfflineException);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem compositeDeploymentItem) {
		compositeDeploymentItem.startTimeStatEntry(
				"Check contained components Software Type",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Check contained components Software Type:" + compositeDeploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, SoftwareTypeCheckVisitor.class);
		try {
			final Collection deploymentItems = compositeDeploymentItem
					.getDeploymentItems();
			for (Iterator iter = deploymentItems.iterator(); iter.hasNext();) {
				final DeploymentItem deploymentItem = (DeploymentItem) iter
						.next();
				if (DeploymentStatus.ADMITTED.equals(deploymentItem
						.getDeploymentStatus())) {
					visit(deploymentItem);
				}
			}
			if (!isThereAvailabaleItems(compositeDeploymentItem)) {
				compositeDeploymentItem
						.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
			}
		} finally {
			Accounting.endMeasure(tagName);
			compositeDeploymentItem.finishTimeStatEntry();
		}
	}
}
