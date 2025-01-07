package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.Utils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class UndeployPhaseGetter implements UndeployItemVisitor{

	private static final UndeployPhaseGetter INSTANCE = new UndeployPhaseGetter();
	private UndeployPhase undeployPhase = null;
	private UndeploymentException undeploymentException = null;

	static UndeployPhaseGetter getInstance() {
		return INSTANCE;
	}
	
	private UndeployPhaseGetter() {
	}

	UndeployPhase getPhase(GenericUndeployItem item) throws UndeploymentException {
		undeployPhase = null;
		undeploymentException = null;
		item.accept(this);
		if(undeploymentException != null)
			throw undeploymentException;
		return undeployPhase;

	}

	private SoftwareTypeService getSoftwareTypeService()
			throws UndeploymentException {
		final Server server = ServerFactory.getInstance().createServer();
		final ServerService serverService = server
				.getServerService(ServerFactory.getInstance()
						.createSoftwareTypeRequest());
		if (serverService == null
				|| !(serverService instanceof SoftwareTypeService)) {
			final String errMsg = "Received ServerService for determining online and "
					+ "offline software types that is no SoftwareTypeService.";
			UndeploymentException ue = new UndeploymentException(errMsg);
			ue.setMessageID("ASJ.dpl_dc.003226");
			throw ue;
		}

		final SoftwareTypeService softwareTypeService = (SoftwareTypeService) serverService;
		return softwareTypeService;
	}

	public void visit(UndeployItem undeployItem) {
		try {
			final SoftwareTypeService softwareTypeService = getSoftwareTypeService();
			final Set<SoftwareType> offlineSoftwareTypes = softwareTypeService
					.getOfflineSoftwareTypes();
			final Set<SoftwareType> onlineSoftwareTypes = softwareTypeService
					.getOnlineSoftwareTypes();
			final Set<SoftwareType> postOnlineSoftwareTypes = softwareTypeService
					.getPostOnlineSoftwareTypes();
	
			if (onlineSoftwareTypes.contains(undeployItem.getSda().getSoftwareType())
					|| postOnlineSoftwareTypes.contains(undeployItem.getSda()
							.getSoftwareType())) {
				undeployPhase = UndeployPhase.ONLINE;
			} else if (offlineSoftwareTypes.contains(undeployItem.getSda()
					.getSoftwareType())) {
	
				if (Utils.getOnlineDeploymentOfCoreComponents()) {
					undeployPhase = UndeployPhase.ONLINE;
				} else {
					undeployPhase = UndeployPhase.OFFLINE;
				}
	
			} else {
				final String errMsg = "An error occurred while getting the undeploy phase. "
						+ "The specified deployment item has a Software Type ('"
						+ undeployItem.getSda().getSoftwareType()
						+ "') which could not be "
						+ "recognized neither as 'post-online', 'online' nor as 'offline'.";
				UndeploymentException ue = new UndeploymentException(errMsg);
				ue.setMessageID("ASJ.dpl_dc.003225");
				throw ue;
			}
		} catch (UndeploymentException e){
			undeploymentException = e;
		}
		
	}

	public void visit(ScaUndeployItem undeployItem) {
		undeployPhase = UndeployPhase.UNKNOWN;
	}

}
